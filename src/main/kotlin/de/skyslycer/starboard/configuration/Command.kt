package de.skyslycer.starboard.configuration

import com.vdurmont.emoji.EmojiParser
import de.skyslycer.starboard.StarboardBot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.slf4j.Logger
import java.awt.Color

class Command(private val guilds: Guilds, private val logger: Logger, private val language: CommandLanguage) {
    fun changeStarboardChannel(event: SlashCommandEvent) {
        logger.info("User ${event.user.id} ran command to change the starboard in channel ${event.channel.idLong} [Guild:${event.guild!!.id}]")
        if (event.getOption("channel")?.asMessageChannel == null) {
            logger.info("The needed option isn't a text channel or the option is gone :/ Aborting. [Guild:${event.guild!!.id}]")
            event.replyEmbeds(
                buildErrorEmbed(language.channel["no-text-channel"] ?: "Dies ist kein Text-Channel!").build()
            ).setEphemeral(true).queue()
            return
        }

        val channel = event.getOption("channel")!!.asMessageChannel!!
        val guild = event.guild!!

        if (!guilds.containsKey(event.guild!!.idLong)) {
            logger.info("Creating new guild settings for guild ${guild.id}")
            guilds[guild.idLong] = StarboardBot.createDefaultGuildSettings(guild.idLong, channel.idLong)
        } else {
            guilds[guild.idLong]!!.starboardChannel = channel.idLong
        }

        StarboardBot.saveGuilds(guilds)

        event.replyEmbeds(
            buildDoneEmbed(
                language.channel["success-description"] ?: "Du hast den Starboard-Channel erfolgreich gesetzt!",
                language.channel["success-name"] ?: "Starboard-Channel",
                "<#${channel.id}>"
            ).build()
        )
            .setEphemeral(true).queue()

        logger.info("Starboard channel got set: ${channel.id} [Guild:${event.guild!!.id}]")
    }

    fun changeStarEmoji(event: SlashCommandEvent) {
        logger.info("User ${event.user.id} ran command to change the reaction emote in channel ${event.channel.idLong} [Guild:${event.guild!!.id}]")
        if (event.getOption("emoji")?.asString == null) {
            logger.info("The needed option is gone :/ Aborting. [Guild:${event.guild!!.id}]")
            event.deferReply(true).setContent(language.slashCommandError).queue()
            return
        }

        if (EmojiParser.extractEmojis(event.getOption("emoji")!!.asString).isEmpty()) {
            logger.info("User typed invalid emoji: ${event.getOption("emoji")!!.asString}. Aborting. [Guild:${event.guild!!.id}]")
            event.replyEmbeds(
                buildErrorEmbed(
                    language.emoji["no-emoji"]
                        ?: "Dein angegebenes Emoji ist kein Emoji, das global auf Discord verfügbar ist!"
                ).build()
            ).setEphemeral(true).queue()
            return
        }

        val emoji = EmojiParser.extractEmojis(event.getOption("emoji")!!.asString)[0]
        val guild = event.guild!!

        if (!guilds.containsKey(event.guild!!.idLong)) {
            logger.info("Creating new guild settings for guild ${guild.id}")
            guilds[guild.idLong] = StarboardBot.createDefaultGuildSettings(guild.idLong, event.channel.idLong)
        }

        guilds[guild.idLong]!!.starUnicode = emoji

        StarboardBot.saveGuilds(guilds)

        event.replyEmbeds(
            buildDoneEmbed(
                language.emoji["success-description"] ?: "Du hast das Reaction Emoji erfolgreich gesetzt!",
                language.emoji["success-name"] ?: "Emoji",
                emoji
            ).build()
        ).setEphemeral(true).queue()

        logger.info("Needed reaction emoji got set: $emoji [Guild:${event.guild!!.id}]")
    }

    fun changeNeededReactionAmount(event: SlashCommandEvent) {
        logger.info("User ${event.user.id} ran command to change the needed reaction amount in channel ${event.channel.idLong} [Guild:${event.guild!!.id}]")
        if (event.getOption("amount")?.asLong == null) {
            logger.info("The needed option is gone :/ Aborting. [Guild:${event.guild!!.id}]")
            event.deferReply(true).setContent(language.slashCommandError).queue()
            return
        }

        val amount = event.getOption("amount")!!.asLong
        val guild = event.guild!!

        if (!guilds.containsKey(event.guild!!.idLong)) {
            logger.info("Creating new guild settings for guild ${guild.id}")
            guilds[guild.idLong] = StarboardBot.createDefaultGuildSettings(guild.idLong, event.channel.idLong)
        }

        guilds[guild.idLong]!!.needed = amount.toInt()

        StarboardBot.saveGuilds(guilds)

        event.replyEmbeds(
            buildDoneEmbed(
                language.needed["success-description"] ?: "Du hast die benötigten Reaktionen erfolgreich gesetzt!",
                language.needed["success-name"] ?: "Benötigte Reaktionen",
                amount.toString()
            ).build()
        )
            .setEphemeral(true).queue()

        logger.info("Needed reaction amount got set: $amount [Guild:${event.guild!!.id}]")
    }

