package com.idan.restDemo.service

import com.idan.restDemo.model.User
import com.idan.restDemo.repository.UserRepository
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private lateinit var userService: UserService

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = User(1, "Idan", "idan@email.com")
        userService = UserService(userRepository)
    }

    @Test
    fun testGetAllUsers() {
        // Arrange
        whenever(userRepository.findAll()).thenReturn(listOf(testUser))

        // Act
        val result = userService.getAllUsers()

        // Assert
        Assertions.assertEquals(listOf(testUser), result)
    }

    @Test
    fun testGetUserById() {
        // Arrange
        whenever(userRepository.findById(1)).thenReturn(Optional.of(testUser))
        whenever(userRepository.findById(2)).thenReturn(Optional.empty())

        // Act
        val result1 = userService.getUserById(1)
        val result2 = userService.getUserById(2)

        // Assert
        kotlin.test.assertNotNull(result1)
        Assertions.assertEquals("Idan", result1?.name)
        kotlin.test.assertNull(result2)
    }

    @Test
    fun testUpdateUser() {
        // Arrange
        val updatedUser = testUser.copy(name = "Updated Name", email = "idan@email.com")
        whenever(userRepository.findById(1)).thenReturn(Optional.of(testUser))
        whenever(userRepository.save(testUser)).thenReturn(updatedUser)

        // Act
        val result = userService.updateUser(1, updatedUser)

        // Assert
        kotlin.test.assertNotNull(result)
        Assertions.assertEquals("Updated Name", result.name)
        Assertions.assertEquals("idan@email.com", result.email)
    }

    @Test
    fun testUpdateUser_InvalidInput() {
        // Arrange
        val invalidUser1 = testUser.copy(name = "")
        val invalidUser2 = testUser.copy(email = "invalid-email")

        whenever(userRepository.findById(1)).thenReturn(Optional.of(testUser))

        // Act & Assert
        val exception1 = assertThrows<ResponseStatusException> {
            userService.updateUser(1, invalidUser1)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exception1.statusCode.value())
        Assertions.assertEquals("Name and email cannot be empty", exception1.reason)

        val exception2 = assertThrows<ResponseStatusException> {
            userService.updateUser(1, invalidUser2)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exception2.statusCode.value())
        Assertions.assertEquals("Invalid email format", exception2.reason)
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
