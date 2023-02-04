package io.wollinger.cutils.commands

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.Commands

object AvatarCommandContext: ContextUserCommand {
    override val name = "Get User avatar"

    override fun run(event: UserContextInteractionEvent) {
        val avatarServer = event.targetMember!!.avatarUrl
        val avatar = event.targetMember!!.user.effectiveAvatarUrl
        val message = """
            Server avatar: ${if(avatarServer != null) "<$avatarServer?1024>" else "Not set"}
            Base avatar: <$avatar?size=1024>
        """.trimIndent()
        event.reply(message).setEphemeral(true).queue()
    }

    override fun getCommandData() = Commands.context(Command.Type.USER, name)
}