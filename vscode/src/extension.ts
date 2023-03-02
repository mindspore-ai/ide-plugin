// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import { homedir } from 'os';
import { join } from 'path';
import * as vscode from 'vscode';
import {fsExistsSync} from "./fileUtil";
import * as fs from "fs";
import { ADDITIONAL_CHARACTERS, WhitzardProvider } from './provider';
import { logger } from './log/log4js';


let whitzardCompletionProvider: WhitzardProvider;

function init(){
	const root = join(homedir(), ".mindspore");
	if (!fsExistsSync(root)){
		fs.mkdirSync(root);
	}
}

export async function activate(context: vscode.ExtensionContext) {
	init();
	let versionNumber = context.extension.packageJSON.version;
	context.subscriptions.push(vscode.commands.registerCommand('getContext', () => versionNumber));

	whitzardCompletionProvider = await WhitzardProvider.getInstance();
	const pythonProvider = vscode.languages.registerCompletionItemProvider([{language: 'python'}],whitzardCompletionProvider, ...ADDITIONAL_CHARACTERS);
	context.subscriptions.push(pythonProvider);

}

// This method is called when your extension is deactivated
export function deactivate() {
	logger.info("decativate start!");
	return whitzardCompletionProvider?.decativate();
	
}
