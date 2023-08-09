package com.example.backend.model

class Admin(
    override val id: Long,
    override var name: String,
    override var passwordHash: String,
    override var roles: List<Role>,
    override var enabled: Boolean
) : User