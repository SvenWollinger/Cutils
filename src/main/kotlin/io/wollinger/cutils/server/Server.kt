package io.wollinger.cutils.server

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.entities.Guild
import java.io.File

data class ServerConfig(
    var cmdPrefix: String = "!"
)

class Server(val guild: Guild) {
    private val id = guild.id
    private val config: ServerConfig
    private val rmm: RoleMessageManager
    val serverFolder = File("servers/$id/")

    init {
        println("Init server $id")
        serverFolder.mkdirs()
        rmm = RoleMessageManager(this)
        File(serverFolder, "config.json").also {
            config = if(it.exists()) ObjectMapper().readValue(it, ServerConfig::class.java) else ServerConfig()
            ObjectMapper().writeValue(it, config)
        }
    }
}