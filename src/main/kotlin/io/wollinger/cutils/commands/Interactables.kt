package io.wollinger.cutils.commands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface Interactable {
    fun getCommandData(): CommandData
}

interface SlashCommand: Interactable {
    val label: String
    fun run(event: SlashCommandInteractionEvent)
}

interface ContextUserCommand: Interactable {
    val name: String
    fun run(event: UserContextInteractionEvent)
}