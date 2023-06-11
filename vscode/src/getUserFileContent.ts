import { TextDecoder } from 'util';
import * as vscode from 'vscode';
import { marked } from 'marked';
import { scanAPI } from './scanAPI';
import path = require('path');

export async function getUserFileContent(context: vscode.ExtensionContext) {
    vscode.commands.registerCommand('mindspore.scanLocalFile', async function (uris: vscode.Uri | vscode.Uri[], label?: string) {
        let showName;
        if (uris instanceof vscode.Uri) {
            showName = path.basename(uris.fsPath);
            uris = [uris];
        } else {
            showName = label;
        }
        let allFileContent = await processUriList(uris);
        
        let htmlTable = scanContentToWebview(allFileContent);
        const panel = vscode.window.createWebviewPanel(
            'markdownTable', // viewType
            showName as string, // title
            vscode.ViewColumn.Beside, // show in new column
            {} // webview options
        );
        panel.webview.html = `
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Markdown Table</title>
                <style>
                    table {
                        border-collapse: collapse;
                    }
                    th, td {
                        border: 1px solid black;
                        padding: 8px;
                        text-align: left;
                    }
                </style>
            </head>
            <body>
                ${htmlTable}
            </body>
            </html>
        `;
    })	
    
}

//扫描文本信息，转化为MarkDown Table
function scanContentToWebview(fileContent: string){
    let apiTable = scanAPI(fileContent);

    const htmlTable = marked(apiTable);

    return htmlTable;    
}

// 逐一解析所点击的文件夹中所有文件
async function processUriList(uris: vscode.Uri[]): Promise<string> {
    let allFileContents = "";

    for (let uri of uris) {
        let fileContent = await scanEachFile(uri);
        allFileContents += fileContent;
    }

    return allFileContents;
}

// 扫描每个文件存成String
async function scanEachFile(uri: vscode.Uri): Promise<string> {
    let data = await vscode.workspace.fs.readFile(uri);
    let fileContent = new TextDecoder().decode(data);
    return fileContent;
}