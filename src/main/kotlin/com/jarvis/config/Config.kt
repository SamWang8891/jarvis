package com.jarvis.config

import com.typesafe.config.ConfigFactory
import mu.KotlinLogging
import io.github.cdimascio.dotenv.dotenv
import kotlin.system.exitProcess

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

            val discordToken = dotenv["DISCORD_TOKEN"]

            if (discordToken == null){
                logger.info{"Discord token not found in env file"}
                exitProcess(1)
            }

            return Config(
                discordToken = dotenv["DISCORD_TOKEN"]
            )
        }
    }
}

