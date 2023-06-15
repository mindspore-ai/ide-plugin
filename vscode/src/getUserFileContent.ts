import { TextDecoder } from 'util';
import * as vscode from 'vscode';
import { marked } from 'marked';
import { scanAPI } from './scanAPI';
import {apiScan} from './scanner';
import path = require('path');
let panelList: vscode.WebviewPanel[] = [];
export async function getUserFileContent(context: vscode.ExtensionContext) {
    vscode.commands.registerCommand('mindspore.scanLocalFile', async function (uris: vscode.Uri | vscode.Uri[], label?: string) {
        let showName;
        if (uris instanceof vscode.Uri) {
            showName = path.basename(uris.fsPath);
            uris = [uris];
        } else {
            showName = label;
        }

        let apiList :string[] = [];
        for (let uri of uris) {
            let codeFile = await processUriList(uri);
            apiList.push(...apiScan(codeFile));
            
        }
        
        let htmlTable = await scanContentToWebview(apiList);
        let viewColumn = vscode.ViewColumn.Beside;
        if (panelList.length > 0) {
            viewColumn = panelList[panelList.length - 1].viewColumn ??vscode.ViewColumn.Beside;
        }
        let panel = vscode.window.createWebviewPanel(
                'markdownTable', // viewType
                showName as string, // title
                viewColumn, // show in new column
                {} // webview options
            );
        panel.title = showName as string;
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
                <h3>可以转化的PyTorch API</h3>
                ${htmlTable[0]}
                <h3>可能是torch.Tensor API的结果</h3>
                ${htmlTable[1]}
                <h3>暂未提供直接映射关系的PyTorch API</h3>
                ${htmlTable[2]}
            </body>
            </html>
        `;
        panel.onDidDispose(() => { panelList.splice(panelList.indexOf(panel), 1);
        });
        panelList.push(panel);
    })
}

//扫描文本信息，转化为MarkDown Table
async function scanContentToWebview(apiList: string[]){
    let apiTables = scanAPI(apiList);
    let htmlTable:string[] = [];
    for (let element of apiTables){
        htmlTable.push(await marked(element));
    };

    return htmlTable;    
}

// 逐一解析所点击的文件夹中所有文件
async function processUriList(uri: vscode.Uri): Promise<string> {
    return await scanEachFile(uri);
}

// 扫描每个文件存成String
async function scanEachFile(uri: vscode.Uri): Promise<string> {
    let data = await vscode.workspace.fs.readFile(uri);
    let fileContent = new TextDecoder().decode(data);
    return fileContent;
}