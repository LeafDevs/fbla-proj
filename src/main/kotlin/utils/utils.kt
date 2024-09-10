package me.leaf.devs.utils

import me.leaf.devs.Main
import java.sql.Connection


class Utils {
    fun createUser(email: String, password: String, username: String): Boolean {
        try {
            if(Main().getConnection()?.isValid(0) == true) {
                val conn: Connection? = Main().getConnection()
                conn?.prepareStatement(String.format("INSERT INTO users (name, email, password) VALUES (%s, %s, %s)", username, email, password))
            }
            return true
        } catch(ignored: Throwable) {
            return false;
        }
    }
}