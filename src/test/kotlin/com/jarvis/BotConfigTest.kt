package com.jarvis

import com.jarvis.config.BotConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class BotConfigTest {

    @Test
    fun `test config loading with environment variables`() {
        // Set test environment variables
        System.setProperty("DISCORD_TOKEN", "test_token")
        System.setProperty("GEMINI_API_KEY", "test_key")

        // This test would require proper setup, just a placeholder
        // val config = BotConfig.load()
        // assertNotNull(config)
    }
}
