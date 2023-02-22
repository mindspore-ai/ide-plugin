import { Constants } from "./constants";
import * as net from "net";

let transformerXLPort : number|undefined;
export async function getTransformerXLPort(){
    if (transformerXLPort) {
        return transformerXLPort;
    }
    transformerXLPort = await tryUsePort(Constants.X86_PYTHON_PORT);
    return transformerXLPort;
}

export async function portUsed(port: number) {
    return new Promise((resolve: any, reject: any) =>{
        let server = net.createServer().listen(port, "::");
        server.on('listening', function(){
            server.close();
            resolve(port);
        });
        server.on('error', function (err: any){
            resolve(err);
        });
    });
}


export async function tryUsePort (port: number) : Promise<number | undefined> {
    let res = await portUsed(port);
    if (res instanceof Error) {
        port = port + 1;
        if (port < 65536) {
            return await tryUsePort(port);
        } else {
            return;
        }
    } else {
        return port;
    }
};

const os: any = require ('os');


export function getIPAddress(): string {
    const interfaces = os.networkInterfaces();
    for (const devName in interfaces){
        const iface = interfaces[devName];
        for (let i = 0; i < iface.length; i++) {
            const alias = iface[i];
            if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal){
                return alias.address;
            }
        }
    }
    return "";
}