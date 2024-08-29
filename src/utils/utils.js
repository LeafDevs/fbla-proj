const { spawnSync } = require("child_process");

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
    }
}