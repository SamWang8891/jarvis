package com.jarvis.gemini

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class GeminiClient(private val apiKey: String) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val modelName = "gemini-pro"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun generateResponse(
        prompt: String,
        conversationHistory: List<Message> = emptyList()
    ): String? {
        return try {
            val contents = buildList {
                addAll(conversationHistory.map { msg ->
                    Content(
                        role = msg.role,
                        parts = listOf(Part(text = msg.content))
                    )
                })
                add(Content(
                    role = "user",
                    parts = listOf(Part(text = prompt))
                ))
            }

            val request = GenerateContentRequest(contents = contents)

            val response: GenerateContentResponse = client.post("$baseUrl/models/$modelName:generateContent") {
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        } catch (e: Exception) {
            logger.error(e) { "Error generating response from Gemini" }
            null
        }
    }

    suspend fun generateResponseWithContext(
        prompt: String,
        context: String
    ): String? {
        val enhancedPrompt = """
            Context: $context

            User Question: $prompt

            Please answer the question based on the provided context.
        """.trimIndent()

        return generateResponse(enhancedPrompt)
    }

    suspend fun streamResponse(
        prompt: String,
        onChunk: (String) -> Unit
    ) {
        try {
            val contents = listOf(
                Content(
                    role = "user",
                    parts = listOf(Part(text = prompt))
                )
            )

            val request = GenerateContentRequest(contents = contents)

            client.post("$baseUrl/models/$modelName:streamGenerateContent") {
                parameter("key", apiKey)
                parameter("alt", "sse")
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<String>().lines().forEach { line ->
                if (line.startsWith("data: ")) {
                    val jsonData = line.substring(6)
                    try {
                        val chunk = Json.parseToJsonElement(jsonData).jsonObject
                        val text = chunk["candidates"]
                            ?.jsonArray?.firstOrNull()
                            ?.jsonObject?.get("content")
                            ?.jsonObject?.get("parts")
                            ?.jsonArray?.firstOrNull()
                            ?.jsonObject?.get("text")
                            ?.jsonPrimitive?.content

                        text?.let { onChunk(it) }
                    } catch (e: Exception) {
                        logger.debug { "Error parsing chunk: $line" }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error streaming response from Gemini" }
        }
    }

    fun close() {
        client.close()
    }
}

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val role: String = "user",
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null
)

data class Message(
    val role: String,
    val content: String
)
