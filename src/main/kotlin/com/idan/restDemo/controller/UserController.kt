package com.idan.restDemo.controller

import com.idan.restDemo.model.User
import com.idan.restDemo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): User? {
        return userService.getUserById(id) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "User not found"
        )
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): User? {
        return userService.updateUser(id, user) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "User not found"
        )
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}