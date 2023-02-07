package io.wollinger.cutils.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

data class UserData(
    var birthdayUnix: Long = -1
)

class UserManager(server: Server) {
    private val usersCached = HashMap<String, UserData>()
    private val folder = File(server.serverFolder, "users")

    init {
        folder.mkdirs()
    }

    fun save() {
        usersCached.forEach { (id, rm) ->
            jacksonObjectMapper().writeValue(File(folder, "$id.json"), rm)
        }
        usersCached.clear()
    }

    private fun load(id: String) {
        if(usersCached.containsKey(id)) return
        val userFile = File(folder, "$id.json")
        if(userFile.exists()) {
            usersCached[id] = jacksonObjectMapper().readValue(userFile, UserData::class.java)
        } else {
            usersCached[id] = UserData()
        }
    }

    fun getUserData(id: String): UserData {
        load(id)
        return usersCached[id]!!
    }
}