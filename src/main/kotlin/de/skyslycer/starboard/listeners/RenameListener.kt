package de.skyslycer.starboard.listeners

import de.skyslycer.starboard.StarboardBot
import de.skyslycer.starboard.configuration.Guilds
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent
import net.dv8tion.jda.api.exceptions.HierarchyException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import java.util.stream.Collectors

class RenameListener(private val guilds: Guilds, private val logger: Logger) : ListenerAdapter() {
    override fun onGuildMemberUpdateNickname(event: GuildMemberUpdateNicknameEvent) {
        if (!guilds.containsKey(event.guild.idLong)) {
            logger.info("Creating new guild settings for guild ${event.guild.id}")
            val channel: TextChannel? = event.guild.textChannels.stream().filter { it is TextChannel }.collect(
                Collectors.toList()).toTypedArray()[0]

            guilds[event.guild.idLong] = StarboardBot.createDefaultGuildSettings(event.guild.idLong, channel?.idLong ?: 1)
            StarboardBot.saveGuilds(guilds)
        }

        val guildSettings = guilds[event.guild.idLong]!!

        if (guildSettings.renamedUsers.contains(event.user.idLong)) {
            logger.info("User marked as renamed by the bot. Aborting and deleting entry. [Guild:${guildSettings.guildId}]")
            guildSettings.renamedUsers.remove(event.user.idLong)
            return
        }

        if (!guildSettings.regexChecker) {
            logger.info("Regex checker for guild ${guildSettings.guildId} is disabled. Aborting.")
            return
        }

        val matcher = Pattern.compile(guildSettings.nameRegex, Pattern.MULTILINE).matcher(event.member.effectiveName)

        if (!matcher.find()) {
            try {
                event.member.modifyNickname(guildSettings.invalidName).queue()
                logger.info("Name of the renamed member doesn't match with the given regex. Setting to ${guildSettings.invalidName}. " +
                        "(Name: ${event.member.effectiveName} Regex: ${guildSettings.nameRegex})")
                guildSettings.renamedUsers.add(event.user.idLong)
            } catch (exception: HierarchyException) {
                logger.info(
                    "Name of the renamed member doesn't match with the given regex. But the member has a higher/the highest role on the server. Aborting. [Guild:${event.guild.id}]")
            }
        } else {
            logger.info("Name matches the given regex! Passing. [Guild:${event.guild.id}]")
        }
    }
}