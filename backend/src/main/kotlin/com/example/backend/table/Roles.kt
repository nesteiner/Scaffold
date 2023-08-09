package com.example.backend.table

import org.jetbrains.exposed.dao.id.LongIdTable

object Roles: LongIdTable() {
    val name = varchar("name", 32)
}