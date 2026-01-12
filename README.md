# Jarvis Discord Bot

A powerful Discord bot built with Kotlin that integrates Model Context Protocol (MCP) and Google's Gemini AI.

## Features

- **Discord Integration**: Built with JDA (Java Discord API) for robust Discord functionality
- **Gemini AI**: Integration with Google's Gemini AI for intelligent responses
- **MCP Support**: Model Context Protocol client for tool calling and context management
- **Asynchronous**: Built with Kotlin Coroutines for efficient async operations
- **Modular Commands**: Easy-to-extend command system

## Project Structure

```
jarvis/
├── src/
│   ├── main/
│   │   ├── kotlin/com/jarvis/
│   │   │   ├── bot/              # Discord bot core
│   │   │   │   └── JarvisBot.kt
│   │   │   ├── commands/         # Command implementations
│   │   │   │   ├── CommandHandler.kt
│   │   │   │   ├── HelpCommand.kt
│   │   │   │   ├── PingCommand.kt
│   │   │   │   ├── AskCommand.kt
│   │   │   │   ├── McpToolsCommand.kt
│   │   │   │   └── McpCallCommand.kt
│   │   │   ├── config/           # Configuration management
│   │   │   │   └── BotConfig.kt
│   │   │   ├── gemini/           # Gemini AI client
│   │   │   │   └── GeminiClient.kt
│   │   │   ├── mcp/              # MCP client
│   │   │   │   └── McpClient.kt
│   │   │   └── Main.kt           # Application entry point
│   │   └── resources/
│   │       ├── application.conf  # Configuration file
│   │       └── logback.xml       # Logging configuration
│   └── test/
│       └── kotlin/com/jarvis/    # Tests
├── build.gradle.kts              # Gradle build configuration
├── settings.gradle.kts           # Gradle settings
├── gradle.properties             # Gradle properties
├── Dockerfile                    # Docker image configuration
├── docker-compose.yml            # Docker Compose configuration
├── .env.example                  # Environment variables example
└── README.md                     # This file
```

## Prerequisites

- JDK 17 or higher
- Gradle 8.5 or higher (or use the Gradle wrapper)
- Discord Bot Token
- Google Gemini API Key
- MCP Server (optional)

## Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd jarvis
```

### 2. Configure Environment Variables

Copy the example environment file and fill in your credentials:

```bash
cp .env.example .env
```

Edit `.env` with your actual values:

```env
DISCORD_TOKEN=your_discord_bot_token_here
GEMINI_API_KEY=your_gemini_api_key_here
MCP_SERVER_URL=ws://localhost:3000
```

### 3. Get a Discord Bot Token

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application
3. Go to the "Bot" section and create a bot
4. Copy the bot token and add it to your `.env` file
5. Enable "Message Content Intent" under Privileged Gateway Intents
6. Invite the bot to your server using the OAuth2 URL generator

### 4. Get a Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key
3. Add it to your `.env` file

### 5. Build the Project

Using Gradle:

```bash
./gradlew build
```

Or if you have Gradle installed:

```bash
gradle build
```

### 6. Run the Bot

Using Gradle:

```bash
./gradlew run
```

Or run the JAR directly:

```bash
java -jar build/libs/jarvis-1.0.0.jar
```

## Docker Deployment

### Build and Run with Docker Compose

```bash
docker-compose up -d
```

This will start both the bot and an example MCP server.

### Build Docker Image Only

```bash
docker build -t jarvis-bot .
```

### Run Docker Container

```bash
docker run -d \
  --name jarvis-bot \
  -e DISCORD_TOKEN=your_token \
  -e GEMINI_API_KEY=your_key \
  -e MCP_SERVER_URL=ws://your-mcp-server:3000 \
  -v $(pwd)/logs:/app/logs \
  jarvis-bot
```

## Available Commands

| Command | Description |
|---------|-------------|
| `!help` | Shows available commands |
| `!ping` | Check if the bot is responsive |
| `!ask <question>` | Ask Gemini AI a question |
| `!mcp-tools` | List available MCP tools |
| `!mcp-call <tool> <args>` | Call an MCP tool |

## Configuration

The bot can be configured via environment variables or the `application.conf` file:

```hocon
bot {
    discord {
        token = ${?DISCORD_TOKEN}
    }

    gemini {
        apiKey = ${?GEMINI_API_KEY}
        enabled = true
    }

    mcp {
        serverUrl = ${?MCP_SERVER_URL}
        enabled = true
    }

    commandPrefix = "!"
}
```

## MCP Integration

The bot includes a Model Context Protocol (MCP) client for connecting to MCP servers. MCP enables:

- Tool calling and function execution
- Context management
- Structured data exchange

### Setting up an MCP Server

You'll need to set up an MCP server separately. The bot expects the server to be available at the URL specified in `MCP_SERVER_URL`.

Example MCP server implementations:
- [MCP TypeScript SDK](https://github.com/modelcontextprotocol/typescript-sdk)
- [MCP Python SDK](https://github.com/modelcontextprotocol/python-sdk)

## Development

### Adding New Commands

1. Create a new class implementing the `Command` interface:

```kotlin
package com.jarvis.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class MyCommand : Command {
    override val name = "mycommand"
    override val description = "My custom command"
    override val usage = "!mycommand [args]"

    override suspend fun execute(event: MessageReceivedEvent, args: String) {
        event.channel.sendMessage("Hello from my command!").queue()
    }
}
```

2. Register the command in `CommandHandler.kt`:

```kotlin
private fun registerCommands() {
    register(HelpCommand(config))
    register(PingCommand())
    register(MyCommand()) // Add your command here
    // ... other commands
}
```

### Running Tests

```bash
./gradlew test
```

## Logging

Logs are written to:
- Console output (INFO level)
- `logs/jarvis.log` (rolling file appender)

Configure logging in `src/main/resources/logback.xml`.

## Troubleshooting

### Bot doesn't respond to commands

- Ensure "Message Content Intent" is enabled in Discord Developer Portal
- Check that the bot has permission to read and send messages in the channel
- Verify the command prefix in your configuration

### Gemini API errors

- Verify your API key is correct
- Check your API quota and limits
- Ensure you have enabled the Generative Language API

### MCP connection issues

- Verify your MCP server is running and accessible
- Check the WebSocket URL format (should start with `ws://` or `wss://`)
- Review MCP server logs for connection errors

## License

See [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and questions, please create an issue in the GitHub repository.
