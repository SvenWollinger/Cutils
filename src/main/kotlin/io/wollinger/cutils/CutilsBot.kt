package io.wollinger.cutils

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import io.wollinger.cutils.commands.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
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
    private val jda: JDA

    private val interactables = HashMap<String, Interactable>().also {
        it[InfoCommandSlash.label] = InfoCommandSlash
        it[InfoCommandContext.name] = InfoCommandContext
    }

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
            it.addEventListeners(this)
            if(config.playing.isNotEmpty()) it.setActivity(Activity.playing(config.playing))
        }.build()
        jda.awaitReady()
        log("Logged in as ${jda.selfUser}")

        //Init Servers
        jda.guilds.forEach { servers[it.id] = Server(it) }

        //Create slash commands
        ArrayList<CommandData>().also {
            interactables.forEach { (_, cmd) -> it.add(cmd.getCommandData()) }
            jda.updateCommands().addCommands(it).complete()
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = (interactables[event.name] as SlashCommand).run(event)
    override fun onUserContextInteraction(event: UserContextInteractionEvent): Unit = (interactables[event.name] as ContextUserCommand).run(event)
}

fun log(msg: Any) = println(msg)