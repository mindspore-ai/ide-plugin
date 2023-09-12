import axios from "axios";
import { logger } from './log/log4js';

const platform: string[] = ['Ascend', 'GPU', 'CPU'];

export async function addPlatform(pyJsonData: any[] | null) {
    if (pyJsonData === null) {
        return;
    }
    let dataPromiseList: Promise<void>[] = [];
    for (let data of pyJsonData) {
        let promise = new Promise<void>(resolve => {
            let apiName = data.mindspore1word;
            axios({
                url: data.mindsporeURL,
                method: 'get'
            }).then((response: any) => {
                let htmlText = response.data as string;
                //apiName位置
                let apiNameIndex = htmlText.indexOf('<dt class="sig sig-object py" id="' + apiName);
                let platformIndex = htmlText.indexOf('支持平台', apiNameIndex);
                if (!htmlText.substring(apiNameIndex, platformIndex).includes('class="py ')) {
                    let platformString = htmlText.substring(platformIndex, htmlText.indexOf('</dd>', platformIndex));
                    let platformList: string[] = [];
                    platform.forEach(type => {
                        if (platformString.includes(type)) {
                            platformList.push(type);
                        }
                    });
                    
                    if (platformList.length > 0) {
                        data.platform = platformList.toString();
                    }
                } else {
                    data.platform = '暂无数据';
                }
                resolve();
            }).catch((error: any) => {
                logger.warn(`get ${apiName} html content msg: ${error}`);
            })
        })
        dataPromiseList.push(promise);
    }
    return Promise.allSettled(dataPromiseList);
}