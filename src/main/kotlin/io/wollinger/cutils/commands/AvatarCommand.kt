package io.wollinger.cutils.commands

import io.wollinger.cutils.utils.UserUtils
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.Commands

object AvatarCommandContext: ContextUserCommand {
    override val name = "Get User avatar"

    override fun run(event: UserContextInteractionEvent) {
        val message = """
            Server avatar: ${UserUtils.getServerAvatarURL(event.member!!) ?: "Not set"}
            Base avatar: ${UserUtils.getAvatarURL(event.member!!.user)}
        """.trimIndent()
        event.reply(message).setEphemeral(true).queue()
    }

    override fun getCommandData() = Commands.context(Command.Type.USER, name)
}