    fun changeNameRegex(event: SlashCommandEvent) {
        logger.info("User ${event.user.id} ran command to change the name regex in channel ${event.channel.idLong} [Guild:${event.guild!!.id}]")
        if (event.getOption("regex")?.asString == null) {
            logger.info("The needed option is gone :/ Aborting. [Guild:${event.guild!!.id}]")
            event.deferReply(true).setContent(language.slashCommandError).queue()
            return
        }

        val newRegex = event.getOption("regex")!!.asString
        val guild = event.guild!!

        if (!guilds.containsKey(event.guild!!.idLong)) {
            logger.info("Creating new guild settings for guild ${guild.id}")
            guilds[guild.idLong] = StarboardBot.createDefaultGuildSettings(guild.idLong, event.channel.idLong)
        }

        guilds[guild.idLong]!!.nameRegex = newRegex

        StarboardBot.saveGuilds(guilds)

        event.replyEmbeds(
            buildDoneEmbed(
                language.nameRegex["success-description"] ?: "Du hast den Regex für Benutzernamen erfolgreich gesetzt!",
                language.nameRegex["success-name"] ?: "Regex",
                newRegex
            ).build()
        )
            .setEphemeral(true).queue()

        logger.info("Regex for names got set: $newRegex [Guild:${event.guild!!.id}]")
    }

    fun changeInvalidName(event: SlashCommandEvent) {
        logger.info("User ${event.user.id} ran command to change the invalid name in channel ${event.channel.idLong} [Guild:${event.guild!!.id}]")
        if (event.getOption("name")?.asString == null) {
            logger.info("The needed option is gone :/ Aborting. [Guild:${event.guild!!.id}]")
            event.deferReply(true).setContent(language.slashCommandError).queue()
            return
        }

        val invalidName = event.getOption("name")!!.asString
        val guild = event.guild!!

        if (!guilds.containsKey(event.guild!!.idLong)) {
            logger.info("Creating new guild settings for guild ${guild.id}")
            guilds[guild.idLong] = StarboardBot.createDefaultGuildSettings(guild.idLong, event.channel.idLong)
        }

        guilds[guild.idLong]!!.invalidName = invalidName

        StarboardBot.saveGuilds(guilds)

        event.replyEmbeds(
            buildDoneEmbed(
                language.invalidName["success-description"]
                    ?: "Du hast den Namen für Benutzer mit ungültigem Namen erfolgreich gesetzt!",
                language.invalidName["success-name"] ?: "Ungültiger Name",
                invalidName
            ).build()
        )
            .setEphemeral(true).queue()

        logger.info("Invalid name got set: $invalidName [Guild:${event.guild!!.id}]")
    }

    fun changeRegexNameChecker(event: SlashCommandEvent) {
        logger.info("User ${event.user.id} ran command to disable/enable the regex checker in channel ${event.channel.idLong} [Guild:${event.guild!!.id}]")
        if (event.getOption("enabled")?.asString == null) {
            logger.info("The needed option is gone :/ Aborting. [Guild:${event.guild!!.id}]")
            event.deferReply(true).setContent(language.slashCommandError).queue()
            return
        }

        val enabled = event.getOption("enabled")!!.asString.equals("true", true)
        val guild = event.guild!!

        if (!guilds.containsKey(event.guild!!.idLong)) {
            logger.info("Creating new guild settings for guild ${guild.id}")
            guilds[guild.idLong] = StarboardBot.createDefaultGuildSettings(guild.idLong, event.channel.idLong)
        }

        guilds[guild.idLong]!!.regexChecker = enabled

        StarboardBot.saveGuilds(guilds)

        event.replyEmbeds(
            buildDoneEmbed(
                language.enableRegex["success-description"]
                    ?: "Du hast den Status des Regex Checkers erfolgreich gesetzt!",
                language.enableRegex["success-name"] ?: "Aktiviert",
                enabled.toString()
            ).build()
        )
            .setEphemeral(true).queue()

        logger.info("Name regex got enabled/disabled: $enabled [Guild:${event.guild!!.id}]")
    }

    private fun buildErrorEmbed(description: String): EmbedBuilder {
        return EmbedBuilder()
            .setColor(Color.RED)
            .setTitle(language.errorEmbedTitle)
            .setDescription(description)
    }

    private fun buildDoneEmbed(description: String, key: String, value: String): EmbedBuilder {
        return EmbedBuilder()
            .setColor(Color.GREEN)
            .setTitle(language.successEmbedTitle)
            .setDescription(description)
            .addField(key, value, false)
    }
}