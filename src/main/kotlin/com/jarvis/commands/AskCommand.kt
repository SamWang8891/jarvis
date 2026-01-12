package com.jarvis.commands

import com.jarvis.gemini.GeminiClient
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class AskCommand(private val geminiClient: GeminiClient?) : Command {
    override val name = "ask"
    override val description = "Ask Gemini AI a question"
    override val usage = "!ask <your question>"

    override suspend fun execute(event: MessageReceivedEvent, args: String) {
        if (geminiClient == null) {
            event.channel.sendMessage("Gemini AI is not enabled.").queue()
            return
        }

        if (args.isBlank()) {
            event.channel.sendMessage("Please provide a question. Usage: $usage").queue()
            return
        }

        event.channel.sendTyping().queue()

        val response = geminiClient.generateResponse(args)

        if (response != null) {
            // Discord has a 2000 character limit for messages
            if (response.length > 2000) {
                val chunks = response.chunked(2000)
                chunks.forEach { chunk ->
                    event.channel.sendMessage(chunk).queue()
                }
            } else {
                event.channel.sendMessage(response).queue()
            }
        } else {
            event.channel.sendMessage("Failed to get a response from Gemini AI.").queue()
        }
    }
}
