package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

private fun view(server: Server, member: Member): String {
    val data = server.userManager.getUserData(member.id).pronouns
    if(data.isEmpty()) return "${member.effectiveName} has no pronouns set."
    var response = "${member.effectiveName}'s Pronouns: (Use in ascending order)\n"
    data.toList().sortedBy { (_, index) -> index}.forEach { (pronoun, _) ->
        response += "$pronoun\n"
    }
    return response
}

object PronounCommandContext: ContextUserCommand {
    override val name = "View Pronouns"

    override fun run(server: Server, event: UserContextInteractionEvent) = event.queueReply(view(server, event.targetMember!!), true)

    override fun getCommandData() = Commands.context(Command.Type.USER, name).also { it.isGuildOnly = true }
}

object PronounCommandSlash: SlashCommand, AutoCompleteListener {
    override val label = "pronouns"
    private val pronouns = listOf(
        "Any/All",
        "He/Him",
        "She/Her",
        "They/Them",
        "Undecided",
        "Ask"
    )

    override fun run(server: Server, event: SlashCommandInteractionEvent) {
        when(event.subcommandName) {
            "view" -> event.queueReply(view(server, event.getOption("user")!!.asMember!!), true)
            "add", "remove" -> {
                val pronoun = event.getOption("pronoun")!!.asString
                val data = server.userManager.getUserData(event.user.id)

                when (event.subcommandName) {
                    "add" -> data.pronouns[pronoun] = event.getOption("priority")?.asInt ?: 0
                    "remove" -> data.pronouns.remove(pronoun)
                }

                event.queueReply("Pronouns updated!", true)
            }
        }
    }

    override fun getCommandData() = Commands.slash(label, "Manage pronouns").also {
        it.isGuildOnly = true

        it.addSubcommands(
            SubcommandData.fromData(OptionData(OptionType.STRING, "view", "View someones Pronouns", true).toData())
                .addOption(OptionType.USER, "user", "User", true)
        )

        it.addSubcommands(
            SubcommandData.fromData(OptionData(OptionType.STRING, "add", "Add a pronoun", true).toData())
                .addOption(OptionType.STRING, "pronoun", "Pronoun", true, true)
                .addOption(OptionType.INTEGER, "priority", "Priority", false)
        )

        it.addSubcommands(
            SubcommandData.fromData(OptionData(OptionType.STRING, "remove", "Remove a pronoun", true).toData())
                .addOption(OptionType.STRING, "pronoun", "Pronoun", true, true)
        )
    }

    override fun onAutoComplete(server: Server, event: CommandAutoCompleteInteractionEvent) {
        when(event.subcommandName) {
            "add" -> event.replyChoiceStrings(pronouns.filter { it.lowercase().startsWith(event.getOption(event.focusedOption.name)!!.asString.lowercase()) }.take(25)).queue()
            "remove" -> event.replyChoiceStrings(server.userManager.getUserData(event.user.id).pronouns.keys.toList()).queue()
        }
    }
}