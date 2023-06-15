import { TreeDataProvider, TreeItem, TreeItemCollapsibleState, ProviderResult, window, EventEmitter, Event } from "vscode";
import * as vscode from "vscode";

export class MyTreeData implements TreeDataProvider<MyTreeItem> {
    constructor() {
        vscode.commands.registerCommand('mindspore.refreshEntry', () => this.refresh());
        vscode.commands.registerCommand('mindspore.content', item => this.onItemClicked(item));
        let fileSystemWatcher = vscode.workspace.createFileSystemWatcher("**/*.py");
        fileSystemWatcher.onDidCreate(() => (this.refresh()));
        fileSystemWatcher.onDidDelete(() => (this.refresh()));
    }

    private _onDidChangeTreeData = new EventEmitter<MyTreeItem | undefined | null | void>();

    // refresh the treeview
    refresh(): void {
        this._onDidChangeTreeData.fire();
    }

    onDidChangeTreeData = this._onDidChangeTreeData.event;

    // action after clicking on item
    onItemClicked(item: MyTreeItem): void {
        item.getDescendants();
    }

    getTreeItem(element: MyTreeItem): MyTreeItem | Thenable<MyTreeItem> {
        if (element.label.startsWith("(WorkSpace)")) {
            return element;
        }
        element.command = { command: 'mindspore.scanLocalFile', title: 'getContent', arguments: [element.getDescendants(), element.label]};
        return element;
    }

    getChildren(element?: MyTreeItem | undefined): ProviderResult<MyTreeItem[]> {

        if (!element) {
            const rootPaths = vscode.workspace.workspaceFolders && vscode.workspace.workspaceFolders.length > 0 
            ? vscode.workspace.workspaceFolders.flatMap((value) => { return {path: value.uri.fsPath, name: value.name}})
            : [];
            const name = vscode.workspace.name??"";
            if (rootPaths.length <= 0) {
                window.showInformationMessage('No file in empty directory');
                return Promise.resolve([]);
            }    
            let newRoot = new MyTreeItem("(WorkSpace)" + name, TreeItemCollapsibleState.Expanded);
            rootPaths.forEach((value) => {
                let newFolder =new MyTreeItem(value.name, TreeItemCollapsibleState.Expanded, vscode.Uri.file(value.path));
                newRoot.addChildren(newFolder)});
            return Promise.resolve([newRoot]);
        } else if(!element.uri) {
            return Promise.resolve(element.init());
        }
        else {
            return Promise.resolve(element.children);
        }
    }

}

export class MyTreeItem extends TreeItem {
    children: MyTreeItem[] = [];
    constructor(
        public readonly label: string,
        public readonly collapsibleState: TreeItemCollapsibleState,
        public readonly uri?: vscode.Uri,
        public readonly isFile: boolean = false
    ){
        super(label, collapsibleState);
    }

    async init(){
        for (let child of this.children) {
            await child.refreshDescendant();
        }
        return this.children;
    }
    async refreshDescendant() {
        if (!this.uri) {
            return [];
        }
        let itemUri = this.uri;
        let fsReadDir = await vscode.workspace.fs.readDirectory(itemUri);
        for (const [fileName, type] of fsReadDir) {
            if (type === vscode.FileType.Directory) {
                let child = new MyTreeItem(fileName, TreeItemCollapsibleState.Expanded, vscode.Uri.joinPath(itemUri,fileName));
                if((await child.refreshDescendant()).length > 0) {
                    this.children.push(child);
                }
            } else {
                if (fileName.endsWith(".py")) {
                    let child = new MyTreeItem(fileName, TreeItemCollapsibleState.None, vscode.Uri.joinPath(itemUri,fileName), true);
                    child.resourceUri= child.uri;
                    this.children.push(child);
                }
            }
        }
        return this.children;
    }

    addChildren(item: MyTreeItem) {
        this.children.push(item);
    }

    async getChildren() {
        if (this.children.length <= 0) {
            await this.refreshDescendant();
        }
        return this.children;
    }

    getDescendants() {
        let result: vscode.Uri[] = [];
        if (this.isFile && this.uri) {
            result.push(this.uri);
            return result;
        }
        for (let child of this.children) {
            result.push(...child.getDescendants());
        }
        return result;
    }
 }