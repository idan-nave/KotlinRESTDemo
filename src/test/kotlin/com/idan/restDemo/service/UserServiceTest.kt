package com.idan.restDemo.service

import com.idan.restDemo.model.User
import com.idan.restDemo.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import java.util.Optional
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.mockito.kotlin.verify
import org.mockito.kotlin.times

class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val userService = UserService(userRepository)

    @Test
    fun testGetAllUsers() {
        // Arrange
        val user = User(1, "Idan", "idan@email.com")
        val usersList = listOf(user)
        whenever(userRepository.findAll()).thenReturn(usersList)

        // Act
        val result = userService.getAllUsers()

        // Assert
        assertEquals(usersList, result)
    }

    @Test
    fun testGetUserById() {
        // Arrange
        val user = User(1, "Idan", "idan@email.com")
        whenever(userRepository.findById(1)).thenReturn(Optional.of(user))
        whenever(userRepository.findById(2)).thenReturn(Optional.empty())

        // Act
        val result = userService.getUserById(1)
        val result2 = userService.getUserById(2)

        // Assert
        assertNotNull(result)
        assertEquals("Idan", result?.name)
        assertNull(result2)
    }

    @Test
    fun testUpdateUser() {
        // Arrange
        val user = User(1, "Idan", "idan@email.com")
        val updatedUser = User(1, "Updated Name", "updated@email.com")
        whenever(userRepository.findById(1)).thenReturn(Optional.of(user))
        whenever(userRepository.save(user)).thenReturn(updatedUser)

        // Act
        val result = userService.updateUser(1, updatedUser)

        // Assert
        assertNotNull(result)
        assertEquals("Updated Name", result.name)
        assertEquals("updated@email.com", result.email)
    }

    @Test
    fun testUpdateUser_InvalidInput() {
        // Arrange
        val user = User(1, "Idan", "idan@email.com")
        val invalidUser1 = User(1, "", "idan@email.com")
        val invalidUser2 = User(1, "Idan", "invalid-email")

        whenever(userRepository.findById(1)).thenReturn(Optional.of(user))

        // Act & Assert
        val exception1 = assertThrows<ResponseStatusException> {
            userService.updateUser(1, invalidUser1)
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception1.statusCode.value())
        assertEquals("Name and email cannot be empty", exception1.reason)

        val exception2 = assertThrows<ResponseStatusException> {
            userService.updateUser(1, invalidUser2)
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception2.statusCode.value())
        assertEquals("Invalid email format", exception2.reason)
    }

    @Test
    fun testDeleteUser() {
        // Arrange
        val userId = 1L

        // Act
        userService.deleteUser(userId)

        // Assert
        verify(userRepository, times(1)).deleteById(userId)
    }
}
