import { markdownTableToJson } from "./readMD";
import { markdownTable } from 'markdown-table';

export function scanAPI(fileContent: string){
    let filePath = "D:/MindSporeToolkit/ide-plugin/vscode/test.md"
    let jsonData = markdownTableToJson(filePath); 

    const table = markdownTable([
        ['header 1', 'header 2'],
        ['row 1 cell 1', 'row 1 cell 2'],
        ['row 2 cell 1', 'row 2 cell 2'],
    ]);

    return table;
}