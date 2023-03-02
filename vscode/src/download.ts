import fs = require("fs")
import path = require("path")
import axios from 'axios'
import compressing = require('compressing')
import { window } from "vscode"
import { logger } from "./log/log4js"
import { fsExistsSync } from "./fileUtil"

export async function downloadFile(url: string, fileName: string, destination: string){
	if (!fsExistsSync(destination)){
		fs.mkdirSync(destination);
	}
    const writer = fs.createWriteStream(path.join(destination, fileName))
    const response = await axios({
        url,
        method: 'GET',
        responseType: 'stream'
    });

    response.data.pipe(writer);

    return new Promise((resolve, reject) => {
        writer.on('finish', resolve);
        writer.on('error', reject);
    });
}

export async function unzipSync(fileName: string, destination: string) {
    return compressing.zip.uncompress(path.join(destination, fileName),destination).then((res:any)=> {
        logger.info('decompression successful');
    }).catch((err: any) =>{
        logger.error('decompression failed');
        throw err;
    })
}

export async function download(fileName: string, url: string, destination: string) {
    await downloadFile(url, fileName, destination);
    const fineNameArr = fileName.split('.');
    const suffix = fineNameArr[fineNameArr.length - 1];
    switch (suffix) {
        case 'zip':
            await unzipSync(fileName, destination);
            fs.unlinkSync(path.join(destination, fileName));
            break;
        case 'json':
            break;
        default:
            logger.error(`suffix of file to download is incorrect, file name is ${fileName}`);
            window.showInformationMessage(`处理下载的文件失败,文件名:${fileName}`);
    }
    
}