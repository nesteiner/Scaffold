package com.example.backend.service

import com.example.backend.constant.ADMIN_ROLE_NAME
import com.example.backend.exception.BadRequestException
import com.example.backend.model.Admin
import com.example.backend.model.Role
import com.example.backend.request.RegisterAdminRequest
import com.example.backend.request.UpdateAdminRequest
import com.example.backend.table.AdminRole
import com.example.backend.table.Admins
import com.example.backend.table.Roles
import com.example.backend.utils.Page
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.ceil
import org.springframework.security.core.userdetails.User as OtherUser

@Service
@Transactional
class AdminService: UserDetailsService, UserService<Admin, RegisterAdminRequest, UpdateAdminRequest> {
    override fun loadUserByUsername(username: String): UserDetails {
        val ifuser = findOne(username)
        return ifuser?.let { user ->
            val authorities = user.roles.map {
                SimpleGrantedAuthority(it.name)
            }

            OtherUser(user.name, user.passwordHash, authorities)
        } ?: throw UsernameNotFoundException("no such user: ${username}")
    }

    override fun findOne(id: Long): Admin? {
        val roleids = Admins.join(
            AdminRole,
            joinType = JoinType.INNER,
            onColumn = Admins.id,
            otherColumn = AdminRole.userid
        ).slice(AdminRole.roleid)
            .selectAll()
            .map {
                it[AdminRole.roleid]
            }

        val roles = Roles.select(Roles.id.inList(roleids))
            .map {
                Role(it[Roles.id].value, it[Roles.name])
            }

        return Admins.select(Admins.id eq id)
            .firstOrNull()?.let {
                Admin(it[Admins.id].value, it[Admins.name], it[Admins.passwordHash], roles, it[Admins.enabled])
            }
    }

    override fun findOne(name: String): Admin? {
        val resultrow = Admins.select(Admins.name eq name)
            .firstOrNull()

        if (resultrow == null) {
            return null
        } else {
            val adminid = resultrow[Admins.id]
            val roleids = Admins.join(
                AdminRole,
                joinType = JoinType.INNER,
                onColumn = Admins.id,
                otherColumn = AdminRole.userid
            ).slice(AdminRole.roleid)
                .select(Admins.id eq adminid)
                .map {
                    it[AdminRole.roleid]
                }

            val roles = Roles.select(Roles.id.inList(roleids))
                .map {
                    Role(it[Roles.id].value, it[Roles.name])
                }

            return Admin(adminid.value, resultrow[Admins.name], resultrow[Admins.passwordHash], roles, resultrow[Admins.enabled])
        }
    }

    override fun findAll(): List<Admin> {
        return Admins.selectAll()
            .map {
                val roleids = Admins.join(
                    AdminRole,
                    joinType = JoinType.INNER,
                    onColumn = Admins.id,
                    otherColumn = AdminRole.userid
                ).slice(AdminRole.roleid)
                    .select(Admins.id eq it[Admins.id])
                    .map {
                        it[AdminRole.roleid]
                    }

                val roles = Roles.select(Roles.id.inList(roleids))
                    .map {
                        Role(it[Roles.id].value, it[Roles.name])
                    }

                Admin(it[Admins.id].value, it[Admins.name], it[Admins.passwordHash], roles, it[Admins.enabled])
            }
    }

    override fun findAll(page: Int, size: Int): Page<Admin> {
        val content = Admins.selectAll()
            .map {
                val roleids = Admins.join(
                    AdminRole,
                    joinType = JoinType.INNER,
                    onColumn = Admins.id,
                    otherColumn = AdminRole.userid
                ).slice(AdminRole.roleid)
                    .select(Admins.id eq it[Admins.id])
                    .limit(size, offset = page * size.toLong())
                    .map {
                        it[AdminRole.roleid]
                    }

                val roles = Roles.select(Roles.id.inList(roleids))
                    .map {
                        Role(it[Roles.id].value, it[Roles.name])
                    }

                Admin(it[Admins.id].value, it[Admins.name], it[Admins.passwordHash], roles, it[Admins.enabled])
            }

        val totalPages = ceil(Admins.selectAll().count() / size.toDouble()).toInt()

        return Page(content, totalPages)
    }

    override fun deleteOne(id: Long) {
        AdminRole.deleteWhere {
            userid eq id
        }

        Admins.deleteWhere {
            Admins.id eq id
        }
    }

    override fun updateOne(data: UpdateAdminRequest): Admin {
        Admins.update({ Admins.id eq data.id}) {
            it[name] = data.name
            it[passwordHash] = data.passwordHash
        }

        return findOne(data.id) ?: throw BadRequestException("no such admin with id: ${data.id}")
    }

    override fun insertOne(data: RegisterAdminRequest): Admin {
        val adminid = Admins.insert {
            it[name] = data.name
            it[passwordHash] = data.passwordHash
            it[enabled] = true
        } get Admins.id

        var _roleid: Long? = null
        val roleidQuery = Roles.slice(Roles.id).select(Roles.name eq ADMIN_ROLE_NAME)
            .firstOrNull()

        if (roleidQuery == null) {
            val _id = Roles.insert {
                it[name] = ADMIN_ROLE_NAME
            } get Roles.id

            _roleid = _id.value
        } else {
            _roleid = roleidQuery[Roles.id].value
        }

        AdminRole.insert {
            it[roleid] = _roleid
            it[userid] = adminid
        }

        val roleids = Admins.join(
            AdminRole,
            joinType = JoinType.INNER,
            onColumn = Admins.id,
            otherColumn = AdminRole.userid
        ).slice(AdminRole.roleid)
            .select(Admins.id eq adminid)
            .map {
                it[AdminRole.roleid]
            }

        val roles = Roles.select(Roles.id.inList(roleids))
            .map {
                Role(it[Roles.id].value, it[Roles.name])
            }

        return Admin(adminid.value, data.name, data.passwordHash, roles, true)
    }
}