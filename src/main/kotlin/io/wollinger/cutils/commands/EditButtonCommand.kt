package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.MessageUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button

object EditButtonCommand: BaseCommand, SlashCommandAdapter, ButtonListenerAdapter {
    override val slashCommandLabel = "editbutton"

    private fun toDeleteID(messageID: String, buttonID: String) = "${slashCommandLabel}_${messageID}_${buttonID}"
    data class Parts(val messageID: String, val buttonID: String)
    private fun getParts(buttonID: String): Parts {
        val parts = buttonID.split("_")
        val label = parts[0]
        val messageID = parts[1]
        val toDelete = buttonID.replace("${label}_${messageID}_", "")
        return Parts(messageID, toDelete)
    }

    private fun view(event: SlashCommandInteractionEvent) {
        event.deferReply(true)
        MessageUtils.findMessage(event.guild!!, event.getOption("message-id")!!.asString, { message ->
            val buttons = ArrayList<Button>()
            message.actionRows.forEach { ar -> ar.forEach { c -> if(c is Button) buttons.add(c) } }
            var reply = "Message ID: ```${message.id}```"
            val deleteButtons = ArrayList<Button>()
            buttons.forEachIndexed { i, btn ->
                deleteButtons.add(Button.danger(toDeleteID(message.id, btn.id!!), "Delete Button ${i + 1}"))
                reply += "Button ${i + 1}: ${btn.label}, ID: ```${btn.id}```\n"
            }
            if(buttons.size == 0) reply += "No buttons"
            event.reply(reply).setEphemeral(true).setActionRow(deleteButtons).queue()
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

    override fun onSlashCommand(server: Server, event: SlashCommandInteractionEvent) {
        when(event.subcommandName) {
            "view" -> view(event)
            "delete" -> delete(event)
        }
    }

    override fun onButtonInteraction(server: Server, event: ButtonInteractionEvent) {
        val parts = getParts(event.button.id!!)
        MessageUtils.findMessage(event.guild!!, parts.messageID, { message ->
            MessageUtils.removeButton(message, parts.buttonID, {
                event.reply("Deleted Button!").setEphemeral(true).queue()
                event.editButton(Button.danger(event.button.id!!, event.button.label).asDisabled()).queue()
            }, {
                event.reply("Could not delete button! Reason: ${it.message}").setEphemeral(true).queue()
            })
        }, {
            event.reply("Message could not be found!").setEphemeral(true).queue()
        })
    }

    override fun register(): Array<CommandData> = arrayOf(
        Commands.slash(slashCommandLabel, "Edit buttons of a message").also {
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
    )
}