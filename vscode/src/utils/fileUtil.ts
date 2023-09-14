const fs = require('fs');



export function fsExistsSync(path: string){
    try {
        fs.accessSync(path, fs.F_OK);
    } catch(e) {
        return false;
    }
    return true;
}