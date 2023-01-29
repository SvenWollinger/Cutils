package io.wollinger.cutils

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

data class ServerConfig(
    var cmdPrefix: String = "!"
)

class Server(id: String) {
    private val config: ServerConfig

    init {
        println("Init server $id")
        File("servers/$id/").mkdirs()
        File("servers/$id/config.json").also {
            config = if(it.exists()) ObjectMapper().readValue(it, ServerConfig::class.java) else ServerConfig()
            ObjectMapper().writeValue(it, config)
        }
    }
}