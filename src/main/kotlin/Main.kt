import com.typesafe.config.ConfigException.Null
import enums.StorageType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import org.yaml.snakeyaml.Yaml
import java.sql.DriverManager
import java.io.File
import java.io.InputStream
import java.sql.Connection


val yaml = Yaml()

data class User(val id: Int, val name: String, val email: String, val password: String)

val inputStream: InputStream = object {}.javaClass.getResourceAsStream("/data/config.yml")
    ?: throw IllegalArgumentException("File not found")


// Parse the YAML content into a Map
val data: Map<String, Any> = yaml.load(inputStream)
val databaseConfig = data["database"] as Map<String, Any>

val host = databaseConfig["host"] as String
val port = databaseConfig["port"] as String
val database = databaseConfig["database"] as String

var url = String.format("jdbc:%s:%s/%s", host, port,database);
val username = databaseConfig["username"] as String
val password = databaseConfig["password"] as String


val connection: Connection = DriverManager
    .getConnection(url, username, password)

val users = mutableListOf<User>()

class Main {
    fun getConnection(): Connection? {
        if(connection.isValid(0)) {
            return connection
        }
        return null;
    }
    fun getUsers(): MutableList<User> {
        return users;
    }
}


fun main() {

    println(connection.isValid(0))

    val inputStream: InputStream = object {}.javaClass.getResourceAsStream("/data/config.yml")
        ?: throw IllegalArgumentException("File not found")


    // Parse the YAML content into a Map
    val data: Map<String, Any> = yaml.load(inputStream)

    // Access the values
    val storageType = data["data-storage-type"] as String

    if(storageType == StorageType.MARIADB.toString() || storageType == StorageType.MYSQL.toString() || storageType == StorageType.POSTGRESQL.toString()) {
        val query = connection.prepareStatement("SELECT * FROM users")
        val result = query.executeQuery()



        while(result.next()) {

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
    }

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

        }
    }.start(wait = true)
}
