package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.UserUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.Commands

object AvatarCommandContext: ContextUserCommand {
    override val name = "Get User avatar"

    override fun run(server: Server, event: UserContextInteractionEvent) {
        event.queueReply("""
            Server avatar: ${UserUtils.getServerAvatarURL(event.member!!) ?: "Not set"}
            Base avatar: ${UserUtils.getAvatarURL(event.member!!.user)}
        """.trimIndent(), true)
    }

    override fun getCommandData() = Commands.context(Command.Type.USER, name)
}