package io.wollinger.cutils.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

data class RoleMessageDTO(
    val messageID: String,
    val reactions: ArrayList<RoleMessageReactionDTO> = ArrayList(),
    val buttons: ArrayList<RoleMessageButtonDTO> = ArrayList()
)

data class RoleMessageReactionDTO(
    val emoji: String,
    val roleID: String
)

data class RoleMessageButtonDTO(
    val text: String,
    val roleID: String
)

class RoleMessageManager(server: Server) {
    private val messages = HashMap<String, RoleMessageDTO>()
    private val folder = File(server.serverFolder, "rolemessages")

    init {
        folder.mkdirs()
        load()
    }

    private fun getRoleMessageDTO(messageID: String) = messages[messageID] ?: RoleMessageDTO(messageID).also { messages[messageID] = it }

    fun addButton(messageID: String, text: String, roleID: String) = getRoleMessageDTO(messageID).buttons.add(RoleMessageButtonDTO(text, roleID))
    fun addReaction(messageID: String, emoji: String, roleID: String) = getRoleMessageDTO(messageID).reactions.add(RoleMessageReactionDTO(emoji, roleID))

    fun save() {
        messages.forEach { (id, rm) ->
            jacksonObjectMapper().writeValue(File(folder, "$id.json"), rm)
        }
    }

    fun load() {
        folder.listFiles()?.forEach {
            val dto = jacksonObjectMapper().readValue(it, RoleMessageDTO::class.java)
            messages[dto.messageID] = dto
        }
    }
}