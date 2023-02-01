package io.wollinger.cutils

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.entities.Guild
import java.io.File

data class ServerConfig(
    var cmdPrefix: String = "!"
)

class Server(private val guild: Guild) {
    private val id = guild.id
    private val config: ServerConfig

    init {
        println("Init server $id")
        File("servers/$id/").mkdirs()
        File("servers/$id/config.json").also {
            config = if(it.exists()) ObjectMapper().readValue(it, ServerConfig::class.java) else ServerConfig()
            ObjectMapper().writeValue(it, config)
        }
    }
}