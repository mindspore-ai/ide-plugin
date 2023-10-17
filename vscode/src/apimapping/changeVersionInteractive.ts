import * as vscode from 'vscode';
import {changeVersion, getVersion} from './apiMappingData';

export enum StatusBarInput {
    good,       //表示不在切换版本
    waiting,     //表示正在切换版本
}

let statusBarItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Left);
const extension = vscode.extensions.getExtension('MindSpore.mindspore-dev-toolkit');
let currentVersion: string;
let versions: any[] = [];
let versionString: string[] = [];
let major: number;
let minor: number;

export async function init(context: vscode.ExtensionContext) {
    if (!extension) {
        currentVersion = '未获取到版本信息';
    } else {
        let getVersion = extension.packageJSON.version.split('.');
        major = parseInt(getVersion[0]);
        minor = parseInt(getVersion[1]);
        currentVersion = `${major}.${minor}`;
    }

    for (let m = major; m >= 2; m--) {
        const startMinor = m === major ? minor : 9;
        for (let i = startMinor; i >= 0; i--) {
            versions.push(`${m}.${i}`);
        }
    }

    versions.push('1.9');
    versions.push('master');

    versionString = versions.map(String);

    vscode.commands.registerCommand('extension.openInputOptions', openInputOptions);

    statusBarItem.text = `API映射数据版本：MindSpore ${currentVersion}`;
    statusBarItem.tooltip = "点击此处切换API映射数据版本";
    statusBarItem.command = 'extension.openInputOptions';
    statusBarItem.show();

    context.subscriptions.push(statusBarItem);
}


export function openInputOptions() {
    vscode.window.showQuickPick(versionString.concat('自定义输入...'), {
        placeHolder: '请选择一个版本或输入自定义版本'
    }).then(async (selection) => {
        if (!selection) return;

        // 如果用户选择了'自定义输入...'，那么展示输入框
        if (selection === '自定义输入...') {
            showCustomInputBox();
        } else {
            // 处理用户选择的内容
            updateStatusBarItem(StatusBarInput.waiting, selection.toString());
            await changeVersion(selection.toString());
            selection = getVersion();
            updateStatusBarItem(StatusBarInput.good, selection.toString());
        }
    });
}

export function showCustomInputBox() {
    vscode.window.showInputBox({
        prompt: '请输入自定义内容',
        validateInput: (value) => {
            if (/^\d+(\.\d+){1,2}$/.test(value)) {
                return null
            } else {
                return "格式错误，格式应形如2.1或2.1.0"
            }
        }
    }).then(async (inputValue) => {
        if (inputValue) {
            if (/^\d+(\.\d+){2}$/.test(inputValue)) {
                let inputSplit = inputValue.split('.');
                let one = parseInt(inputSplit[0]);
                let two = parseInt(inputSplit[1]);
                inputValue = `${one}.${two}`
            }
            updateStatusBarItem(StatusBarInput.waiting, inputValue);
            let isChangeSuccess = await changeVersion(inputValue);
            let currentValue = getVersion();
            updateStatusBarItem(StatusBarInput.good, currentValue);
            if (isChangeSuccess && !versions.some(v => v === inputValue)) {
                versions.push(inputValue);
                sortVersion(versions);
            }
        }
    });
}

function updateStatusBarItem(statusBarInputValue: StatusBarInput, selection: string) {
    switch (statusBarInputValue) {
        case StatusBarInput.good:
            statusBarItem.text = `API映射数据版本：MindSpore ${selection}`;
            statusBarItem.tooltip = "点击此处切换API映射数据版本";
            statusBarItem.command = 'extension.openInputOptions';
            statusBarItem.show();
            break;
        case StatusBarInput.waiting:
            statusBarItem.text = `$(sync~spin)正在切换版本`;
            statusBarItem.tooltip = "正在切换版本";
            statusBarItem.command = undefined;
            statusBarItem.show();
            break;
    }
}

function sortVersion(version: any[]) {
    versions = version.sort(compareStringsAsNumbers);
    versionString = versions.map(String);
}

function compareStringsAsNumbers(a: string, b: string): number {
    const [majorA, minorA] = a.split('.').map(part => parseInt(part, 10));
    const [majorB, minorB] = b.split('.').map(part => parseInt(part, 10));

    // 比较主版本
    if (Number.isNaN(majorA) || majorA < majorB) {
        return 1;
    } else if (Number.isNaN(majorB) || majorA > majorB) {
        return -1;
    }

    // 如果主版本相同，比较子版本
    if (minorA < minorB) {
        return 1;
    } else if (minorA > minorB) {
        return -1;
    }

    return 0;
}