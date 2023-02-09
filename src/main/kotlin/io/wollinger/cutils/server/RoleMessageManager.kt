package io.wollinger.cutils.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.wollinger.cutils.CutilsBot
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

class ReactionRoleManager(server: Server): ListenerAdapter() {
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
            val dto = jacksonObjectMapper().readValue(it, ReactionMessageDTO::class.java)
            messages[dto.messageID] = dto
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if(event.user!!.id == CutilsBot.jda.selfUser.id) return

        messages[event.messageId]!!.reactions.forEach {
            if(it.emoji == event.emoji.toString()) {
                event.guild.addRoleToMember(event.member!!, event.guild.getRoleById(it.roleID)!!).queue()
                return
            }
        }
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        if(event.user!!.id == CutilsBot.jda.selfUser.id) return

        messages[event.messageId]!!.reactions.forEach {
            if(it.emoji == event.emoji.toString()) {
                event.guild.removeRoleFromMember(event.member!!, event.guild.getRoleById(it.roleID)!!).queue()
                return
            }
        }
    }
}