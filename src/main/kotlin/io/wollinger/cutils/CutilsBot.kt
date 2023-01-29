package io.wollinger.cutils

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
data class BotConfig(
    var token: String = "",
    var playing: String = ""
)

object CutilsBot {
    private val config: BotConfig
    private val servers = HashMap<String, Server>()
    val jda: JDA

    init {
        File("config.json").also {
            //Load file or create fresh one
            config = if(it.exists()) ObjectMapper().readValue(it, BotConfig::class.java) else BotConfig()
            //Check if token is set and if not, prompt
            if(config.token.isEmpty()) {
                print("Token: ")
                config.token = readln()
            }
            //Write changes to disk
            ObjectMapper().writeValue(it, config)
        }

        jda = JDABuilder.createDefault(config.token).also {
            it.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
            it.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES)
            it.setBulkDeleteSplittingEnabled(false)
            if(config.playing.isNotEmpty()) it.setActivity(Activity.playing(config.playing))
        }.build()
        jda.awaitReady()
        log("Logged in as ${jda.selfUser}")

        jda.guilds.forEach {
            log("Found server: $it")
            servers[it.id] = Server(it.id)
        }
    }
}

fun log(msg: Any) = println(msg)