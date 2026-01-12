package com.jarvis.mcp

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.*
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicLong

private val logger = KotlinLogging.logger {}

class McpClient(private val serverUrl: String) {
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    private val requestId = AtomicLong(0)
    private val responseChannels = mutableMapOf<Long, Channel<JsonElement>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun connect() {
        try {
            logger.info { "Connecting to MCP server at $serverUrl" }

            client.webSocket(serverUrl) {
                session = this

                // Initialize MCP session
                initialize()

                // Start message receiver
                scope.launch {
                    receiveMessages()
                }

                logger.info { "MCP connection established" }

                // Keep connection alive
                for (frame in incoming) {
                    // Connection will be maintained
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to connect to MCP server" }
            throw e
        }
    }

    private suspend fun initialize() {
        val request = buildJsonObject {
            put("jsonrpc", "2.0")
            put("id", requestId.incrementAndGet())
            put("method", "initialize")
            put("params", buildJsonObject {
                put("protocolVersion", "2024-11-05")
                put("clientInfo", buildJsonObject {
                    put("name", "jarvis-discord-bot")
                    put("version", "1.0.0")
                })
                put("capabilities", buildJsonObject {
                    putJsonObject("roots") {
                        put("listChanged", true)
                    }
                })
            })
        }

        sendRequest(request)
    }

    private suspend fun receiveMessages() {
        try {
            session?.let { ws ->
                for (frame in ws.incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        handleMessage(text)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error receiving messages" }
        }
    }

    private suspend fun handleMessage(message: String) {
        try {
            val json = Json.parseToJsonElement(message).jsonObject

            // Check if it's a response or a notification
            val id = json["id"]?.jsonPrimitive?.longOrNull

            if (id != null) {
                // It's a response
                responseChannels[id]?.send(json)
            } else {
                // It's a notification or request from server
                logger.debug { "Received notification: $message" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error handling message: $message" }
        }
    }

    private suspend fun sendRequest(request: JsonObject) {
        session?.send(Frame.Text(request.toString()))
    }

    suspend fun callTool(toolName: String, arguments: Map<String, JsonElement> = emptyMap()): JsonElement? {
        val id = requestId.incrementAndGet()
        val channel = Channel<JsonElement>(1)
        responseChannels[id] = channel

        val request = buildJsonObject {
            put("jsonrpc", "2.0")
            put("id", id)
            put("method", "tools/call")
            put("params", buildJsonObject {
                put("name", toolName)
                put("arguments", JsonObject(arguments))
            })
        }

        try {
            sendRequest(request)
            return withTimeout(30_000) {
                val response = channel.receive()
                response.jsonObject["result"]
            }
        } catch (e: Exception) {
            logger.error(e) { "Error calling tool: $toolName" }
            return null
        } finally {
            responseChannels.remove(id)
            channel.close()
        }
    }

    suspend fun listTools(): List<McpTool> {
        val id = requestId.incrementAndGet()
        val channel = Channel<JsonElement>(1)
        responseChannels[id] = channel

        val request = buildJsonObject {
            put("jsonrpc", "2.0")
            put("id", id)
            put("method", "tools/list")
        }

        return try {
            sendRequest(request)
            withTimeout(10_000) {
                val response = channel.receive()
                val tools = response.jsonObject["result"]?.jsonObject?.get("tools")?.jsonArray
                tools?.map { tool ->
                    val obj = tool.jsonObject
                    McpTool(
                        name = obj["name"]?.jsonPrimitive?.content ?: "",
                        description = obj["description"]?.jsonPrimitive?.content ?: ""
                    )
                } ?: emptyList()
            }
        } catch (e: Exception) {
            logger.error(e) { "Error listing tools" }
            emptyList()
        } finally {
            responseChannels.remove(id)
            channel.close()
        }
    }

    fun disconnect() {
        logger.info { "Disconnecting from MCP server" }
        scope.cancel()
        responseChannels.values.forEach { it.close() }
        responseChannels.clear()
        runBlocking {
            session?.close()
            client.close()
        }
    }
}

data class McpTool(
    val name: String,
    val description: String
)