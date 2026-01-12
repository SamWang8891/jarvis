package com.jarvis.bot

import com.jarvis.commands.CommandHandler
import com.jarvis.config.BotConfig
import com.jarvis.gemini.GeminiClient
import com.jarvis.mcp.McpClient
import kotlinx.coroutines.*
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent

private val logger = KotlinLogging.logger {}

class JarvisBot(private val config: BotConfig) : ListenerAdapter() {
    private var jda: JDA? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val geminiClient: GeminiClient? = if (config.enableGemini) {
        GeminiClient(config.geminiApiKey)
    } else null

    private val mcpClient: McpClient? = if (config.enableMcp) {
        McpClient(config.mcpServerUrl)
    } else null

    private val commandHandler = CommandHandler(
        config = config,
        geminiClient = geminiClient,
        mcpClient = mcpClient
    )

    suspend fun start() {
        logger.info { "Initializing Discord bot..." }

        jda = JDABuilder.createDefault(config.discordToken)
            .enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.DIRECT_MESSAGES
            )
            .setActivity(Activity.listening("${config.commandPrefix}help"))
            .addEventListeners(this)
            .build()
            .awaitReady()

        logger.info { "Bot is ready and connected to Discord!" }

        // Connect to MCP server if enabled
        mcpClient?.let {
            scope.launch {
                try {
                    it.connect()
                    logger.info { "Connected to MCP server" }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to connect to MCP server" }
                }
            }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val message = event.message.contentRaw
        if (!message.startsWith(config.commandPrefix)) return

        scope.launch {
            try {
                commandHandler.handleCommand(event)
            } catch (e: Exception) {
                logger.error(e) { "Error handling command: $message" }
                event.channel.sendMessage("An error occurred while processing your command.").queue()
            }
        }
    }

    fun shutdown() {
        logger.info { "Shutting down bot..." }
        scope.cancel()
        mcpClient?.disconnect()
        jda?.shutdown()
    }
}