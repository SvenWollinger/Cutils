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
    //Loaded commands
    private val commands: Array<BaseCommand> = arrayOf(
        AvatarCommand,
        SayCommand,
        PronounCommand,
        InfoCommand
    )

    //Registered Commands (labels/names as keys)
    private val registered = HashMap<String, BaseCommand>()

    fun register() {
        ArrayList<CommandData>().also {
            commands.forEach { cmd ->
                cmd.register().forEach { cmdData ->
                    registered[cmdData.name] = cmd
                    it.add(cmdData)
                }
            }
            CutilsBot.jda.updateCommands().addCommands(it).complete()
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val cmd = registered[event.name]
        if(cmd != null && cmd is SlashCommandAdapter) cmd.onSlashCommand(CutilsBot.getServer(event.guild!!.id), event)
    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        val cmd = registered[event.name]
        if(cmd != null && cmd is UserContextAdapter) cmd.onUserContext(CutilsBot.getServer(event.guild!!.id), event)
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        val cmd = registered[event.name]
        if(cmd != null && cmd is MessageContextAdapter) cmd.onMessageContext(CutilsBot.getServer(event.guild!!.id), event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val cmd = registered[event.name]
        if(cmd != null && cmd is CommandAutoCompleteAdapter) cmd.onCommandAutoComplete(CutilsBot.getServer(event.guild!!.id), event)
    }
    override fun onModalInteraction(event: ModalInteractionEvent) {
        val cmd = registered[event.modalId.split("_")[0]]
        if(cmd != null && cmd is ModalListenerAdapter) cmd.onModalInteraction(CutilsBot.getServer(event.guild!!.id), event)
    }
}

fun SlashCommandData.adminOnly() = run { defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR) }
fun CommandData.adminOnly() = run { defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR) }