package io.wollinger.cutils.commands

import io.wollinger.cutils.CutilsBot
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object CommandManager: ListenerAdapter() {
    private val commands = HashMap<String, Interactable>().also {
        it[InfoCommandSlash.label] = InfoCommandSlash
        it[InfoCommandContext.name] = InfoCommandContext
        it[AvatarCommandContext.name] = AvatarCommandContext
        it[ReactionRoleCommandSlash.label] = ReactionRoleCommandSlash
        it[ButtonRoleCommandSlash.label] = ButtonRoleCommandSlash
        it[SayCommand.label] = SayCommand
        it[ButtonEditCommandSlash.label] = ButtonEditCommandSlash
        it[GetMessageIDCommandContext.name] = GetMessageIDCommandContext
    }

    fun register() {
        ArrayList<CommandData>().also {
            commands.forEach { (_, cmd) ->
                val cmdData = cmd.getCommandData()
                if(cmdData != null) it.add(cmdData)
            }
            CutilsBot.jda.updateCommands().addCommands(it).complete()
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = (commands[event.name] as SlashCommand).run(event)
    override fun onUserContextInteraction(event: UserContextInteractionEvent): Unit = (commands[event.name] as ContextUserCommand).run(event)
    override fun onMessageContextInteraction(event: MessageContextInteractionEvent): Unit = (commands[event.name] as ContextMessageCommand).run(event)
    override fun onModalInteraction(event: ModalInteractionEvent): Unit = (commands[event.modalId] as ModalInteractionCommand).run(event)
}