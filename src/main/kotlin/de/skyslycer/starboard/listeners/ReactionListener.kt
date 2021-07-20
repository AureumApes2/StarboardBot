package de.skyslycer.starboard.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import de.skyslycer.starboard.StarboardBot
import de.skyslycer.starboard.configuration.Guilds
import de.skyslycer.starboard.configuration.Language
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import org.slf4j.Logger

class ReactionListener(private val guilds: Guilds, private val language: Language, private val logger: Logger) : ListenerAdapter() {
    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.reactionEmote.isEmote) {
            return
        }

        if (!guilds.containsKey(event.guild.idLong)) {
            logger.info("Creating new guild settings for guild ${event.guild.id} and saving")
            guilds[event.guild.idLong] = StarboardBot.createDefaultGuildSettings(event.guild.idLong, event.channel.idLong)
            StarboardBot.saveGuilds(guilds)
        }

        val guildSettings = guilds[event.guild.idLong]!!

        if (event.reactionEmote.emoji != guildSettings.starUnicode) {
            return
        }

        event.retrieveMessage().queue { message ->
            if (message.author.idLong == event.userIdLong) {
                logger.info("Message author tried to star his own message. Removing reaction. (Message ${message.id} in channel ${message.channel.id}) [Guild:${event.guild.id}]")
                event.reaction.removeReaction(event.user)
            }

            event.reaction.retrieveUsers().queue {
                if (it.size == guildSettings.needed) {
                    logger.info(
                        "Enough stars for this guild to add the message to the starboard (Message ${message.id} in channel ${message.channel.id} with ${guildSettings.needed} reactions) [Guild:${event.guild.id}]"
                    )

                    val starboardChannel = if (event.guild.getTextChannelById(guildSettings.starboardChannel) != null) {
                        event.guild.getTextChannelById(guildSettings.starboardChannel)!!
                    } else {
                        event.channel
                    }

                    if (!guildSettings.doneMessages.contains(message.idLong)) {
                        sendStarboardMessage(starboardChannel, message.author.name, message.contentRaw, "${message.channel.id};${message.id}")
                        guildSettings.doneMessages.add(message.idLong)
                        StarboardBot.saveGuilds(guilds)
                        logger.info("Added message ${message.id} in channel ${message.channel.id} to the starboard! [Guild:${event.guild.id}]")
                    } else {
                        logger.info("Message already got added to the starboard! Aborting. (Message: ${message.id} Channel: ${message.channel.id}) [Guild:${event.guild.id}]")
                    }
                }
            }
        }
    }

    private fun sendStarboardMessage(channel: TextChannel, user: String, message: String, messageLink: String) {
        channel.sendMessage(
            MessageBuilder(message).setActionRows(
                ActionRow.of(
                    Button.success("sb:dummy:user", "@$user"),
                    Button.primary("sb:message:$messageLink", language.buttonMessage)
                )
            ).build()
        ).queue()
    }
}