package de.skyslycer.starboard.configuration

import com.fasterxml.jackson.annotation.JsonProperty

class CommandLanguage {
    @JsonProperty("error")
    var slashCommandError = "Es ist ein **Fehler aufgetreten**. Bitte versuche es erneut."

    @JsonProperty("no-permission")
    var noPermission = "Du hast nicht genügend Rechte für dieses Kommando!"

    @JsonProperty("error-embed-title")
    var errorEmbedTitle = "Fehler"

    @JsonProperty("success-embed-title")
    var successEmbedTitle = "Erfolg!"

    var channel = mapOf(
        "no-text-channel" to "Dies ist kein Text-Channel!",
        "success-name" to "Starboard-Channel",
        "success-description" to "Du hast den Starboard-Channel erfolgreich gesetzt!"
    )

    var needed = mapOf(
        "success-name" to "Benötigte Reaktionen",
        "success-description" to "Du hast die benötigten Reaktionen erfolgreich gesetzt!"
    )

    var nameRegex = mapOf(
        "success-name" to "Regex",
        "success-description" to "Du hast den Regex für Benutzernamen erfolgreich gesetzt!"
    )

    var invalidName = mapOf(
        "success-name" to "Ungültiger Name",
        "success-description" to "Du hast den Namen für Benutzer mit ungültigem Namen erfolgreich gesetzt!"
    )

    var enableRegex = mapOf(
        "success-name" to "Aktiviert",
        "success-description" to "Du hast den Status des Regex Checkers erfolgreich gesetzt!"
    )

    var emoji = mapOf(
        "no-emoji" to "Dein angegebenes Emoji ist kein Emoji, das global auf Discord verfügbar ist!",
        "success-name" to "Emoji",
        "success-description" to "Du hast das Reaction Emoji erfolgreich gesetzt!"
    )
}