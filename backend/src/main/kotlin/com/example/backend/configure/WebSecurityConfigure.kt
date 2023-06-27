package com.example.backend.configure

import com.example.backend.encoder.MD5PasswordEncoder
import com.example.backend.filter.AuthenticationFilter
import com.example.backend.filter.LoginFilter
import com.example.backend.service.StudentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(OpenConfiguration::class)
class WebSecurityConfigure {
    companion object {
        val logger = LoggerFactory.getLogger(WebSecurityConfigure::class.java)
    }

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(authenticationManagerBuilder: AuthenticationManagerBuilder,
                        studentService: StudentService,
                        mD5PasswordEncoder: MD5PasswordEncoder) {
        authenticationManagerBuilder
            .userDetailsService(studentService)
            .passwordEncoder(mD5PasswordEncoder)
    }

    @Bean
    fun openConfiguration(): OpenConfiguration {
        return OpenConfiguration()
    }

    @Autowired
    lateinit var openConfig: OpenConfiguration
    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity, loginFilter: LoginFilter, authenticateFilter: AuthenticationFilter): SecurityFilterChain {

        http.csrf().disable()
            .authorizeHttpRequests()
//            .requestMatchers(authenticateUrl, registerUrl).permitAll()
            .requestMatchers(*openConfig.urls).permitAll()
            .requestMatchers("/admin/**").hasAuthority("admin")
            .requestMatchers("/user/**").hasAnyAuthority(*openConfig.roles)
            .and()
            .authorizeHttpRequests().requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterAt(authenticateFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}