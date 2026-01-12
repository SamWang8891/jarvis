package com.jarvis.bot

import com.jarvis.config.Config
import mu.KotlinLogging
import java.net.http.WebSocket


private val logger = KotlinLogging.logger {}

class JarvisBot(private val config: Config){
    suspend fun start(){
        logger.info{"Starting bot!"}
    }
}