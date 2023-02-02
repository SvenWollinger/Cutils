package io.wollinger.cutils.server

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

    init {
        File(server.serverFolder, "rolemessages").mkdirs()
    }
}