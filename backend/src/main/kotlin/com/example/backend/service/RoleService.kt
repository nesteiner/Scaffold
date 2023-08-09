package com.example.backend.service

import com.example.backend.constant.DEFAULT_ROLE_NAME
import com.example.backend.model.Role
import com.example.backend.request.RegisterRoleRequest
import com.example.backend.request.UpdateRoleRequest
import com.example.backend.table.Roles
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RoleService {
    fun insertOne(request: RegisterRoleRequest): Role {
        val roleid = Roles.insert {
            it[name] = request.name
        } get Roles.id

        return Role(roleid.value, request.name)
    }

    fun updateOne(data: UpdateRoleRequest): Role {
        Roles.update({ Roles.id eq data.id }) {
            it[name] = data.name
        }

        return Role(data.id, data.name)
    }

    fun findOne(id: Long): Role? {
        return Roles.select(Roles.id eq id)
            .firstOrNull()?.let {
                Role(it[Roles.id].value, it[Roles.name])
            }

    }

    fun findOne(name: String): Role? {
        return Roles.select(Roles.name eq name)
            .firstOrNull()?.let {
                Role(it[Roles.id].value, it[Roles.name])
            }
    }

    fun findDefault(): Role {
        var role = findOne(DEFAULT_ROLE_NAME)
        if (role == null) {
            role = insertOne(RegisterRoleRequest(DEFAULT_ROLE_NAME))
        }

        return role
    }
    fun findAll(): List<Role> {
        return Roles.selectAll()
            .map {
                Role(it[Roles.id].value, it[Roles.name])
            }
    }
}