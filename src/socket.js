// Messages Socket Server, Will be used to send messages encrypted from client to client in a secure way


const { Server } = require("socket.io");

const io = new Server()

io.on("connection", (socket) => {
    // implement the connection stuff (aka handle the connections);
})

io.listen(4000);