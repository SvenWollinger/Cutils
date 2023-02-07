package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.TimestampType
import io.wollinger.cutils.utils.UserUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

private fun getInfoMessage(member: Member): String {
    val f = TimestampType.LONG_DATE_SHORT_TIME
    return """
        User info:
        ID: ${member.id}
        Current username: ${member.user.name}#${member.user.discriminator} (as of ${f.formatNow()})
        Nickname: ${member.nickname ?: "No nickname"}
        Mention: ${member.asMention}
        Account created: ${f.format(member.timeCreated)}
        Time joined: ${f.format(member.timeJoined)}
        Time boosted: ${member.timeBoosted.let { if(it == null) "Not boosting" else f.format(it) }}
        Server avatar url: ${UserUtils.getServerAvatarURL(member) ?: "Not set"}
        Avatar url: ${UserUtils.getAvatarURL(member.user)}
    """.trimIndent()
}

object InfoCommandSlash: SlashCommand {
    override val label = "info"

    override fun run(server: Server, event: SlashCommandInteractionEvent) {
        val input = event.getOption("user-id")!!.asString

        if(input.toLongOrNull() == null) {
            event.queueReply("Bad id!", true)
            return
        }

        val member = event.guild?.getMemberById(input)

        if(member == null) event.queueReply("User with that id not found!", true)
        else event.queueReply(getInfoMessage(member))
    }

    override fun getCommandData() = Commands.slash(label, "Get info about user").also {
        it.isGuildOnly = true
        it.addOption(OptionType.STRING, "user-id", "User ID (Right click a user if you dont have the id)",true)
    }
}

object InfoCommandContext: ContextUserCommand {
    override val name = "Get User info"

    override fun run(server: Server, event: UserContextInteractionEvent) = event.queueReply(getInfoMessage(event.targetMember!!))

    override fun getCommandData() = Commands.context(Command.Type.USER, name).also {
        it.isGuildOnly = true
    }
}