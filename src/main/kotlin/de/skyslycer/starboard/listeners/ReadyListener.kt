package de.skyslycer.starboard.listeners

import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import java.util.logging.Logger

class ReadyListener(private val logger: Logger) : ListenerAdapter() {
    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.updateCommands().addCommands(
            CommandData("config", "Change this guild's settings")
                .addSubcommands(
                    SubcommandData("channel", "Set the starboard channel").addOption(
                        OptionType.CHANNEL, "channel", "The new starboard channel", true
                    ),
                    SubcommandData("reaction", "Set the reaction").addOption(
                        OptionType.STRING, "emoji", "The new reaction for the starboard (Needs to be a discord emoji)", true
                    ),
                    SubcommandData("needed", "Set the needed amount for a message to get added to the starboard").addOption(
                        OptionType.INTEGER, "amount", "The new amount", true
                    ),
                    SubcommandData("name-regex", "Set the name regex").addOption(
                        OptionType.STRING, "regex", "The new name regex", true
                    ),
                    SubcommandData("invalid-name", "Set the name a member gets when using a not matching name").addOption(
                        OptionType.STRING, "name", "The new name for members", true
                    ),
                    SubcommandData("regex-enabled", "Enable/disable the regex checker").addOption(
                        OptionType.BOOLEAN, "enabled", "If the regex checker should be enabled", true
                    )
                )
        ).queue()
    }
}