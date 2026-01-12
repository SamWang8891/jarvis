package com.jarvis.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class PingCommand : Command {
    override val name = "ping"
    override val description = "Check if the bot is responsive"
    override val usage = "!ping"

    override suspend fun execute(event: MessageReceivedEvent, args: String) {
        val startTime = System.currentTimeMillis()

        event.channel.sendMessage("Pinging...").queue { message ->
            val latency = System.currentTimeMillis() - startTime
            val gatewayPing = event.jda.gatewayPing

            message.editMessage(
                "Pong! \uD83C\uDFD3\n" +
                        "Bot Latency: ${latency}ms\n" +
                        "Gateway Ping: ${gatewayPing}ms"
            ).queue()
        }
    }
}
