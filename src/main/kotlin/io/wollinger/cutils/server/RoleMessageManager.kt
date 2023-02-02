package io.wollinger.cutils.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

data class RoleMessageDTO(
    var messageID: String,
    var r: ArrayList<RoleMessageItemDTO>
)

data class RoleMessageItemDTO(
    var type: Type,
    var content: String
) { enum class Type { REACTION, BUTTON } }

class RoleMessageManager(server: Server) {
    private val messages = HashMap<String, RoleMessageDTO>()
    private val folder = File(server.serverFolder, "rolemessages")

    init {
        folder.mkdirs()
    }

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