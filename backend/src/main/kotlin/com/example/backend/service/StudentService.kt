package com.example.backend.service

import com.example.backend.constant.DEFAULT_ROLE_NAME
import com.example.backend.exception.BadRequestException
import com.example.backend.model.Role
import com.example.backend.model.Student
import com.example.backend.request.RegisterStudentRequest
import com.example.backend.request.UpdateStudentRequest
import com.example.backend.table.Roles
import com.example.backend.table.StudentRole
import com.example.backend.table.Students
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
class StudentService : UserDetailsService, UserService<Student, RegisterStudentRequest, UpdateStudentRequest> {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val ifuser = findOne(username)
        return ifuser?.let { user ->
            val authorities = user.roles.map {
                SimpleGrantedAuthority(it.name)
            }

            OtherUser(user.name, user.passwordHash, authorities)
        } ?: throw UsernameNotFoundException("no such user: ${username}")
    }

    override fun findOne(id: Long): Student? {
        val roleids = Students.join(
            StudentRole,
            joinType = JoinType.INNER,
            onColumn = Students.id,
            otherColumn = StudentRole.userid
        ).select(StudentRole.userid eq id)
            .map {
                it[StudentRole.roleid]
            }

        val roles = Roles.select(Roles.id.inList(roleids))
            .map {
                Role(it[Roles.id].value, it[Roles.name])
            }

        return Students.select(Students.id eq id)
            .firstOrNull()?.let {
                Student(
                    it[Students.id].value,
                    it[Students.name],
                    it[Students.passwordHash],
                    roles,
                    it[Students.enabled],
                    it[Students.grade],
                    it[Students.major],
                    it[Students.clazz],
                    it[Students.institute],
                    it[Students.telephone],
                    it[Students.email],
                    it[Students.cardId],
                    it[Students.sex]
                )
            }
    }

    override fun findOne(name: String): Student? {
        val roleids = Students.join(
            StudentRole,
            joinType = JoinType.INNER,
            onColumn = Students.id,
            otherColumn = StudentRole.userid
        ).select(Students.name eq name)
            .map {
                it[StudentRole.roleid]
            }

        val roles = Roles.select(Roles.id.inList(roleids))
            .map {
                Role(it[Roles.id].value, it[Roles.name])
            }

        return Students.select(Students.name eq name)
            .firstOrNull()?.let {
                Student(
                    it[Students.id].value,
                    it[Students.name],
                    it[Students.passwordHash],
                    roles,
                    it[Students.enabled],
                    it[Students.grade],
                    it[Students.major],
                    it[Students.clazz],
                    it[Students.institute],
                    it[Students.telephone],
                    it[Students.email],
                    it[Students.cardId],
                    it[Students.sex]
                )
            }
    }

    override fun findAll(): List<Student> {
        return Students.selectAll()
            .map {
                val studentid = it[Students.id]
                val roleids = Students.join(
                    StudentRole,
                    joinType = JoinType.INNER,
                    onColumn = Students.id,
                    otherColumn = StudentRole.userid
                ).select(StudentRole.userid eq studentid)
                    .map {
                        it[StudentRole.roleid]
                    }

                val roles = Roles.select(Roles.id.inList(roleids))
                    .map {
                        Role(it[Roles.id].value, it[Roles.name])
                    }

                Student(
                    it[Students.id].value,
                    it[Students.name],
                    it[Students.passwordHash],
                    roles,
                    it[Students.enabled],
                    it[Students.grade],
                    it[Students.major],
                    it[Students.clazz],
                    it[Students.institute],
                    it[Students.telephone],
                    it[Students.email],
                    it[Students.cardId],
                    it[Students.sex]
                )
            }

    }

    override fun findAll(page: Int, size: Int): Page<Student> {
        val content = Students.selectAll()
            .limit(size, offset = page * size.toLong())
            .map {
                val studentid = it[Students.id]
                val roleids = Students.join(
                    StudentRole,
                    joinType = JoinType.INNER,
                    onColumn = Students.id,
                    otherColumn = StudentRole.userid
                ).select(StudentRole.userid eq studentid)
                    .map {
                        it[StudentRole.roleid]
                    }

                val roles = Roles.select(Roles.id.inList(roleids))
                    .map {
                        Role(it[Roles.id].value, it[Roles.name])
                    }

                Student(
                    it[Students.id].value,
                    it[Students.name],
                    it[Students.passwordHash],
                    roles,
                    it[Students.enabled],
                    it[Students.grade],
                    it[Students.major],
                    it[Students.clazz],
                    it[Students.institute],
                    it[Students.telephone],
                    it[Students.email],
                    it[Students.cardId],
                    it[Students.sex]
                )
            }

        val totalPages = ceil(Students.selectAll().count() / size.toDouble()).toInt()

        return Page(content, totalPages)
    }

    override fun deleteOne(id: Long) {
        StudentRole.deleteWhere {
            userid eq id
        }

        Students.deleteWhere {
            Students.id eq id
        }
    }

    override fun updateOne(data: UpdateStudentRequest): Student {
        Students.update({Students.id eq data.id}) {
            it[name] = data.name
            it[grade] = data.grade
            it[major] = data.major
            it[clazz] = data.clazz
            it[institute] = data.institute
            it[telephone] = data.telephone
            it[email] = data.email
            it[passwordHash] = data.passwordHash
            it[cardId] = data.cardId
            it[sex] = data.sex
        }

        return findOne(data.id) ?: throw BadRequestException("no such student with id: ${data.id}")
    }

    override fun insertOne(data: RegisterStudentRequest): Student {
        println("start")
        val studentid = Students.insert {
            it[name] = data.name
            it[passwordHash] = data.passwordHash
            it[cardId] = data.cardId
            it[clazz] = data.clazz
            it[email] = data.email
            it[major] = data.major
            it[institute] = data.institute
            it[grade] = data.grade
            it[sex] = data.sex
            it[telephone] = data.telephone
            it[enabled] = true
        } get Students.id

        println("start over")
        var _roleid: Long? = null
        val roleidQuery = Roles.slice(Roles.id).select(Roles.name eq DEFAULT_ROLE_NAME)
            .firstOrNull()

        if (roleidQuery == null) {
            val _id = Roles.insert {
                it[name] = DEFAULT_ROLE_NAME
            } get Roles.id

            _roleid = _id.value
        } else {
            _roleid = roleidQuery[Roles.id].value
        }

        StudentRole.insert {
            it[roleid] = _roleid
            it[userid] = studentid
        }

        val roleids = Students.join(
            StudentRole,
            joinType = JoinType.INNER,
            onColumn = Students.id,
            otherColumn = StudentRole.userid
        ).slice(StudentRole.roleid)
            .select(Students.id eq studentid)
            .map {
                it[StudentRole.roleid]
            }

        val roles = Roles.select(Roles.id.inList(roleids))
            .map {
                Role(it[Roles.id].value, it[Roles.name])
            }

        return Student(
            studentid.value,
            data.name,
            data.passwordHash,
            roles,
            true,
            data.grade,
            data.major,
            data.clazz,
            data.institute,
            data.telephone,
            data.email,
            data.cardId,
            data.sex
        )
    }
}