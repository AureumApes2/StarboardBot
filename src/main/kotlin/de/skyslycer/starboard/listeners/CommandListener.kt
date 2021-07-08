package de.skyslycer.starboard.listeners

import de.skyslycer.starboard.configuration.Command
import de.skyslycer.starboard.configuration.CommandLanguage
import de.skyslycer.starboard.configuration.Guilds
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger

class CommandListener(guilds: Guilds, private val language: CommandLanguage, private val logger: Logger) : ListenerAdapter() {
    private val commands = Command(guilds, logger, language)

    override fun onSlashCommand(event: SlashCommandEvent) {
        if (event.guild == null) {
            event.deferReply(true).setContent(language.slashCommandError).queue()
            return
        }

        if (event.member == null) {
            event.deferReply(true).setContent(language.slashCommandError).queue()
            return
        }

        if (!event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
            sendNoPermissionMessage(event)
            return
        }

        when (event.subcommandName) {
            "channel" -> commands.changeStarboardChannel(event)
            "needed" -> commands.changeNeededReactionAmount(event)
            "name-regex" -> commands.changeNameRegex(event)
            "invalid-name" -> commands.changeInvalidName(event)
            "regex-enabled" -> commands.changeRegexNameChecker(event)
            "reaction" -> commands.changeStarEmoji(event)
            else -> {
                logger.info("Broken command found: ${event.subcommandName} [Guild:${event.guild!!.id}]")
                event.deferReply(true).setContent(language.slashCommandError).queue()
            }
        }
    }

    private fun sendNoPermissionMessage(event: SlashCommandEvent) {
        event.deferReply(true).setContent(language.noPermission).queue()
    }
}