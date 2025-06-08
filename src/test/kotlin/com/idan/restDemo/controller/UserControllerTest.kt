package com.idan.restDemo.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.idan.restDemo.model.User
import com.idan.restDemo.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    private val objectMapper = jacksonObjectMapper()

    private val testUser = User(1, "Idan", "idan@email.com")

    @BeforeEach
    fun setUp() {
        every { userService.getAllUsers() } returns listOf(testUser)
        every { userService.getUserById(1) } returns testUser
        every { userService.getUserById(2) } returns null
        every { userService.updateUser(1, any()) } returns testUser.copy(name = "Updated Name")
        every { userService.updateUser(2, any()) } returns null
        every { userService.deleteUser(any<Long>()) } returns Unit
    }

    @Test
    fun testGetAllUsers() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Idan"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("idan@email.com"))
    }

    @Test
    fun testGetUserById() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Idan"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("idan@email.com"))

        mockMvc.perform(MockMvcRequestBuilders.get("/users/2"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun testUpdateUser() {
        val updatedUser = testUser.copy(name = "Updated Name")
        val requestBody = objectMapper.writeValueAsString(updatedUser)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Name"))

        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun testDeleteUser() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}