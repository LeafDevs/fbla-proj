const { spawnSync } = require("child_process");
const fs = require("node:fs");
const fick = require("../../")

module.exports = {
    checkPort(port) {
        const output = spawnSync(
            `lsof -i tcp:${port} | awk '{print $2}' | grep --invert PID`,
            {shell: true}
        )
        if(output.error) {
            console.log(output.error);
        }
    
        const pid = Buffer.from(output.stdout.buffer).toString().split('\n')[0];
        console.log({ pid });
        if(pid) {
            return true;
        } else {
            return false;
        }
    },
    createPosting(title, description, accountid, thumbnail, positions) {
        const data = fs.readFileSync("../data/postings.json")
    }
}