package com.jarvis.commands

import com.jarvis.mcp.McpClient
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color

class McpToolsCommand(private val mcpClient: McpClient?) : Command {
    override val name = "mcp-tools"
    override val description = "List available MCP tools"
    override val usage = "!mcp-tools"

    override suspend fun execute(event: MessageReceivedEvent, args: String) {
        if (mcpClient == null) {
            event.channel.sendMessage("MCP is not enabled.").queue()
            return
        }

        event.channel.sendTyping().queue()

        val tools = mcpClient.listTools()

        if (tools.isEmpty()) {
            event.channel.sendMessage("No MCP tools available.").queue()
            return
        }

        val embed = EmbedBuilder()
            .setTitle("Available MCP Tools")
            .setDescription("Here are the available MCP tools:")
            .setColor(Color.GREEN)

        tools.forEach { tool ->
            embed.addField(tool.name, tool.description, false)
        }

        event.channel.sendMessageEmbeds(embed.build()).queue()
    }
}
