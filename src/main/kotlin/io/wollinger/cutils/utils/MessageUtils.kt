package io.wollinger.cutils.utils

import io.wollinger.cutils.commands.ButtonRoleCommandSlash
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.util.concurrent.CopyOnWriteArrayList

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

    fun removeButton(message: Message, buttonID: String): Boolean {
        val components = CopyOnWriteArrayList<LayoutComponent>()
        message.components.forEach { components.add(it) }

        components.forEach { component ->
            if(component is ActionRow) {
                val buttons = CopyOnWriteArrayList(component.components)
                buttons.forEach { btn ->
                    if(btn is Button && buttonID == btn.id) {
                        buttons.remove(btn)
                        components.remove(component)
                        if(buttons.isNotEmpty()) components.add(ActionRow.of(buttons))
                        message.editMessageComponents(components).queue()
                        return true
                    }
                }
            }
        }
        return false
    }
}