package io.wollinger.cutils.commands

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
    fun onAutoComplete(event: CommandAutoCompleteInteractionEvent)
}

interface SlashCommand: Interactable {
    val label: String
    fun run(event: SlashCommandInteractionEvent)
}

interface ContextUserCommand: Interactable {
    val name: String
    fun run(event: UserContextInteractionEvent)
}

interface ContextMessageCommand: Interactable {
    val name: String
    fun run(event: MessageContextInteractionEvent)
}

interface ModalInteractionCommand: Interactable {
    val id: String
    fun run(event: ModalInteractionEvent)
}