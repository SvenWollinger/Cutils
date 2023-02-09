package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.MessageUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

object EmojiRoleCommand: BaseCommand, SlashCommandAdapter {
    override val slashCommandLabel = "emojirole"

    override fun onSlashCommand(server: Server, event: SlashCommandInteractionEvent) {
        val messageID = event.getOption("message-id")!!.asString
        val emoji = Emoji.fromFormatted(event.getOption("emoji")!!.asString)
        val role = event.getOption("role")!!.asRole

        if(messageID.toLongOrNull() == null) {
            event.queueReply("Bad id!", true)
            return
        }

        MessageUtils.findMessage(event.guild!!, messageID, {
            it.addReaction(emoji).queue({
                event.queueReply("Done!", true)
                server.rmm.addReaction(messageID, emoji.toString(), role.id)
            }, {
                event.queueReply("Could not add emoji. (Bad emoji?)", true)
            })
        }, {
            event.queueReply("Message not found.", true)
        })
    }

    override fun register(): Array<CommandData> = arrayOf(
        Commands.slash(slashCommandLabel, "Add a role reaction").also {
            it.isGuildOnly = true
            it.adminOnly()
            it.addOption(OptionType.STRING, "message-id", "Message to add to", true)
            it.addOption(OptionType.STRING, "emoji", "Emoji to use", true)
            it.addOption(OptionType.ROLE, "role", "Role to add", true)
        }
    )
}