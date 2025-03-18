package com.idan.restDemo.service

import com.idan.restDemo.model.User
import com.idan.restDemo.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.Optional
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getUserById(id: Long): User? {
        val optionalUser = userRepository.findById(id)
        return optionalUser.orElse(null)
    }

    fun updateUser(id: Long, user: User): User? {
        if (user.name.isNullOrBlank() || user.email.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and email cannot be empty")
        }

        if (!isValidEmail(user.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format")
        }

        val optionalExistingUser = userRepository.findById(id)
        return if (optionalExistingUser.isPresent) {
            val existingUser = optionalExistingUser.get()
            existingUser.name = user.name
            existingUser.email = user.email
            userRepository.save(existingUser)
            existingUser
        } else {
            null
        }
    }

    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return emailRegex.matches(email)
    }
}
