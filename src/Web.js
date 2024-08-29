/**
 * 
 * Contact Information
 * 726-288-2797
 * guidance@school.edu
 * 123 School Street, New York, 14194
 * 
 */


const express = require('express');
const app = express();
const http = require("http");
const WebAPI = require('./WebAPI');
const path = require('node:path');
const Other = require('./Other');
const server = http.createServer(app);
const API = WebAPI.app;
const APIServer = WebAPI.server;
const fs = require('node:fs');
let reason = "Unknown";
let isClosed = false;

app.use(express.static(path.join(__dirname, 'css')));

app.use(express.static(path.join(__dirname, 'img')));



let ping = {
    "3125": 1,
    "3126": 1,
    "3127": 1,
    "3124": 1
}
let success = {
    "3124": true,
    "3125": true,
    "3126": true,
    "3127": true
}


app.get("/", (req, res) => {
    console.log(`[WEB] User Accessed Page. IP: ${req.socket.remoteAddress}`); // logging purposes for rate limiting users from accessing API related pages and accessing the API to avoid the server from being overloaded.
    res.sendFile(__dirname + "/sites/index.html") // displays the home page for the user.
    console.log(`[WEB] Successfully displayed the index page to user with IP: ${req.socket.remoteAddress}`); // Lets the console know that the user has been displayed the page.
})

app.get('/api/v1/ping', (req, res) => {
    res.json({ api: ping[3125], msgs: ping[3124], acc: ping[3126], app: ping[3127] }) // Public API to view all the pings for the api.
})
app.get("/close", (req,res) => {
    res.send("Closed API");
    console.log("[WEB] Recieved Request to close API...")
    console.log("[API] Closing API in 5 Seconds..");
    reason = "API Closed by peer."
    after(2, ()=>{
        APIServer.close()
        Other.msgs.close();
        Other.apps.close();
        console.log("[API] API Closed.")
        isClosed = true;
    });
})

console.log('[WEB] Initializing Web Server...');
after(1, () => {
    // 1 second after the code is ran it will start the web framework
    // This is to make sure all of the application data is loaded before the website is loaded to avoid overlapping multiple tasks.
    server.listen(3030, () => { 
        console.log("[WEB] Web Framework has been initialized.")
    })
    console.log("[API] Initializing Web API...")
})

after(2, WebAPI.start()); // This will start the Web APIS such as the Messaging Backend, Accounts Backend, API Backend, and Applications Backend.
after(2, Other.start("all"));

after(3, heartbeat()); // This is the system that checks the API's alive time and checks if they are down. If they do go down they restart automatically after 5 seconds
// It restarts after 5 seconds to give time to make sure any requests to the API get voided.



// Used to let tasks start after a certain amount of seconds to avoid over pressuring the system.
function after(seconds=1, callback=()=>{}) {
    setTimeout(callback, seconds * 1000)
}

let ports = [
    3124,
    3125,  
    3126,
    3127
]

let servers = {
    3124: "msgs",
    3125: "api",
    3126: "accs",
    3127: "apps"
}

// identifiers:
// 3124 = Msgs
// 3125 = API
// 3126 = Accounts
// 3127 = Applications



function heartbeat() {
    setInterval(async ()=> {
        // console.log("[HEARTBEAT] Sending Heartbeat to the the endpoints")
        try {
            for(port of ports) { // Iterate through all ports in use by the web server.
                let time = Date.now(); // used for gathering Elapsed Time.
                let res;
                try {
                    res = await fetch(`http://127.0.0.1:${port}/api/ping`); // Fetch from the api a response to make sure the server is up and running.
                    if(!success[port]) success[port] = true;
                } catch {
                    if(success[port]) success[port] = false;
                    res = null;
                    ping[port] = 0; // If the ping equals 0 then it is considered DOWN.
                    scheduleReboot(servers[port],2);
                    continue;
                }
                if(res != null && res.ok) {
                    let elapsed = Date.now() - time; // sets elapsed time (aka the ping)
                    ping[port] = elapsed;
                }
            }
            // console.log(ping[3126])
            // console.log(`[HEARTBEAT] Repsonses:\nMessages: ${ping[3124]}ms\nAPI: ${ping[3125]}ms\nAccounts: ${ping[3126]}ms\nApplications: ${ping[3127]}ms`)
        } catch(error) {
            console.log("[HEARTBEAT] Recieved Error While trying heartbeat. Servers down?") // Incase ALL 4 servers end up down it will display this message in 
            console.log("[HEARTBEAT] Reason: " + error) // console to alert the administrators that the server is down and is having an error.
            console.error(error); // error logging
            console.trace();
            // after(2, WebAPI.start()); 
            isClosed = false;
        }
    }, 300)
}

let apirs, msgsrs, accsrs, appsrs = false;

const scheduleReboot = (server="all", seconds=5) => { // Used for scheduling a reboot of a server or all of them.
    switch(server) {
        case "api":
            if(apirs) return;
            apirs = true;
            after(seconds, () => {WebAPI.start(); console.log("[HEARTBEAT] Restarted API Endpoints."); apirs = false;}) // As explained above after the set seconds (default 5) the server specified will restart.
            break;
        case "msgs":
            if(msgsrs) return;
            msgsrs = true;
            after(seconds, () => {Other.start("msgs"); console.log("[HEARTBEAT] Restarted Messages Endpoints."); msgsrs = false;})
            break;
        case "apps":
            if(appsrs) return;
            appsrs = true;
            after(seconds, () => {Other.start("apps"); console.log("[HEARTBEAT] Restarted Applications Endpoints."); appsrs = false;})
            break;
        case "accs":
            if(accsrs) return;
            accrs = true;
            after(seconds, () => {Other.start("accs"); console.log("[HEARTBEAT] Restarted Account Endpoints."); accrs = false;})
            break;
        default:
            WebAPI.app.close();
            Other.msgs.close();
            Other.apps.close();
            Other.accs.close();
            after(seconds, () => {
                WebAPI.start();
                Other.start();
                console.log("[HEARTBEAT] Restarted All Endpoints.")
            })
        
    }
}
