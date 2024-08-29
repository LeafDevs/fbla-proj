const express = require("express");

const msg = express();
const app = express();
const acc = express(); 

const http = require("http");


const msgs = http.createServer(msg);

const accs = http.createServer(acc)

const apps = http.createServer(app);

msg.get("/api/ping", (req, res) => {
    res.json({code: 400})
})
acc.get("/api/ping", (req, res) => {
    res.json({code: 400})
})
app.get("/api/ping", (req, res) => {
    res.json({code: 400})
})

module.exports = {
    msgs,
    msg,
    apps,
    app,
    accs,
    acc,
    start(server="msgs") {
        switch(server) {
            case "msgs":
                msgs.listen(3124, () => {
                    console.log('[MESSAGES] Messages Endpoint started.');
                })
                break;
            case "apps":
                apps.listen(3127, ()=> {
                    console.log("[APPLICATIONS] Applications Endpoint Started!")
                })
                break;
            case "accs": 
                accs.listen(3126, () => {
                    console.log("[ACCOUNTS] Accounts Endpoint started!")
                })
                break;
            case "all":
                accs.listen(3126, () => {
                    console.log("[ACCOUNTS] Accounts Endpoint started!")
                })
                msgs.listen(3124, () => {
                    console.log('[MESSAGES] Messages Endpoint started.');
                })
                apps.listen(3127, () => {
                    console.log("[APPLICATIONS] Applications Endpoint started.")
                })
                break;
            default:
                apps.listen(3127, () => {
                    console.log("[APPLICATIONS] Applications Endpoint started.")
                })
                break;
        }
    }
}