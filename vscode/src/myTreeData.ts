import { TreeDataProvider, TreeItem, TreeItemCollapsibleState, ProviderResult, window, EventEmitter, Event } from "vscode";
import * as vscode from "vscode";

export class MyTreeData implements TreeDataProvider<MyTreeItem> {
    constructor(private rootPath: string | undefined) {
        vscode.commands.registerCommand('TreeViewTest_One.refreshEntry', () => this.refresh());
        vscode.commands.registerCommand('MyTreeItem.content', item => this.onItemClicked(item));
    }

    private _onDidChangeTreeData: EventEmitter<MyTreeItem | undefined | null | void> = new EventEmitter<MyTreeItem | undefined | null | void>();

    // refresh the treeview
    refresh(): void {
        this._onDidChangeTreeData.fire();
    }

    // action after clicking on item
    onItemClicked(item: MyTreeItem): void {
        item.getDescendants();
    }

    getTreeItem(element: MyTreeItem): MyTreeItem | Thenable<MyTreeItem> {
        element.command = { command: 'mindspore.scanLocalFile', title: 'getContent', arguments: [element.getDescendants(), element.label]};
        return element;
    }

    getChildren(element?: MyTreeItem | undefined): ProviderResult<MyTreeItem[]> {
        if (!this.rootPath) {
            window.showInformationMessage('No file in empty directory');
            return Promise.resolve([]);
        }

        if (!element) {
            let newRoot = new MyTreeItem('', vscode.Uri.file(this.rootPath), TreeItemCollapsibleState.Expanded);
            return Promise.resolve(newRoot.refreshDescendant(this.rootPath));
        } else {
            return Promise.resolve(element.getChildren(vscode.Uri.joinPath(element.parentPath, element.label).fsPath));
        }
    }

}

export class MyTreeItem extends TreeItem {
    children: MyTreeItem[] = [];
    uri: vscode.Uri;
    constructor(
        public readonly label: string,
        public readonly parentPath: vscode.Uri,
        public readonly collapsibleState: TreeItemCollapsibleState,
        public readonly isFile: boolean = false
    ){
        super(label, collapsibleState);
        this.uri = vscode.Uri.joinPath(parentPath, label);
    }

    async refreshDescendant(itemPath: string) {
        let itemUri = vscode.Uri.file(itemPath);
        let fsReadDir = await vscode.workspace.fs.readDirectory(itemUri);
        for (const [fileName, type] of fsReadDir) {
            let filePath = vscode.Uri.joinPath(itemUri,fileName).fsPath;
            if (type === vscode.FileType.Directory) {
                let child = new MyTreeItem(fileName, itemUri, TreeItemCollapsibleState.Expanded);
                if((await child.refreshDescendant(filePath)).length > 0) {
                    this.children.push(child);
                }
            } else {
                if (fileName.endsWith(".ts")) {
                    let child = new MyTreeItem(fileName, itemUri, TreeItemCollapsibleState.None, true);
                    this.children.push(child);
                }
            }
        }
        return this.children;
    }

    getChildren(itemPath: string): MyTreeItem[] {
        return this.children;
    }

    getDescendants() {
        let result: vscode.Uri[] = [];
        if (this.isFile) {
            result.push(this.uri);
            return result;
        }
        for (let child of this.children) {
            result.push(...child.getDescendants());
        }
        return result;
    }
 }