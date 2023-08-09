package com.example.backend.table

import org.jetbrains.exposed.sql.Table

object StudentRole: Table() {
    val userid = reference("userid", Students)
    val roleid = reference("roleid", Roles)
}