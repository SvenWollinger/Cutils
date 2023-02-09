package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

object SayCommand: SlashCommand, ModalListener {
    override val label = "say"

    override fun run(server: Server, event: SlashCommandInteractionEvent) {
        val message = event.getOption("text")?.asString
        if(message != null) {
            event.channel.sendMessage(message).queue()
            event.queueReply("Sent!", true)
            return
        }

        val body = TextInput.create("message", "Message", TextInputStyle.PARAGRAPH).also {
            it.placeholder = "Message"
            it.minLength = 1
            it.maxLength = TextInput.MAX_VALUE_LENGTH
        }.build()

        val modal = Modal.create("${label}_say", "Send message").addActionRow(body).build()
        event.replyModal(modal).queue()
    }

    override fun getCommandData() = Commands.slash(label, "Say something as the bot").also {
        it.adminOnly()
        it.addOption(OptionType.STRING, "text", "What you want to say", false)
    }

    override fun onModal(server: Server, event: ModalInteractionEvent) {
        event.channel.sendMessage(event.getValue("message")!!.asString).queue()
        event.reply("Sent!").setEphemeral(true).queue()
    }
}