package io.wollinger.cutils.commands

import io.wollinger.cutils.CutilsBot
import io.wollinger.cutils.utils.MessageUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.messages.MessageEditData
import java.util.concurrent.CopyOnWriteArrayList

object ButtonRoleCommandSlash: SlashCommand {
    override val label = "buttonrole"

    private fun toButtonID(roleID: String) = "cutils-buttonrole-$roleID"
    private fun toRoleID(buttonID: String) = buttonID.split("-")[2]

    private fun add(event: SlashCommandInteractionEvent) {
        val messageID = event.getOption("message-id")!!.asString
        val role = event.getOption("role")!!.asRole
        val text = event.getOption("text")?.asString ?: role.name

        fun reply(msg: String) = event.reply(msg).setEphemeral(true).queue()

        if(text.length > Button.LABEL_MAX_LENGTH) {
            reply("Text too long!")
            return
        }

        if(messageID.toLongOrNull() == null) {
            reply("Bad id!")
            return
        }

        MessageUtils.findMessage(event.guild!!, messageID, { message ->
            if(message.author.id != CutilsBot.id) {
                reply("Message must be sent by me!")
                return@findMessage
            }

            val buttonID = toButtonID(role.id)
            val items = ArrayList<ItemComponent>()
            message.actionRows.forEach { row ->
                row.forEach { item ->
                    if(item is Button) {
                        items.add(item)
                        if(item.id == buttonID) {
                            reply("Button already exists with that role!")
                            return@findMessage
                        }
                    }
                }
            }

            if(items.size >= Message.MAX_COMPONENT_COUNT) {
                reply("Maximum number of buttons reached.")
                return@findMessage
            }

            items.add(Button.primary(buttonID, text))

            message.editMessage(MessageEditData.fromMessage(message)).setActionRow(items).queue({
                reply("Button added.")
            }, { t ->
                reply("Button could not be added. ${t.message}")
            })
        }, {
            reply("Message not found.")
        })
    }

    private fun remove(event: SlashCommandInteractionEvent) {
        val messageID = event.getOption("message-id")!!.asString
        val roleID = event.getOption("role-id")!!.asString

        MessageUtils.findMessage(event.guild!!, messageID, {
            val response = if(MessageUtils.removeButton(it, toButtonID(roleID))) "Button removed!" else "Button with that role not found."
            event.reply(response).setEphemeral(true).queue()
        }, {
            event.reply("Message not found.").setEphemeral(true).queue()
        })
    }

    override fun run(event: SlashCommandInteractionEvent) {
        when(event.subcommandName) {
            "add" -> add(event)
            "remove" -> remove(event)
        }
    }

    override fun getCommandData() = Commands.slash(label, "Add a role button").also {
        it.isGuildOnly = true
        it.defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)

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
}