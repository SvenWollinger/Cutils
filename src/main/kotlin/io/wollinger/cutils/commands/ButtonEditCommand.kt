package io.wollinger.cutils.commands

import io.wollinger.cutils.utils.MessageUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button

object ButtonEditCommandSlash: SlashCommand {
    override val label = "editbutton"

    private fun view(event: SlashCommandInteractionEvent) {
        event.deferReply(true)
        MessageUtils.findMessage(event.guild!!, event.getOption("message-id")!!.asString, {
            val buttons = ArrayList<Button>()
            it.actionRows.forEach { ar -> ar.forEach { c -> if(c is Button) buttons.add(c) } }
            var message = "Message ID: ```${it.id}```"
            buttons.forEachIndexed { i, btn ->
                message += "Button ${i + 1}: ${btn.label}, ID: ```${btn.id}```\n"
            }
            event.queueReply(message, true)
        }, {
            event.queueReply("Message not found.", true)
        })
    }

    private fun delete(event: SlashCommandInteractionEvent) {
        MessageUtils.findMessage(event.guild!!, event.getOption("message-id")!!.asString, {
            MessageUtils.removeButton(it, event.getOption("button-id")!!.asString, {
                event.queueReply("Button deleted.", true)
            }, {
                event.queueReply("Button not found.", true)
            })
        }, {
            event.queueReply("Message not found.", true)
        })
    }

    override fun run(event: SlashCommandInteractionEvent) {
        when(event.subcommandName) {
            "view" -> view(event)
            "delete" -> delete(event)
        }
    }

    override fun getCommandData() = Commands.slash(label, "Edit buttons of a message").also {
        it.isGuildOnly = true
        it.adminOnly()
        
        it.addSubcommands(
            SubcommandData.fromData(OptionData(OptionType.STRING, "view", "View buttons", true).toData())
                .addOption(OptionType.STRING, "message-id", "Message ID", true)
        )

        it.addSubcommands(
            SubcommandData.fromData(OptionData(OptionType.STRING, "delete", "Delete button", true).toData())
                .addOption(OptionType.STRING, "message-id", "Message ID", true)
                .addOption(OptionType.STRING, "button-id", "Button ID", true)
        )
    }
}