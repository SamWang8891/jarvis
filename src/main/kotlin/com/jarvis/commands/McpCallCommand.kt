package com.jarvis.commands

import com.jarvis.mcp.McpClient
import kotlinx.serialization.json.JsonPrimitive
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class McpCallCommand(private val mcpClient: McpClient?) : Command {
    override val name = "mcp-call"
    override val description = "Call an MCP tool"
    override val usage = "!mcp-call <tool-name> [args-as-json]"

    override suspend fun execute(event: MessageReceivedEvent, args: String) {
        if (mcpClient == null) {
            event.channel.sendMessage("MCP is not enabled.").queue()
            return
        }

        if (args.isBlank()) {
            event.channel.sendMessage("Please provide a tool name. Usage: $usage").queue()
            return
        }

        val parts = args.split(" ", limit = 2)
        val toolName = parts[0]
        val toolArgs = if (parts.size > 1) {
            try {
                // Simple parsing: split by spaces and create key-value pairs
                val argParts = parts[1].split(" ")
                argParts.mapNotNull { arg ->
                    val keyValue = arg.split("=", limit = 2)
                    if (keyValue.size == 2) {
                        keyValue[0] to JsonPrimitive(keyValue[1])
                    } else null
                }.toMap()
            } catch (e: Exception) {
                event.channel.sendMessage("Invalid arguments format. Use key=value pairs.").queue()
                return
            }
        } else {
            emptyMap()
        }

        event.channel.sendTyping().queue()

        val result = mcpClient.callTool(toolName, toolArgs)

        if (result != null) {
            val resultText = result.toString()
            if (resultText.length > 2000) {
                val chunks = resultText.chunked(2000)
                chunks.forEach { chunk ->
                    event.channel.sendMessage("```json\n$chunk\n```").queue()
                }
            } else {
                event.channel.sendMessage("```json\n$resultText\n```").queue()
            }
        } else {
            event.channel.sendMessage("Failed to call MCP tool: $toolName").queue()
        }
    }
}
