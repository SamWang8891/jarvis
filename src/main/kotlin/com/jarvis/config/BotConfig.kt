package com.jarvis.config

import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.dotenv
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class BotConfig(
    val discordToken: String,
    val geminiApiKey: String,
    val mcpServerUrl: String,
    val commandPrefix: String = "!",
    val enableMcp: Boolean = true,
    val enableGemini: Boolean = true
) {
    companion object {
        fun load(): BotConfig {
            logger.info { "Loading configuration..." }

            // Load .env file
            val dotenv = dotenv {
                ignoreIfMissing = true
            }

            val config = ConfigFactory.load()

            return BotConfig(
                discordToken = getEnvOrConfig("DISCORD_TOKEN", config, "bot.discord.token", dotenv)
                    ?: throw IllegalStateException("DISCORD_TOKEN environment variable or bot.discord.token config must be set"),
                geminiApiKey = getEnvOrConfig("GEMINI_API_KEY", config, "bot.gemini.apiKey", dotenv)
                    ?: throw IllegalStateException("GEMINI_API_KEY environment variable or bot.gemini.apiKey config must be set"),
                mcpServerUrl = getEnvOrConfig("MCP_SERVER_URL", config, "bot.mcp.serverUrl", dotenv)
                    ?: "ws://localhost:3000",
                commandPrefix = config.getString("bot.commandPrefix"),
                enableMcp = config.getBoolean("bot.mcp.enabled"),
                enableGemini = config.getBoolean("bot.gemini.enabled")
            )
        }

        private fun getEnvOrConfig(
            envVar: String,
            config: com.typesafe.config.Config,
            configPath: String,
            dotenv: io.github.cdimascio.dotenv.Dotenv
        ): String? {
            // Check .env file first, then system environment, then config file
            return dotenv[envVar] ?: System.getenv(envVar) ?: if (config.hasPath(configPath)) {
                config.getString(configPath)
            } else {
                null
            }
        }
    }
}