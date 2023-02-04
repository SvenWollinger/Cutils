package io.wollinger.cutils.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

data class ReactionMessageDTO(
    val messageID: String,
    val reactions: ArrayList<ReactionRoleDTO> = ArrayList(),
)

data class ReactionRoleDTO(
    val emoji: String,
    val roleID: String
)

class ReactionRoleManager(server: Server) {
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
}