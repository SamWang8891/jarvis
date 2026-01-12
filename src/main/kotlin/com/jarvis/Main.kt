package com.jarvis

import com.jarvis.bot.JarvisBot
import mu.KotlinLogging
import kotlin.system.exitProcess
import com.jarvis.config.Config

private val logger = KotlinLogging.logger {}

suspend fun main(){
    print("Program started!")
    try{
        val config = Config.load()
        val bot = JarvisBot(config)
        bot.start()
    }catch (e: Exception){
        logger.error(e){"Failed to start the program!"}
        exitProcess(1)
    }


    exitProcess(0)
}