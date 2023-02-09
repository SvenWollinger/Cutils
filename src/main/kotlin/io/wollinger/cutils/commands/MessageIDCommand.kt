package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

object MessageIDCommand: BaseCommand, MessageContextAdapter {
    override val messageContextLabel = "Get message id"

    override fun onMessageContext(server: Server, event: MessageContextInteractionEvent) = event.queueReply("ID: ```${event.target.id}```", true)

    override fun register(): Array<CommandData> = arrayOf(
        Commands.context(Command.Type.MESSAGE, messageContextLabel)
    )
}