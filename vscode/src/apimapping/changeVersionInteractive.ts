import * as vscode from 'vscode';
import { changeVersion, getVersion } from './apiMappingData';

export enum StatusBarInput{
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
    if (!extension){
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
    versionString = versions.map(String);

    vscode.commands.registerCommand('extension.openInputOptions', openInputOptions);

    statusBarItem.text = `MindSpore ${currentVersion}`;
    statusBarItem.tooltip = "点击此处切换版本";
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
        prompt: '请输入自定义内容'
    }).then(async (inputValue) => {
        if (inputValue) {
            updateStatusBarItem(StatusBarInput.waiting, inputValue);
            await changeVersion(inputValue);
            let currentValue = getVersion();
            updateStatusBarItem(StatusBarInput.good, currentValue);
            versions.push(+inputValue);
            sortVersion(versions);
        }
    });
}

function updateStatusBarItem(statusBarInputValue: StatusBarInput, selection: string) {
    switch (statusBarInputValue) {
        case StatusBarInput.good:
            statusBarItem.text = `MindSpore ${selection}`;
            statusBarItem.tooltip = "点击此处切换版本";
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
    versions = version.sort((a,b) => b - a);
    versionString = versions.map(String);
}

