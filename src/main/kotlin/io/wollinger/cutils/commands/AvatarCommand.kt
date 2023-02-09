package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.UserUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

object AvatarCommand: BaseCommand, SlashCommandAdapter, UserContextAdapter {
    override val userContextLabel = "Get User avatar"
    override val slashCommandLabel = "avatar"

    private fun get(member: Member) = """
        Server avatar: ${UserUtils.getServerAvatarURL(member) ?: "Not set"}
        Base avatar: ${UserUtils.getAvatarURL(member.user)}
    """.trimIndent()

    override fun onUserContext(server: Server, event: UserContextInteractionEvent) = event.queueReply(get(event.member!!), true)
    override fun onSlashCommand(server: Server, event: SlashCommandInteractionEvent) {
        val member = event.guild!!.getMemberById(event.getOption("user-id")!!.asString)
        event.queueReply(if(member != null) get(member) else "User not found.", true)
    }

    override fun register() = arrayOf(
        Commands.context(Command.Type.USER, userContextLabel),
        Commands.slash(slashCommandLabel, userContextLabel).also {
            it.addOption(OptionType.STRING, "user-id", "User ID (Right click a user if you dont have the id)",true)
        }
    )
}