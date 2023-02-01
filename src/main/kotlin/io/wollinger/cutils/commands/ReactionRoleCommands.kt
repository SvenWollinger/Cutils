package io.wollinger.cutils.commands

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

private val templateEmojis = listOf(":rat:", ":smile:", ":apple:", ":thinking_face:")

private fun getModal(message: String?, channel: String): Modal {
    val modal = Modal.create("roleemoji-modal", ReactionRoleCommandContext.name)
    val channelID = TextInput.create("channel", "Channel id", TextInputStyle.SHORT).also {
        it.value = channel
    }.build()
    val messageID = TextInput.create("message", "Message id", TextInputStyle.SHORT).also {
        it.value = message
    }.build()
    val emojiInput = TextInput.create("emoji", "Emoji", TextInputStyle.SHORT).also {
        it.placeholder = templateEmojis[(templateEmojis.indices).random()]
        it.setRequiredRange(2, 32)
    }.build()
    modal.addActionRow(channelID)
    modal.addActionRow(messageID)
    modal.addActionRow(emojiInput)
    return modal.build()
}

object ReactionRoleCommandContext: ContextMessageCommand {
    override val name = "Add role emoji"

    override fun run(event: MessageContextInteractionEvent) {
        event.replyModal(getModal(event.target.id, event.target.channel.id)).queue()
    }

    override fun getCommandData() = Commands.context(Command.Type.MESSAGE, name).also {
        it.isGuildOnly = true
        it.defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
    }
}

object ReactionRoleCommandSlash: SlashCommand {
    override val label = "rr"

    override fun run(event: SlashCommandInteractionEvent) {
        event.replyModal(getModal(null, event.channel.id)).queue()
    }

    override fun getCommandData() = Commands.slash(label, "Add a role reaction").also {
        it.isGuildOnly = true
        it.defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
    }
}