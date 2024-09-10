package me.leaf.devs

import com.typesafe.config.ConfigException.Null
import me.leaf.devs.enums.StorageType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import org.yaml.snakeyaml.Yaml
import me.leaf.devs.update.UpdateChecker
import java.sql.DriverManager
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.Connection
import java.util.jar.JarFile

val yaml = Yaml()

data class User(val id: Int, val name: String, val email: String, val password: String)

var inputStream: InputStream? = null


// Parse the YAML content into a Map
var data: Map<String, Any>? = null

var connection: Connection? = null

val users = mutableListOf<User>()

class Main {
    fun getConnection(): Connection? {
        if(connection?.isValid(0) == true) {
            return connection
        }
        return null;
    }
    fun getUsers(): MutableList<User> {
        return users;
    }

    fun getConfig(): Map<String, Any>? {
        return data;
    }
}


fun main() {
    val runningFromIDE = System.getenv("IS_IDE") == "true"

    if (runningFromIDE) {
        println("Detected Development Environment.")
        inputStream = {}.javaClass.getResourceAsStream("/data/config.yml")
            ?: throw IllegalArgumentException("File not found")
    } else {
        println("Detected Production Environment.")
        val jarFile = File(Main::class.java.protectionDomain.codeSource.location.toURI()).absolutePath
        val destDir = File("prism")

        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        // Extract resources from the JAR
        extractResourcesFromJar(jarFile, destDir)

        inputStream = File("prism/data/config.yml").inputStream()
    }

    println(" ")
    println("Ignore the SLFJ4 Warnings, They mean nothing. I do not like any of the SLF4J Loggers that kTor supports.")
    println(" ")

    data = yaml.load(inputStream)

    connectDatabase()

    if(data?.get("check-for-updates") == true) {
        data?.get("update-branch")?.let { UpdateChecker.checkUpdate(it.toString()) };
    }

    val query = connection?.prepareStatement("SELECT * FROM users")
    val result = query?.executeQuery()

    while(result?.next() == true) {

        // Getting the value of the id column
        val id = result.getInt("id")

        // Getting the value of the username column
        val username = result.getString("username")

        // Getting the value of the email column
        val email = result.getString("email")

        // Getting the value of the password column (if needed)
        val password = result.getString("password")

        // adding the users to a data storage.
        users.add(User(id, username, email, password))
    }
    println(users);


    println("")


    webServer()



}

fun webServer() {
    embeddedServer(Netty, port = 3040) {
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
            post("/api/v1/register") {

            }


        }
    }.start(wait = true)
}


fun connectDatabase() {
    val databaseConfig = data?.get("database") as Map<String, Any>

    val host = databaseConfig["host"] as String
    val port = databaseConfig["port"] as String
    val database = databaseConfig["database"] as String

    val url = String.format("jdbc:%s:%s/%s", host, port,database);
    val username = databaseConfig["username"] as String
    val password = databaseConfig["password"] as String


    connection = DriverManager.getConnection(url, username, password)
}


fun extractResourcesFromJar(jarPath: String, destDir: File) {
    JarFile(jarPath).use { jar ->
        val entries = jar.entries()
        val whitelist = setOf("data", "static")
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val entryPath = entry.name
            val entryFile = File(destDir, entryPath)

            if (whitelist.any { entryPath.startsWith(it) }) {
                if (entry.isDirectory) {
                    if (!entryFile.exists()) {
                        entryFile.mkdirs()
                    }
                } else {
                    if (!entryFile.exists()) {
                        entryFile.parentFile.mkdirs()
                        jar.getInputStream(entry).use { input ->
                            Files.copy(input, entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        }
                    }
                }
            }
        }
    }
}