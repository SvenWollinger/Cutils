package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.MessageUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.utils.messages.MessageEditData

object EditMessageCommand: BaseCommand, SlashCommandAdapter {
    override val slashCommandLabel = "editmessage"

    override fun onSlashCommand(server: Server, event: SlashCommandInteractionEvent) {
        MessageUtils.findMessage(event.guild!!, event.getOption("message-id")!!.asString, {
            it.editMessage(MessageEditData.fromMessage(it)).setContent(event.getOption("content")?.asString ?: "").queue({
                event.queueReply("Done.", true)
            }, { t ->
                event.queueReply("Message could not be edited. Reason: ${t.message}", true)
            })
        }, {
            event.queueReply("Message not found!", true)
        })
    }

    override fun register(): Array<CommandData> = arrayOf(
        Commands.slash(slashCommandLabel, "Edit message").also {
            it.adminOnly()
            it.isGuildOnly = true
            it.addOption(OptionType.STRING, "message-id", "Message ID", true)
            it.addOption(OptionType.STRING, "content", "New Content", false)
        }
    )
}