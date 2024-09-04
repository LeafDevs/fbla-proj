package me.leaf.devs

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import java.io.File

fun main() {
    webServer()
}

fun webServer() {
    embeddedServer(Netty, port = 3000) {
        routing {
            static("/") {
                resources("static")
            }
            get("/") {
                call.respondFile(File("src/main/resources/static/html/index.html"))
            }
            get("/jobs") {
                call.respondFile(File("src/main/resources/static/html/posting.html"))
            }

        }
    }.start(wait = true)
}
