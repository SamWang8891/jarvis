package com.jarvis.config

import com.typesafe.config.ConfigFactory
import mu.KotlinLogging
import io.github.cdimascio.dotenv.dotenv

private val logger = KotlinLogging.logger {}

data class Config(
    val discordToken: String
){
    companion object{
        fun load(): Config{
            logger.info{"Loading env file"}

            val dotenv = dotenv{
                ignoreIfMalformed = true
                ignoreIfMissing = true
            }

            val config = ConfigFactory.load()

            val discordToken = getEnvOrConfig("DISCORD_TOKEN", config, "bot.discord.token", dotenv)
                ?: throw IllegalStateException("DISCORD_TOKEN environment variable or bot.discord.token config must be set")

            return Config(
                discordToken
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

