package com.jarvis

import com.jarvis.bot.JarvisBot
import com.jarvis.config.BotConfig
import mu.KotlinLogging
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

suspend fun main() {
    logger.info { "Starting Jarvis Discord Bot..." }

    try {
        val config = BotConfig.load()
        val bot = JarvisBot(config)
        bot.start()

        // Keep the application running
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info { "Shutting down Jarvis..." }
            bot.shutdown()
        })
    } catch (e: Exception) {
        logger.error(e) { "Failed to start bot" }
        exitProcess(1)
    }
}