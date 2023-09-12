// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import { homedir } from 'os';
import { join } from 'path';
import * as vscode from 'vscode';
import {fsExistsSync} from "./fileUtil";
import * as fs from "fs";
import { ADDITIONAL_CHARACTERS, WhitzardProvider } from './provider';
import { logger } from './log/log4js';
import { getUserFileContent } from './getUserFileContent';
import { MyTreeData } from './myTreeData';
import * as scanner from './scanner';
import { OpenWebProvider } from './SearchWebveiwProvider';
let whitzardCompletionProvider: WhitzardProvider;


async function init(){
	const root = join(homedir(), ".mindspore");
	if (!fsExistsSync(root)){
		fs.mkdirSync(root);
	}
	await scanner.init();
}

export async function activate(context: vscode.ExtensionContext) {
	await init();
	getUserFileContent(context);
	let versionNumber = context.extension.packageJSON.version;
	context.subscriptions.push(vscode.commands.registerCommand('getContext', () => versionNumber));

	WhitzardProvider.getInstance().then(result => {
		whitzardCompletionProvider = result;
		const pythonProvider = vscode.languages.registerCompletionItemProvider([{language: 'python'}],whitzardCompletionProvider, ...ADDITIONAL_CHARACTERS);
		context.subscriptions.push(pythonProvider);
		vscode.window.showInformationMessage("MindSpore Dev Toolkit智能补全启动成功！")

	});

	const provider = new OpenWebProvider(context.extensionUri);
	context.subscriptions.push(
		vscode.window.registerWebviewViewProvider(OpenWebProvider.viewType, provider));

	// register treeview
	vscode.window.registerTreeDataProvider('MindSporeTreeView', new MyTreeData());
}

// This method is called when your extension is deactivated
export function deactivate() {
	logger.info("deativate start!");
	return whitzardCompletionProvider?.decativate();

}

