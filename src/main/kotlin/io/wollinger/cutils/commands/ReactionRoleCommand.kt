package io.wollinger.cutils.commands

import io.wollinger.cutils.utils.MessageUtils
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

object ReactionRoleCommandSlash: SlashCommand {
    override val label = "rr"

    override fun run(event: SlashCommandInteractionEvent) {
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
                //TODO: Add emoji to database
            }, {
                event.queueReply("Could not add emoji. (Bad emoji?)", true)
            })
        }, {
            event.queueReply("Message not found.", true)
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