package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface BaseCommand {
    fun register(): Array<CommandData>
}

interface SlashCommandAdapter {
    val slashCommandLabel: String
    fun onSlashCommand(server: Server, event: SlashCommandInteractionEvent)
}

interface UserContextAdapter {
    val userContextLabel: String
    fun onUserContext(server: Server, event: UserContextInteractionEvent)
}

interface MessageContextAdapter {
    val messageContextLabel: String
    fun onMessageContext(server: Server, event: MessageContextInteractionEvent)
}

interface CommandAutoCompleteAdapter {
    fun onCommandAutoComplete(server: Server, event: CommandAutoCompleteInteractionEvent)
}

interface ModalListenerAdapter {
    fun onModalInteraction(server: Server, event: ModalInteractionEvent)
}

interface ButtonListenerAdapter {
    fun onButtonInteraction(server: Server, event: ButtonInteractionEvent)
}