package me.leaf.devs.update

import me.leaf.devs.Main
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class Versions(val latest: String, val stable: String, val recommended: String);

object UpdateChecker {

    fun checkUpdate(branch: String): Boolean {
        if (Branch.parseBranch(branch) != null) {
            val data = URL("https://gist.githubusercontent.com/LeafDevs/2b0b652efe1538e5f99255f9387934dc/raw/c6b2d6fb56dbebb5333899ef4d431a251b860ece/ver.json").readText();
            val json = Json.decodeFromString<Versions>(data);

            val ver: String? = when(branch) {
                "stable" -> json.stable
                "recommended" -> json.recommended
                "latest" -> json.latest
                else -> null
            }

            if(ver == null) {
                println("+------------------------------------------------------+")
                println("|         Invalid Branch for Update Checker.           |")
                println("| Valid Branches are: stable, recommended, and latest  |")
                println("+------------------------------------------------------+")
                return false
            } else {
                if(Main().getConfig()?.get("version").toString() != ver) {
                    println("                New Update Available!")
                    println("      Visit the github to install the new update!")
                    println("")
                    println("                 New Version: " + ver)
                    println("                Current Version: " + Main().getConfig()?.get("version"))
                    println("")
                    return true
                } else {
                    println("Everything is up to date!")
                    return false
                }
            }
        }
        return false
    }

    private enum class Branch(s: String) {
        STABLE("stable"),
        RECOMMENDED("recommended"),
        LATEST("latest");


        companion object {
            fun parseBranch(branch: String): Branch? {
                return when (branch.toUpperCase()) {
                    "STABLE" -> STABLE
                    "RECOMMENDED" -> RECOMMENDED
                    "LATEST" -> LATEST
                    else -> null
                }
            }
        }

        fun getBranch(): String {
            return name
        }
    }

    fun updateJar() {
        checkUpdate(branch = Main().getConfig()?.get("").toString())
    }
}
