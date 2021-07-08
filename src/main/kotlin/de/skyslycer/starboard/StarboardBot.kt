package de.skyslycer.starboard

import ch.qos.logback.classic.LoggerContext
import com.fasterxml.jackson.databind.ObjectMapper
import de.skyslycer.starboard.configuration.*
import de.skyslycer.starboard.listeners.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class StarboardBot {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private var config = Config()
    private val configFile = File("config/config.json")

    private var language = Language()
    private val languageFile = File("config/language.json")

    private var commandLanguage = CommandLanguage()
    private val commandLanguageFile = File("config/command_language.json")

    private var guilds = Guilds()
    private var guildsFile = File("config/guilds.json")

    private val mapper = ObjectMapper()

    fun start() {
        logger.info("Starting bot StarboardBot!")

        initGuilds()
        initConfig()
        initLanguage()
        initCommandLanguage()
        initJda()
    }

    private fun initGuilds() {
        if (!guildsFile.exists()) {
            guildsFile.parentFile.mkdirs()
            guildsFile.createNewFile()
            mapper.writerWithDefaultPrettyPrinter().writeValue(guildsFile, guilds)
        }

        guilds = mapper.readValue(guildsFile, Guilds::class.java)
        logger.info("Successfully loaded guilds! (config/guilds.json) [${guilds.size} loaded!]")
    }

    private fun initConfig() {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config)
        }

        config = mapper.readValue(configFile, Config::class.java)
        logger.info("Successfully loaded config! (config/config.json)")
    }

    private fun initLanguage() {
        if (!languageFile.exists()) {
            languageFile.parentFile.mkdirs()
            languageFile.createNewFile()
            mapper.writerWithDefaultPrettyPrinter().writeValue(languageFile, language)
        }

        language = mapper.readValue(languageFile, Language::class.java)
        logger.info("Successfully loaded language config! (config/language.json)")
    }

    private fun initCommandLanguage() {
        if (!commandLanguageFile.exists()) {
            commandLanguageFile.parentFile.mkdirs()
            commandLanguageFile.createNewFile()
            mapper.writerWithDefaultPrettyPrinter().writeValue(commandLanguageFile, commandLanguage)
        }

        commandLanguage = mapper.readValue(commandLanguageFile, CommandLanguage::class.java)
        logger.info("Successfully loaded command language config! (config/command_language.json)")
    }

    private fun initJda() {
        val jda = JDABuilder.create(config.token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
            .addEventListeners(
                ReactionListener(guilds, language, logger),
                ButtonListener(language, logger),
                MemberJoinListener(guilds, logger),
                RenameListener(guilds, logger),
                CommandListener(guilds, commandLanguage, logger)
            )
            .disableCache(
                CacheFlag.ACTIVITY,
                CacheFlag.VOICE_STATE,
                CacheFlag.EMOTE,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.ONLINE_STATUS
            )
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .build()

        initCommands(jda)
    }

    private fun initCommands(jda: JDA) {
        jda.updateCommands().addCommands(
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

    companion object {
        fun createDefaultGuildSettings(guildId: Long, channelId: Long): GuildSettings {
            val settings = GuildSettings()
            settings.guildId = guildId
            settings.starboardChannel = channelId
            return settings
        }

        fun saveGuilds(guilds: Guilds) {
            val mapper = ObjectMapper()
            mapper.writerWithDefaultPrettyPrinter().writeValue(File("config/guilds.json"), guilds)
        }
    }
}

fun main() {
    StarboardBot().start()
}