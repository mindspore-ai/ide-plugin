import { TextDecoder } from 'util';
import * as vscode from 'vscode';
import { marked } from 'marked';
import {mapAPI} from './mapper';
import {scanAPI} from './scanner';
import path = require('path');
import { homedir } from 'os';
import { join } from 'path';
import * as fs from 'fs';
import * as ApiScanMapping from './apiMappingData';

let panelList: vscode.WebviewPanel[] = [];
let extensionContext : vscode.ExtensionContext;
export async function init(context: vscode.ExtensionContext) {
    vscode.commands.registerCommand('mindspore.scanLocalFile', apiScanHandler);
    extensionContext = context;
    ApiScanMapping.init();
    
}
async function apiScanHandler(uris: vscode.Uri | vscode.Uri[], label?: string, ) {
    let showName: string | undefined;
    if (uris instanceof vscode.Uri) {
        showName = path.basename(uris.fsPath);
        uris = [uris];
    } else {
        showName = label;
    }

    let apiList :string[] = [];
    for (let uri of uris) {
        let codeFile = await processUriList(uri);
        apiList.push(...scanAPI(codeFile));
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
            {
                enableScripts:true
            } // webview options
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
                    width: 100%;
                    word-break: break-word;
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
            <a style="display: block;margin-bottom: 1rem;" href="https://www.mindspore.cn/docs/zh-CN/master/migration_guide/analysis_and_preparation.html#%E7%BC%BA%E5%A4%B1api%E5%A4%84%E7%90%86%E7%AD%96%E7%95%A5">请阅：缺失API处理策略</a>
            ${htmlTable[2]}
            <button id="button">导出成CSV</button>

            <script>
                const vscode = acquireVsCodeApi();
                document.getElementById('button').addEventListener('click', () => {
                    vscode.postMessage({
                        command: 'exportCSV'
                    })
                });
            </script>
        </body>
        </html>
    `;
    panel.webview.onDidReceiveMessage(
        message => {
            switch (message.command) {
                case 'exportCSV':
                    writeCSV(htmlTable, showName as string);
                    return;
            }
        },
        undefined,
        extensionContext.subscriptions
    );
    panel.onDidDispose(() => { panelList.splice(panelList.indexOf(panel), 1);
    });
    panelList.push(panel);
}
//扫描文本信息，转化为MarkDown Table
async function scanContentToWebview(apiList: string[]){
    let apiTables = await mapAPI(apiList);
    let htmlTable:string[] = [];
    for (let element of apiTables){
        htmlTable.push(marked(element));
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

// 解析 HTML 表格
function parseHtmlTable(htmlTable: string): string[][] {
    const rows = htmlTable.match(/<tr>.*?<\/tr>/gs) || [];
    return rows.map(row => {
        const cells = row.match(/<th>.*?<\/th>|<td>.*?<\/td>/gs) || [];
        return cells.map(cell => {
            const text = cell.replace(/<.*?>/g, '').trim();
            const link = (cell.match(/<a href="(.*?)">/)||[])[1];
            return link ? `=HYPERLINK("${link}", "${text}")` : text;
        });
    });
}

// 转换数据为 CSV 格式的字符串
function toCsv(data: string[][]): string {
    return data.map(row => {
        return row.map(cell => {
            const containsSpecialCharacters = /[",\n]/.test(cell);
            if (containsSpecialCharacters) {
                // 如果单元格包含特殊字符（逗号，引号或换行符），则用双引号括起来，并转义双引号
                return '"' + cell.replace(/"/g, '""') + '"';
            } else {
                return cell;
            }
        }).join(',');
    }).join('\n');
}

function writeCSV(htmlTable: string[], currentFileName: string) {
    // Prepare data as before...
    const data1 = parseHtmlTable(htmlTable[0]);
    const data2 = parseHtmlTable(htmlTable[1]);
    const data3 = parseHtmlTable(htmlTable[2]);

    const csvData = [
        '可以转化的PyTorch API',
        toCsv(data1),
        '',
        '可能是torch.Tensor API的结果',
        toCsv(data2),
        '',
        '暂未提供直接映射关系的PyTorch API',
        toCsv(data3),
    ].join('\n');

    const now = new Date();
    const timestamp = `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}-${now.getHours().toString().padStart(2, '0')}-${now.getMinutes().toString().padStart(2, '0')}-${now.getSeconds().toString().padStart(2, '0')}`;
    const fileNameWithoutExtension = path.parse(currentFileName).name;
    const filename = join(homedir(), `${fileNameWithoutExtension}-mapping-${timestamp}.csv`);

    // Open save dialog for user to select location and specify filename
    vscode.window.showSaveDialog({

        defaultUri: vscode.Uri.file(filename), // Set default file name and path
        filters: {
            'CSV Files': ['csv'],
        }
    }).then((fileUri: any) => {
        if (fileUri) {
            fs.writeFile(fileUri.fsPath, '\ufeff' + csvData, 'utf8', (err) => {
                if (err) {
                    vscode.window.showErrorMessage('导出文件失败: ' + err.message);
                } else {
                    vscode.window.showInformationMessage('导出文件成功!');
                }
            });
        }
    });
}
