package io.wollinger.cutils.commands

import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.Commands

object GetMessageIDCommandContext: ContextMessageCommand {
    override val name = "Get message id"

    override fun run(event: MessageContextInteractionEvent) {
        event.queueReply("ID: ```${event.target.id}```", true)
    }

    override fun getCommandData() = Commands.context(Command.Type.MESSAGE, name).also {
        it.adminOnly()
    }
}