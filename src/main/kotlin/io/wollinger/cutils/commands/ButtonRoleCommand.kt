package io.wollinger.cutils.commands

import io.wollinger.cutils.CutilsBot
import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.MessageUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.messages.MessageEditData

object ButtonRoleCommand: BaseCommand, SlashCommandAdapter {
    override val slashCommandLabel = "buttonrole"

    private fun toButtonID(roleID: String) = "cutils-buttonrole-$roleID"
    private fun toRoleID(buttonID: String) = buttonID.split("-")[2]

    private fun add(event: SlashCommandInteractionEvent) {
        val messageID = event.getOption("message-id")!!.asString
        val role = event.getOption("role")!!.asRole
        val text = event.getOption("text")?.asString ?: role.name

        if(text.length > Button.LABEL_MAX_LENGTH) {
            event.queueReply("Text too long!", true)
            return
        }

        if(messageID.toLongOrNull() == null) {
            event.queueReply("Bad id!", true)
            return
        }

        MessageUtils.findMessage(event.guild!!, messageID, { message ->
            if(message.author.id != CutilsBot.id) {
                event.queueReply("Message must be sent by me!", true)
                return@findMessage
            }

            val buttonID = toButtonID(role.id)
            val items = ArrayList<ItemComponent>()
            message.actionRows.forEach { row ->
                row.forEach { item ->
                    if(item is Button) {
                        items.add(item)
                        if(item.id == buttonID) {
                            event.queueReply("Button already exists with that role!", true)
                            return@findMessage
                        }
                    }
                }
            }

            if(items.size >= Message.MAX_COMPONENT_COUNT) {
                event.queueReply("Maximum number of buttons reached.", true)
                return@findMessage
            }

            items.add(Button.primary(buttonID, text))

            message.editMessage(MessageEditData.fromMessage(message)).setActionRow(items).queue({
                event.queueReply("Button added.", true)
            }, { t ->
                event.queueReply("Button could not be added. ${t.message}", true)
            })
        }, {
            event.queueReply("Message not found.", true)
        })
    }

    private fun remove(event: SlashCommandInteractionEvent) {
        val messageID = event.getOption("message-id")!!.asString
        val roleID = event.getOption("role-id")!!.asString

        MessageUtils.findMessage(event.guild!!, messageID, {
            MessageUtils.removeButton(it, toButtonID(roleID), {
                event.queueReply("Button removed!", true)
            }, {
                event.queueReply("Button with that role not found.", true)
            })
        }, {
            event.queueReply("Message not found.", true)
        })
    }

    override fun onSlashCommand(server: Server, event: SlashCommandInteractionEvent) {
        when(event.subcommandName) {
            "add" -> add(event)
            "remove" -> remove(event)
        }
    }

    override fun register(): Array<CommandData> = arrayOf(
        Commands.slash(slashCommandLabel, "Add a role button").also {
            it.isGuildOnly = true
            it.adminOnly()

            it.addSubcommands(
                SubcommandData.fromData(OptionData(OptionType.STRING, "add", "Add a role button", true).toData())
                    .addOption(OptionType.STRING, "message-id", "Message to add to", true)
                    .addOption(OptionType.ROLE, "role", "Role to add", true)
                    .addOption(OptionType.STRING, "text", "Button text", false)
            )

            it.addSubcommands(
                SubcommandData.fromData(OptionData(OptionType.STRING, "remove", "Remove a role button", true).toData())
                    .addOption(OptionType.STRING, "message-id", "Message ID", true)
                    .addOption(OptionType.STRING, "role-id", "ID of Role to remove", true)
            )
        }
    )
}