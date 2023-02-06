package io.wollinger.cutils.commands

import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

object SayCommand: SlashCommand {
    override val label = "say"

    override fun run(event: SlashCommandInteractionEvent) {
        event.channel.sendMessage(event.getOption("text")!!.asString).queue()
        event.queueReply("Sent!", true)
    }

    override fun getCommandData() = Commands.slash(label, "Say something as the bot").also {
        it.defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
        it.addOption(OptionType.STRING, "text", "What you want to say", true)
    }
}