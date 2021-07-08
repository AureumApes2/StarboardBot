package de.skyslycer.starboard.configuration

import com.fasterxml.jackson.annotation.JsonProperty

class Language {
    // The label for the message link button
    @JsonProperty("button-message")
    var buttonMessage = "Nachrichtenlink"

    // The message when the channel of a message doesn't exist anymore
    @JsonProperty("deleted-channel")
    var deletedChannel = "Der **Channel** der **Nachricht** wurde leider nicht gefunden."

    // The message when the message doesn't exist anymore
    @JsonProperty("deleted-message")
    var deletedMessage = "Der die **Nachricht** wurde leider nicht gefunden."

    // The message when everything needed exists
    @JsonProperty("message-link")
    var messageLink = "**Nachrichtenlink**: %link%"
}