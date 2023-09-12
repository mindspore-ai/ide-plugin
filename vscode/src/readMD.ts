import fs = require("fs");
import path = require("path");
import * as scanPlatform from './scanPlatform';
interface TableData {
    header: string[];
    rows: string[][];
  }
  
function readMarkdownTable(filePath: string) {
  try {
    const fileContent = fs.readFileSync(path.resolve(__dirname, filePath), 'utf-8');
    const rawTables = extractTables(fileContent);

      // 解析每个表格
      const tableDataArray: TableData[] = rawTables.map(rawTable => parseTable(rawTable));
      return tableDataArray;
  } catch (error: any) {
      console.error(`Failed to read file: ${error.message}`);
      return null;
  }
}

export function markdownTableToJson(filePath: string): any[] | null {
    let tableDatas = readMarkdownTable(filePath);

    let resultJson: any[] = [];
    if (tableDatas){
        tableDatas = tableDatas.slice(1);
        for (let tableData of tableDatas) {
            const headerOperator = tableData.header[0];
            const headerMindspore = tableData.header[1];
            const headerDiff = tableData.header[2];
            tableData.header[0] = headerOperator;
            tableData.header[1] = "operatorURL";
            tableData.header[2] = "operator1word";
            tableData.header[3] = "operator2word";
            tableData.header[4] = "operator3word";
            tableData.header[5] = "operator4word";
            tableData.header[6] = headerMindspore;
            tableData.header[7] = "mindsporeURL";
            tableData.header[8] = "mindspore1word";
            tableData.header[9] = "mindspore2word";
            tableData.header[10] = "mindspore3word";
            tableData.header[11] = "mindspore4word";
            tableData.header[12] = "remark";
            tableData.header[13] = "diffURL";
            tableData.header[14] = "version";
            tableData.header[15] = "platform";

            const jsonData = tableData.rows.map((row) => {
                const rowObject: { [key: string]: string } = {};
                row.forEach((cell, index) => {
                    const pattern = /\[(.*?)\]/g;
                    const tempNameMatch = pattern.exec(cell);
                    const tempName = (tempNameMatch)? tempNameMatch[1] : cell;
                    const parts = tempName.split(".");
                    const tempNameSplit = [
                        tempName,                 // "This.is.a.test"
                        parts.slice(1).join("."), // "is.a.test"
                        parts.slice(2).join("."), // "a.test"
                        parts.slice(3).join("."), // "test"
                    ];

                    const patternURL = /\((.*?)\)/g;
                    const tempURLMatch = patternURL.exec(cell);
                    const tempURL = (tempURLMatch)? tempURLMatch[1] : "";
                    rowObject[tableData.header[(index+1)*6-6]] = tempName;
                    rowObject[tableData.header[(index+1)*6-5]] = tempURL;
                    rowObject[tableData.header[(index+1)*6-4]] = tempNameSplit[0];
                    rowObject[tableData.header[(index+1)*6-3]] = tempNameSplit[1];
                    rowObject[tableData.header[(index+1)*6-2]] = tempNameSplit[2];
                    rowObject[tableData.header[(index+1)*6-1]] = tempNameSplit[3];
                    rowObject[tableData.header[14]] = headerOperator;

                });
                return rowObject;
            });
            resultJson.push(...jsonData);
        }
        return resultJson;
    }
    return null;
}

function extractTables(fileContent: string): string[][] {
    const lines = fileContent.split('\n');
    let currentTable: string[] = [];
    let tables: string[][] = [];

    for (let line of lines) {
        if (line.trim().startsWith('|') || line.trim().endsWith('|')) {
            currentTable.push(line.trim());
        } else {
            if (currentTable.length > 0) {
                tables.push(currentTable);
                currentTable = [];
            }
        }
    }

    // 如果文件结尾处有一个表格，那么需要手动将它添加到列表中
    if (currentTable.length > 0) {
        tables.push(currentTable);
    }

    return tables;
}

function parseTable(table: string[]): TableData {
    const header = table[0].split('|').filter(cell => cell.trim() !== '').map(cell => cell.trim());  // 获取表头
    const rows = table.slice(2).map(row => row.split('|').filter(cell => cell.trim() !== '').map(cell => cell.trim()));  // 获取表格内容
    return { header, rows };
}

export function searchJson(jsonData: any[], searchWords: string[]): any[] {
    const searchRegex = new RegExp(`^\(${searchWords.join('|')})`, 'i');
    const filteredJson = jsonData.filter((obj) => {
      for (const key in obj) {
        if (searchRegex.test(obj[key])) {
          //obj.key 是header
          //
          return true;
        }
      }
      return false;
    });
    return filteredJson;
  }
  
export function getResult(filepath: string, searchWords: string[]){
    let jsonData = markdownTableToJson(filepath)
    if (jsonData){
      const result = searchJson(jsonData, searchWords)
      return result;
    }
    return null;
  }