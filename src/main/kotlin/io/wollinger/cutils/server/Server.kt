package io.wollinger.cutils.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.wollinger.cutils.CutilsBot
import net.dv8tion.jda.api.entities.Guild
import java.io.File

data class ServerConfig(
    var cmdPrefix: String = "!"
)

class Server(guild: Guild) {
    private val id = guild.id
    private val config: ServerConfig
    val rmm: ReactionRoleManager
    val userManager: UserManager
    val serverFolder = File("servers/$id/")

    init {
        println("Init server $id")
        serverFolder.mkdirs()
        rmm = ReactionRoleManager(this)
        userManager = UserManager(this)
        CutilsBot.jda.addEventListener(rmm)
        File(serverFolder, "config.json").also {
            config = if(it.exists()) ObjectMapper().readValue(it, ServerConfig::class.java) else ServerConfig()
            ObjectMapper().writeValue(it, config)
        }
    }
}