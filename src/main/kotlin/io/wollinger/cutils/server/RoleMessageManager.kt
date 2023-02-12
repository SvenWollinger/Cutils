package io.wollinger.cutils.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.wollinger.cutils.CutilsBot
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.File

data class ReactionMessageDTO(
    val messageID: String,
    val reactions: ArrayList<ReactionRoleDTO> = ArrayList(),
)

data class ReactionRoleDTO(
    val emoji: String,
    val roleID: String
)

class ReactionRoleManager(private val server: Server) {
    private val messages = HashMap<String, ReactionMessageDTO>()
    private val folder = File(server.serverFolder, "reactionroles")

    init {
        folder.mkdirs()
        load()
    }

    private fun getReactionMessage(messageID: String) = messages[messageID] ?: ReactionMessageDTO(messageID).also { messages[messageID] = it }

    fun addReaction(messageID: String, emoji: String, roleID: String) = getReactionMessage(messageID).reactions.add(ReactionRoleDTO(emoji, roleID))

    fun save() {
        messages.forEach { (id, rm) ->
            jacksonObjectMapper().writeValue(File(folder, "$id.json"), rm)
        }
    }

    fun load() {
        folder.listFiles()?.forEach {
            jacksonObjectMapper().readValue(it, ReactionMessageDTO::class.java).also { dto -> messages[dto.messageID] = dto }
        }
    }

    fun onReaction(member: Member, messageID: String, isAdded: Boolean, emoji: Emoji, guild: Guild) {
        println("oi")
        messages[messageID]?.reactions?.forEach {
            if(it.emoji == emoji.toString()) {
                if(isAdded) guild.addRoleToMember(member, guild.getRoleById(it.roleID)!!).queue()
                else guild.removeRoleFromMember(member, guild.getRoleById(it.roleID)!!).queue()
                return
            }
        }
    }
}