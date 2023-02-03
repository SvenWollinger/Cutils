package io.wollinger.cutils.commands

import io.wollinger.cutils.CutilsBot
import io.wollinger.cutils.utils.MessageUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.messages.MessageEditData

object ButtonRoleCommandSlash: SlashCommand {
    override val label = "br"

    override fun run(event: SlashCommandInteractionEvent) {
        val messageID = event.getOption("message-id")!!.asString
        val text = event.getOption("text")!!.asString
        val role = event.getOption("role")!!.asRole

        if(text.length > Button.LABEL_MAX_LENGTH) {
            event.reply("Text too long!").setEphemeral(true).queue()
            return
        }

        if(messageID.toLongOrNull() == null) {
            event.reply("Bad id!").setEphemeral(true).queue()
            return
        }

        MessageUtils.findMessage(event.guild!!, messageID, { message ->
            if(message.author.id != CutilsBot.id) {
                event.reply("Message must be sent by me!").setEphemeral(true).queue()
                return@findMessage
            }

            val buttonID = "cutils-br-$role"
            val items = ArrayList<ItemComponent>()
            message.actionRows.forEach { row ->
                row.forEach { item ->
                    items.add(item)
                    if(item is Button) {
                        if(item.id == buttonID) {
                            event.reply("Button already exists with that role!").setEphemeral(true).queue()
                            return@findMessage
                        }
                    }
                }
            }

            if(items.size >= Message.MAX_COMPONENT_COUNT) {
                event.reply("Maximum number of buttons reached.").setEphemeral(true).queue()
                return@findMessage
            }

            items.add(Button.primary(buttonID, text))

            message.editMessage(MessageEditData.fromMessage(message)).setActionRow(items).queue({
                event.reply("Button added.").setEphemeral(true).queue()
            }, { t ->
                println()
                event.reply("Button could not be added. ${t.message}").setEphemeral(true).queue()
            })
        }, {
            event.reply("Message not found.").setEphemeral(true).queue()
        })
    }

    override fun getCommandData() = Commands.slash(label, "Add a role button").also {
        it.isGuildOnly = true
        it.defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
        it.addOption(OptionType.STRING, "message-id", "Message to add to", true)
        it.addOption(OptionType.STRING, "text", "Button text", true)
        it.addOption(OptionType.ROLE, "role", "Role to add", true)
    }
}

object ReactionRoleCommandSlash: SlashCommand {
    override val label = "rr"

    override fun run(event: SlashCommandInteractionEvent) {
        val messageID = event.getOption("message-id")!!.asString
        val emoji = Emoji.fromFormatted(event.getOption("emoji")!!.asString)
        val role = event.getOption("role")!!.asRole

        if(messageID.toLongOrNull() == null) {
            event.reply("Bad id!").setEphemeral(true).queue()
            return
        }

        MessageUtils.findMessage(event.guild!!, messageID, {
            it.addReaction(emoji).queue({
                event.reply("Done!").setEphemeral(true).queue()
                //TODO: Add emoji to database
            }, {
                event.reply("Could not add emoji. (Bad emoji?)").setEphemeral(true).queue()
            })
        }, {
            event.reply("Message not found.").setEphemeral(true).queue()
        })
    }

    override fun getCommandData() = Commands.slash(label, "Add a role reaction").also {
        it.isGuildOnly = true
        it.defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
        it.addOption(OptionType.STRING, "message-id", "Message to add to", true)
        it.addOption(OptionType.STRING, "emoji", "Emoji to use", true)
        it.addOption(OptionType.ROLE, "role", "Role to add", true)
    }
}