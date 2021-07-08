package de.skyslycer.starboard.listeners

import de.skyslycer.starboard.configuration.Config
import de.skyslycer.starboard.configuration.Language
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger

class ButtonListener(private val language: Language, private val logger: Logger) : ListenerAdapter() {
    override fun onButtonClick(event: ButtonClickEvent) {
        when {
            event.componentId.startsWith("sb:dummy:user") -> {
                logger.info("A user clicked the user link button. (Useless) [Guild:${event.guild!!.id}]")
                event.deferEdit().queue()
            }
            event.componentId.startsWith("sb:message") -> {
                if (event.guild == null) {
                    event.deferEdit().queue()
                    return
                }

                logger.info("A user clicked the message link button. [Guild:${event.guild!!.id}]")

                val componentIdSplit = event.componentId.split(":")
                if (componentIdSplit.size == 3) {
                    val channel = componentIdSplit[2].split(";")[0]
                    val message = componentIdSplit[2].split(";")[1]

                    if (event.guild!!.getTextChannelById(channel) == null) {
                        logger.info("Text channel couldn't be found by id $channel in guild ${event.guild}")
                        event.deferReply(true).setContent(language.deletedChannel).queue()
                        return
                    }

                    event.guild!!.getTextChannelById(channel)!!.retrieveMessageById(message).queue({
                        event.deferReply(true).setContent(
                            language.messageLink.replace(
                                "%link%", "https://discord.com/channels/${event.guild!!.id}/$channel/$message"
                            )
                        ).queue()
                        logger.info("Fulfilled all requirements to send the message link. Message link sent: https://discord.com/channels/${event.guild!!.id}/$channel/$message [Guild:${event.guild!!.id}]")
                    }) {
                        logger.info("Message couldn't be found by id $message in text channel $channel that is in guild ${event.guild}")
                        event.deferReply(true).setContent(language.deletedMessage).queue()
                    }
                } else {
                    logger.info("Something went wrong with this button id: ${event.componentId} [Guild:${event.guild!!.id}]")
                }
            }
        }
    }
}