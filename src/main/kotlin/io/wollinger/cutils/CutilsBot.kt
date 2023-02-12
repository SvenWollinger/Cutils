package io.wollinger.cutils

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import io.wollinger.cutils.commands.*
import io.wollinger.cutils.server.Server
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
data class BotConfig(
    var token: String = "",
    var playing: String = ""
)

object CutilsBot: ListenerAdapter() {
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
            it.addEventListeners(CommandManager)
            it.addEventListeners(this)
            if(config.playing.isNotEmpty()) it.setActivity(Activity.playing(config.playing))
        }.build()
        jda.awaitReady()
        log("Logged in as ${jda.selfUser}")

        //Init Servers
        jda.guilds.forEach { servers[it.id] = Server(it) }

        //Init Command Manager
        CommandManager.register()
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if(event.user!!.id == jda.selfUser.id) return
        servers[event.guild.id]!!.rmm.onReaction(member = event.member!!, messageID = event.messageId, isAdded = true, guild = event.guild, emoji = event.emoji)
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        if(event.user!!.id == jda.selfUser.id) return
        servers[event.guild.id]!!.rmm.onReaction(member = event.member!!, messageID = event.messageId, isAdded = false, guild = event.guild, emoji = event.emoji)
    }

    fun getServer(id: String) = servers[id]!!

    val id: String get() = jda.selfUser.id
}

fun log(msg: Any) = println(msg)

fun main() {
    //This causes the object to be created
    CutilsBot
}