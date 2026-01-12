package com.jarvis.commands

import com.jarvis.config.BotConfig
import com.jarvis.gemini.GeminiClient
import com.jarvis.mcp.McpClient
import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

private val logger = KotlinLogging.logger {}

class CommandHandler(
    private val config: BotConfig,
    private val geminiClient: GeminiClient?,
    private val mcpClient: McpClient?
) {
    private val commands = mutableMapOf<String, Command>()

    init {
        registerCommands()
    }

    private fun registerCommands() {
        register(HelpCommand(config))
        register(PingCommand())
        register(AskCommand(geminiClient))
        register(McpToolsCommand(mcpClient))
        register(McpCallCommand(mcpClient))
    }

    private fun register(command: Command) {
        commands[command.name] = command
        logger.debug { "Registered command: ${command.name}" }
    }

    suspend fun handleCommand(event: MessageReceivedEvent) {
        val content = event.message.contentRaw
        val prefix = config.commandPrefix

        if (!content.startsWith(prefix)) return

        val parts = content.substring(prefix.length).trim().split(" ", limit = 2)
        val commandName = parts[0].lowercase()
        val args = if (parts.size > 1) parts[1] else ""

        val command = commands[commandName]
        if (command == null) {
            event.channel.sendMessage("Unknown command. Use `${prefix}help` for available commands.").queue()
            return
        }

        logger.info { "Executing command: $commandName by ${event.author.name}" }

        try {
            command.execute(event, args)
        } catch (e: Exception) {
            logger.error(e) { "Error executing command: $commandName" }
            event.channel.sendMessage("Error executing command: ${e.message}").queue()
        }
    }

    fun getCommands(): Collection<Command> = commands.values
}

interface Command {
    val name: String
    val description: String
    val usage: String

    suspend fun execute(event: MessageReceivedEvent, args: String)
}
