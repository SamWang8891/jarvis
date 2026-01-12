package com.jarvis.commands

import com.jarvis.config.BotConfig
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color

class HelpCommand(private val config: BotConfig) : Command {
    override val name = "help"
    override val description = "Shows available commands"
    override val usage = "${config.commandPrefix}help"

    override suspend fun execute(event: MessageReceivedEvent, args: String) {
        val embed = EmbedBuilder()
            .setTitle("Jarvis Bot Commands")
            .setDescription("Here are the available commands:")
            .setColor(Color.BLUE)
            .addField("${config.commandPrefix}help", "Shows this help message", false)
            .addField("${config.commandPrefix}ping", "Check if the bot is responsive", false)
            .addField("${config.commandPrefix}ask <question>", "Ask Gemini AI a question", false)
            .addField("${config.commandPrefix}mcp-tools", "List available MCP tools", false)
            .addField("${config.commandPrefix}mcp-call <tool> <args>", "Call an MCP tool", false)
            .setFooter("Jarvis v1.0.0")
            .build()

        event.channel.sendMessageEmbeds(embed).queue()
    }
}
