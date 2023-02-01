package io.wollinger.cutils.commands

import io.wollinger.cutils.CutilsBot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object CommandManager: ListenerAdapter() {
    private val commands = HashMap<String, Interactable>().also {
        it[InfoCommandSlash.label] = InfoCommandSlash
        it[InfoCommandContext.name] = InfoCommandContext
        it[AvatarCommandContext.name] = AvatarCommandContext
    }

    fun register() {
        ArrayList<CommandData>().also {
            commands.forEach { (_, cmd) -> it.add(cmd.getCommandData()) }
            CutilsBot.jda.updateCommands().addCommands(it).complete()
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = (commands[event.name] as SlashCommand).run(event)
    override fun onUserContextInteraction(event: UserContextInteractionEvent): Unit = (commands[event.name] as ContextUserCommand).run(event)
}