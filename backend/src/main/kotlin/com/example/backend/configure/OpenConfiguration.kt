package com.example.backend.configure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "open")
data class OpenConfiguration(
    var urls: Array<String> = arrayOf(),
    var roles: Array<String> = arrayOf()
)
