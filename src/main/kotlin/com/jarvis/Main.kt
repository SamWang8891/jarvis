package com.jarvis

import mu.KotlinLogging
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

suspend fun main(){
    print("Hello World!")

    exitProcess(0)
}