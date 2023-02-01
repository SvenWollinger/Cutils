package io.wollinger.cutils.commands

import io.wollinger.cutils.TimestampType
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
        Mention: ${member.asMention}
        Account created: ${f.format(member.timeCreated)}
        Time joined: ${f.format(member.timeJoined)}
        Time boosted: ${member.timeBoosted.let { if(it == null) "-" else f.format(it) }}
        Avatar url: <${member.effectiveAvatarUrl}?size=1024>
    """.trimIndent()
}

object InfoCommandSlash: SlashCommand {
    override val label = "info"

    override fun run(event: SlashCommandInteractionEvent) {
        val input = event.getOption("user-id")!!.asString

        if(input.toLongOrNull() == null) {
            event.reply("Bad id!").setEphemeral(true).queue()
            return
        }

        val member = event.guild?.getMemberById(input)

        if(member == null) event.reply("User with that id not found!").setEphemeral(true).queue()
        else event.reply(getInfoMessage(member)).queue()
    }

    override fun getCommandData() = Commands.slash(label, "Get info about user").also {
        it.isGuildOnly = true
        it.addOption(OptionType.STRING, "user-id", "User ID (Right click a user if you dont have the id)",true)
    }
}

object InfoCommandContext: ContextUserCommand {
    override val name = "Get User info"

    override fun run(event: UserContextInteractionEvent) {
        event.reply(getInfoMessage(event.targetMember!!)).queue()
    }

    override fun getCommandData() = Commands.context(Command.Type.USER, name).also {
        it.isGuildOnly = true
    }
}