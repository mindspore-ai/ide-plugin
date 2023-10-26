import fs = require("fs");
import path = require("path");
import { getJsonData } from "./apiMappingData";

interface TableData {
    header: string[];
    rows: string[][];
  }
  
function readMarkdownTable(filePath: string):[TableData[], boolean] {
  let isSlice = false;
  try {
    const fileContent = fs.readFileSync(path.resolve(__dirname, filePath), 'utf-8');
    if (fileContent.includes("通用差异参数表")){
      isSlice = true;
    }
    const rawTables = extractTables(fileContent);

      // 解析每个表格
      const tableDataArray: TableData[] = rawTables.map(rawTable => parseTable(rawTable));
      return [tableDataArray, isSlice];
  } catch (error: any) {
      console.error(`Failed to read file: ${error.message}`);
      return [[], false];
  }
}

export function markdownTableToJson(filePath: string): any[] | undefined {
    let [tableData, isSlice] = readMarkdownTable(filePath);

    let resultJson: any[] = [];
    if (tableData.length > 0){
        if (isSlice){
          tableData = tableData.slice(1);
        }
        for (let record of tableData) {
            const headerOperator = record.header[0];
            const headerMindspore = record.header[1];
            const headerDiff = record.header[2];
            record.header[0] = headerOperator;
            record.header[1] = "operatorURL";
            record.header[2] = "operator1word";
            record.header[3] = "operator2word";
            record.header[4] = "operator3word";
            record.header[5] = "operator4word";
            record.header[6] = headerMindspore;
            record.header[7] = "mindsporeURL";
            record.header[8] = "mindspore1word";
            record.header[9] = "mindspore2word";
            record.header[10] = "mindspore3word";
            record.header[11] = "mindspore4word";
            record.header[12] = "remark";
            record.header[13] = "diffURL";
            record.header[14] = "version";
            record.header[15] = "platform";

            const jsonData = record.rows.map((row) => {
                
                const rowObject: { [key: string]: string } = {};
                // const rowObject2: apiRecord = {}
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
                    rowObject[record.header[(index+1)*6-6]] = tempName;
                    rowObject[record.header[(index+1)*6-5]] = tempURL;
                    rowObject[record.header[(index+1)*6-4]] = tempNameSplit[0];
                    rowObject[record.header[(index+1)*6-3]] = tempNameSplit[1];
                    rowObject[record.header[(index+1)*6-2]] = tempNameSplit[2];
                    rowObject[record.header[(index+1)*6-1]] = tempNameSplit[3];
                    rowObject[record.header[14]] = headerOperator;

                });
                return rowObject;
            });
            resultJson.push(...jsonData);
        }
        return resultJson;
    }
    return undefined;
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
  
export function getResult(searchWords: string[]){
    let jsonData = getJsonData();
    if (jsonData){
      const result = searchJson(jsonData, searchWords);
      return result;
    }
    return null;
  }