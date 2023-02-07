package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface Interactable {
    fun getCommandData(): CommandData?
}

interface AutoCompleter {
    fun onAutoComplete(server: Server, event: CommandAutoCompleteInteractionEvent)
}

interface SlashCommand: Interactable {
    val label: String
    fun run(server: Server, event: SlashCommandInteractionEvent)
}

interface ContextUserCommand: Interactable {
    val name: String
    fun run(server: Server, event: UserContextInteractionEvent)
}

interface ContextMessageCommand: Interactable {
    val name: String
    fun run(server: Server, event: MessageContextInteractionEvent)
}

interface ModalInteractionCommand: Interactable {
    val id: String
    fun run(server: Server, event: ModalInteractionEvent)
}