package io.wollinger.cutils.commands

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.Commands

object AvatarCommandContext: ContextUserCommand {
    override val name = "Get User avatar"

    override fun run(event: UserContextInteractionEvent) {
        event.reply("<${event.targetMember!!.effectiveAvatarUrl}?size=1024>").setEphemeral(true).queue()
    }

    override fun getCommandData() = Commands.context(Command.Type.USER, name)
}