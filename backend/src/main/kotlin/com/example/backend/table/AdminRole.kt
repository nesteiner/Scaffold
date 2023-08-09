package com.example.backend.table

import org.jetbrains.exposed.sql.Table

object AdminRole: Table() {
    val userid = reference("userid", Admins)
    val roleid = reference("roleid", Roles)
}