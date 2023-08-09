package com.example.backend

import com.example.backend.constant.ADMIN_ROLE_NAME
import com.example.backend.constant.DEFAULT_ROLE_NAME
import com.example.backend.model.Admin
import com.example.backend.model.Role
import com.example.backend.request.RegisterAdminRequest
import com.example.backend.request.RegisterRoleRequest
import com.example.backend.request.RegisterStudentRequest
import com.example.backend.service.AdminService
import com.example.backend.service.RoleService
import com.example.backend.service.StudentService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UsernameNotFoundException

@SpringBootTest
class BackendApplicationTests {
    @Autowired
    lateinit var adminService: AdminService
    @Autowired
    lateinit var roleService: RoleService
    @Autowired
    lateinit var studentService: StudentService
    @Test
    fun contextLoads() {
    }

    @Test
    fun injectUserAndAdmin() {
        val roles = roleService.findAll()
        if (roles.isEmpty()) {
            roleService.insertOne(RegisterRoleRequest(ADMIN_ROLE_NAME))
            roleService.insertOne(RegisterRoleRequest(DEFAULT_ROLE_NAME))

            roleService.findAll()
        }

        adminService.insertOne(RegisterAdminRequest(
            "admin",
            "5f4dcc3b5aa765d61d8327deb882cf99",
        ))

        studentService.insertOne(RegisterStudentRequest(
            "steiner",
            "大二",
            "软件工程",
            "200917102",
            "计算机",
            "18967700988",
            "steiner3044@163.com",
            "5f4dcc3b5aa765d61d8327deb882cf99",
            "330329200104276274",
            "男"
        ))

    }

}
