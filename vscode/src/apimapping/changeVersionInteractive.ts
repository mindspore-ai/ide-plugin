import * as vscode from 'vscode';
import { changeVersion } from './apiMappingData';

export async function init(context: vscode.ExtensionContext) {
    vscode.commands.registerCommand('extension.openInputOptions', openInputOptions);
    let statusBarItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Left);
    statusBarItem.text = "点击此处切换版本";
    statusBarItem.command = 'extension.openInputOptions';
    statusBarItem.show();

    context.subscriptions.push(statusBarItem);
}


export function openInputOptions() {
    const versionOptions = generateVersionOptions();
    vscode.window.showQuickPick(versionOptions.concat('自定义输入...'), {
        placeHolder: '请选择一个版本或输入自定义版本'
    }).then((selection) => {
        if (!selection) {return;};

        // 如果用户选择了'自定义输入...'，那么展示输入框
        if (selection === '自定义输入...') {
            showCustomInputBox();
        } else {
            // 处理用户选择的内容
            changeVersion(selection);
        }
    });
}

export function showCustomInputBox() {
    vscode.window.showInputBox({
        prompt: '请输入自定义内容'
    }).then((inputValue) => {
        if (inputValue) {
            changeVersion(inputValue);
        }
    });
}

export function generateVersionOptions() {
    const extension = vscode.extensions.getExtension('MindSpore.mindspore-dev-toolkit'); 
    if (!extension) {
        return [];
    }
    
    const currentVersion = extension.packageJSON.version.split('.');
    const major = parseInt(currentVersion[0]);
    const minor = parseInt(currentVersion[1]);

    const versions = [];

    for (let m = major; m >= 2; m--) {
        const startMinor = m === major ? minor : 9;
        for (let i = startMinor; i >= 0; i--) {
            versions.push(`${m}.${i}`);
        }
    }

    versions.push('1.9');

    return versions;
}

