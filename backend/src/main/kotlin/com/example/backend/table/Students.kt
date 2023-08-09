package com.example.backend.table

import org.jetbrains.exposed.dao.id.LongIdTable

object Students: LongIdTable() {
    val name = char("name", 24)
    val grade = char("grade", 8)
    val major = char("major", 16)
    val clazz = char("clazz", 24)
    val institute = char("institute", 32)
    val telephone = char("telephone", 11)
    val email = varchar("email", 24)
    val passwordHash = varchar("passwordHash", 256)
    val cardId = char("cardId", 18)
    val sex = char("sex", 1)
    val enabled = bool("enabled")
}