package com.example.backend.service

import com.example.backend.utils.Page

interface UserService<EntityType, RegisterType, UpdateType> {
    fun findOne(id: Long): EntityType?
    fun findOne(name: String): EntityType?
    fun findAll(): List<EntityType>
    fun findAll(page: Int, size: Int): Page<EntityType>
    fun insertOne(data: RegisterType): EntityType
    fun updateOne(data: UpdateType): EntityType
    fun deleteOne(id: Long)
}