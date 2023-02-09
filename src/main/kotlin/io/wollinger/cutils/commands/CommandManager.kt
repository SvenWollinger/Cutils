package io.wollinger.cutils.commands

import io.wollinger.cutils.CutilsBot
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

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
        it[BirthdayCommandSlash.label] = BirthdayCommandSlash
        it[PronounCommandSlash.label] = PronounCommandSlash
        it[PronounCommandContext.name] = PronounCommandContext
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

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = (commands[event.name] as SlashCommand).run(CutilsBot.getServer(event.guild!!.id), event)
    override fun onUserContextInteraction(event: UserContextInteractionEvent): Unit = (commands[event.name] as ContextUserCommand).run(CutilsBot.getServer(event.guild!!.id), event)
    override fun onMessageContextInteraction(event: MessageContextInteractionEvent): Unit = (commands[event.name] as ContextMessageCommand).run(CutilsBot.getServer(event.guild!!.id), event)
    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val cmd = commands[event.name]
        if(cmd != null && cmd is AutoCompleteListener) cmd.onAutoComplete(CutilsBot.getServer(event.guild!!.id), event)
    }
    override fun onModalInteraction(event: ModalInteractionEvent) {
        val cmd = commands[event.modalId.split("_")[0]]
        if(cmd != null && cmd is ModalListener) cmd.onModal(CutilsBot.getServer(event.guild!!.id), event)
    }
}

fun SlashCommandData.adminOnly() = run { defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR) }
fun CommandData.adminOnly() = run { defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR) }