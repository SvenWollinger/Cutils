package io.wollinger.cutils.commands

import io.wollinger.cutils.server.Server
import io.wollinger.cutils.utils.TimestampType
import io.wollinger.cutils.utils.queueReply
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import java.lang.Exception
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters
import java.util.TimeZone

object BirthdayCommandSlash: SlashCommand, AutoCompleter {
    override val label = "birthday"

    override fun run(server: Server, event: SlashCommandInteractionEvent) {
        val timeZone = event.getOption("timezone")!!.asString
        val day = event.getOption("day")!!.asString
        val month = event.getOption("month")!!.asString
        val year = event.getOption("year")!!.asString

        try {
            val zone = ZoneId.of(timeZone)
            try {
                val date = ZonedDateTime.of(LocalDateTime.of(year.toInt(), month.toInt(), day.toInt(), 0, 0), zone)
                server.userManager.getUserData(event.user.id).birthdayUnix = date.toEpochSecond()
                event.queueReply("Set birthday to: ${TimestampType.LONG_DATE.format(date)}", true)
            } catch (_: Exception) {
                event.queueReply("Bad date!", true)
                return
            }
        } catch (_: Exception) {
            event.queueReply("Bad timezone!", true)
            return
        }
    }

    override fun getCommandData() = Commands.slash(label, "Manage birthday settings").also {
        it.addSubcommands(
            SubcommandData.fromData(OptionData(OptionType.STRING, "set", "Set birthday", true).toData())
                .addOption(OptionType.STRING, "timezone", "Timezone (eg. \"Europe/Berlin\")", true, true)
                .addOption(OptionType.STRING, "day", "Day", true)
                .addOption(OptionType.STRING, "month", "Month", true)
                .addOption(OptionType.STRING, "year", "Year", true)
        )

        it.addSubcommands(SubcommandData.fromData(OptionData(OptionType.STRING, "remove", "Remove birthday data", true).toData()))
    }

    override fun onAutoComplete(server: Server, event: CommandAutoCompleteInteractionEvent) {
        val currentInput = event.getOption("timezone")!!.asString
        val options = TimeZone.getAvailableIDs()
        event.replyChoiceStrings(options.filter { it.contains(currentInput) }.take(25)).queue()
    }
}