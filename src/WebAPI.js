const express = require("express");
const http = require("http");
const app = express();
const fs = require('node:fs')

const server = http.createServer(app);

app.get("/api/ping", (req, res) => {
    res.json({code: 400})
})

app.get("/api/v1/accounts", (req,res) => {
    if(!res.session.admin) {
        res.json({code: 401});
    }
    const data = JSON.parse(fs.readFileSync("Data/accounts.json"));
    res.json(data);
})


app.get("/api/v1/sub", (req,res) => {
    res.json({code: 400});
})


module.exports = {

    app,
    server,

    start() {
        server.listen(3125, ()=> {
            console.log("\n[API] Web API started successfully!\n");
        })
    }
}