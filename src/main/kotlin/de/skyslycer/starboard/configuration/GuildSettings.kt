package de.skyslycer.starboard.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

class GuildSettings {
    // The guild id for this guild setting
    @JsonProperty("guild-id")
    var guildId: Long = 0

    // The starboard channel id
    @JsonProperty("starboard-channel")
    var starboardChannel: Long = 0

    // The star unicode for the reactions
    @JsonProperty("star-unicode")
    var starUnicode: String = "⭐"

    // The needed amount of reactions to add it to the starboard
    @JsonProperty("needed-reactions")
    var needed: Int = 5

    // All the messages that already got added to the starboard
    @JsonProperty("messages-list")
    var doneMessages: ArrayList<Long> = arrayListOf()

    // The name regex needed to match for the name
    @JsonProperty("name-regex")
    var nameRegex: String = "^[a-zA-Z0-9]{3}.*\$"

    // The name set for users that don't match the regex
    @JsonProperty("invalid-name")
    var invalidName: String = "Ungültiger Name"

    // If the checker should be enabled
    @JsonProperty("regex-checker")
    var regexChecker: Boolean = true

    @JsonIgnore
    var renamedUsers = arrayListOf<Long>()
}