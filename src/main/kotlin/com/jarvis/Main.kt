package com.jarvis

import mu.KotlinLogging
import kotlin.system.exitProcess
import com.jarvis.config.Config

private val logger = KotlinLogging.logger {}

suspend fun main(){
    print("Program started!")
    try{
        val config = Config.load()

    }catch (e: Exception){
        logger.error(e){"Failed to start the program!"}
        exitProcess(1)
    }


    exitProcess(0)
}