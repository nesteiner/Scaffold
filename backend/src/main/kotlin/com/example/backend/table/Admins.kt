package com.example.backend.table

import org.jetbrains.exposed.dao.id.LongIdTable

object Admins: LongIdTable() {
    val name = varchar("name", 16)
    val passwordHash = varchar("passwordHash", 256)
    val enabled = bool("enabled")
}