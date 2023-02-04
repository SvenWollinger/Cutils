package io.wollinger.cutils.commands

import io.wollinger.cutils.utils.MessageUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button

object ButtonEditCommandSlash: SlashCommand {
    override val label = "editbutton"

    override fun run(event: SlashCommandInteractionEvent) {
        event.deferReply(true)
        MessageUtils.findMessage(event.guild!!, event.getOption("message-id")!!.asString, {
            val buttons = ArrayList<Button>()
            it.actionRows.forEach { ar -> ar.forEach { c -> if(c is Button) buttons.add(c) } }
            var message = ""
            val removeButtons = ArrayList<Button>()
            buttons.forEachIndexed { i, btn ->
                message += "Button ${i + 1}: ${btn.label}, ID: ${btn.id}"
                removeButtons.add(Button.danger("cutils-editbutton-remove-$i", "Remove Button ${i + 1}"))
            }
            event.reply(message).setEphemeral(true).addActionRow(removeButtons).queue()
        }, {
            event.reply("Message not found.").setEphemeral(true).queue()
        })
    }

    override fun getCommandData() = Commands.slash(label, "Edit buttons of a message").also {
        it.isGuildOnly = true
        it.defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
        it.addOption(OptionType.STRING, "message-id", "Message to edit", true)
    }
}