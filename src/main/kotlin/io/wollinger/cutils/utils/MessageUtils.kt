package io.wollinger.cutils.utils

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message

object MessageUtils {
    fun findMessage(guild: Guild, messageID: String, onSuccess: (Message) -> (Unit), onFailure: () -> (Unit)) {
        var searched = 0
        val max = guild.textChannels.size

        guild.textChannels.forEach { channel ->
            channel.retrieveMessageById(messageID).queue({
                onSuccess.invoke(it)
            }, {
                searched++
                if(searched == max) {
                    onFailure.invoke()
                    return@queue
                }
            })
        }
    }
}