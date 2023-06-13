import fs = require("fs");

interface TableData {
    header: string[];
    rows: string[][];
  }
  
function readMarkdownTable(filePath: string): TableData | null {
  try {
    const fileContent = fs.readFileSync(filePath, 'utf-8');
    const lines = fileContent.split('\n');
    const tableStart = lines.findIndex((line) => line.trim().startsWith('|'));
    if (tableStart >= 0) {
      const header = lines[tableStart].split('|').map((cell) => cell.trim()).filter(Boolean);
      const rows = lines.slice(tableStart + 2).filter((line) => line.trim().startsWith('|'))
        .map((line) => line.split('|').map((cell) => cell.trim()).filter(Boolean));
      return { header, rows };
    }
    return null;
  } catch (error: any) {
    console.error(`Failed to read file: ${error.message}`);
    return null;
  }
}

export function markdownTableToJson(filePath: string): any[] | null {
  const tableData = readMarkdownTable(filePath);
  if (tableData) {
      const headerOperator = tableData.header[0]
      const headerMindspore = tableData.header[1]
      const headerDiff = tableData.header[2]
      tableData.header[0] = headerOperator
      tableData.header[1] = "operatorURL"
      tableData.header[2] = "operator1word"
      tableData.header[3] = "operator2word"
      tableData.header[4] = "operator3word"
      tableData.header[5] = "operator4word"
      tableData.header[6] = headerMindspore
      tableData.header[7] = "mindsporeURL"
      tableData.header[8] = "mindspore1word"
      tableData.header[9] = "mindspore2word"
      tableData.header[10] = "mindspore3word"
      tableData.header[11] = "mindspore4word"
      tableData.header[12] = "remark"
      tableData.header[13] = "diffURL"
      tableData.header[14] = "version"

    const jsonData = tableData.rows.map((row) => {
      const rowObject: { [key: string]: string } = {};
      row.forEach((cell, index) => {
          const pattern = /\[(.*?)\]/g;
        const tempNameMatch = pattern.exec(cell);
        const tempName = (tempNameMatch)? tempNameMatch[1] : "";
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
    
    return jsonData;
  }
  return null;
}



