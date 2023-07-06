import * as TreeSitter from "web-tree-sitter";
import * as path from "path";
import { downloadFile } from "./download";
import * as scanAPI from "./scanAPI";
import { Constants } from "./constants";
import { homedir } from "os";
import { join } from "path";
let parser: TreeSitter;
export async function init() {
    await TreeSitter.init({
        locateFile(scriptName: string, scriptDirectory: string) {
            return path.resolve(__dirname, `../tree-sitter.wasm`);
        }
    });
    parser = new TreeSitter();
    const python = await TreeSitter.Language.load(path.resolve(__dirname, `../tree-sitter-python.wasm`));
    parser.setLanguage(python);
    let downloadFlag = await downloadFile(Constants.PYTORCH_API_MAPPING_DOWNLOAD_URL, Constants.PYTORCH_API_MAPPING_FILENAME, join(homedir(), ".mindspore"), 2000);
	scanAPI.initApiMap(downloadFlag);
}


export function apiScan(data:string) {
    const tree = parser?.parse(data);
    let scanner = new APIScanner();
    scanner.iter(tree.rootNode);
    return scanner.apiList;
}

class APIScanner{
    importMap = new Map<string, string>();
    apiList : string[] = [];

    getApiList() {
        return this.apiList;
    }
    iter(node: TreeSitter.SyntaxNode) {
        if ("call" === node.type) {
            let attribute = node.children[0].text;
            let list :string[] = [];
            let attrList = attribute.split(".");
            for (let element of attrList) {
                if (!element.includes("(")) {
                    list.push(element);
                } else {
                    list.push(...attrList.slice(-1));
                    break;
                }
            }
            let result = "";
            if (list.length === 1) {
                result =list[0];
                result = this.importMap.get(result)?? result;
            } else if (list.length > 1) {
                let apiName = list.pop();
                let importSearchName = list.join(".");
                importSearchName = this.importMap.get(importSearchName)?? importSearchName;
                result = importSearchName + "." + apiName;
            }


            this.apiList.push(result);
        }
        // if ("attribute" == node.type) {
        //     apiList.push(node.text);
        // }
        if ("import_statement" === node.type) {
            node.children.forEach(element => {
                if ("dotted_name" === element.type) {
                    this.importMap.set(element.text, element.text);
                } else if("aliased_import" === element.type) {
                    this.importMap.set(element.children[2].text, element.children[0].text);
                }
            });
            return;
        }
        if ("import_from_statement" === node.type) {
            let packageName = node.children[1].text;
            node.children.forEach((element, index) => {
                if (index < 2) {
                    return;
                }
                if ("dotted_name" === element.type) {
                    this.importMap.set(element.text, packageName + "."+element.text);
                } else if("aliased_import" === element.type) {
                    this.importMap.set(element.children[2].text, packageName + "." + element.children[0].text);
                }
            });
            return;
        }
        node.children.forEach(element => {
            this.iter(element);
        });
    }
}

