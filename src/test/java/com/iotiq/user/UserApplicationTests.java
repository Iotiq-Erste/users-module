package com.iotiq.user;

import com.iotiq.user.domain.Role;
import com.iotiq.user.domain.User;
import com.iotiq.user.internal.UserRepository;
import com.iotiq.user.messages.UserCreateDto;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserApplicationTests {

    private static String id;
    String MAIL = "email@email.com";
    String FIRSTNAME = "First";
    String LASTNAME = "Last";
    String USERNAME = "firstlast";
    String PASSWORD = "pass";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    void add() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        UserCreateDto userCreateDto = new UserCreateDto();

        userCreateDto.setEmail(MAIL);
        userCreateDto.setFirstname(FIRSTNAME);
        userCreateDto.setLastname(LASTNAME);
        userCreateDto.setUsername(USERNAME);
        userCreateDto.setPassword(PASSWORD);
        userCreateDto.setRole(Role.ADMIN);

        ResultActions result = mockMvc.perform(
                post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userCreateDto))
        );

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()));

        id = JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.id");

        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
            User testUser = users.get(users.size() - 1);
            assertThat(testUser.getPersonalInfo().getEmail()).isEqualTo(MAIL);
            assertThat(testUser.getPersonalInfo().getFirstName()).isEqualTo(FIRSTNAME);
            assertThat(testUser.getPersonalInfo().getLastName()).isEqualTo(LASTNAME);
            assertThat(testUser.getAccountSecurity().getRole()).isEqualTo(Role.ADMIN);
        });
    }

    @Test
    @Order(2)
    void findById() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        assertThat(databaseSizeBeforeCreate).isEqualTo(1);

        ResultActions result = mockMvc
                .perform(
                        get("/api/v1/users/" + id)
                );

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id", is(id)),
                        jsonPath("$.firstname", is(FIRSTNAME)),
                        jsonPath("$.lastname", is(LASTNAME)),
                        jsonPath("$.username", is(USERNAME))
                );
    }

    private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
        userAssertion.accept(userRepository.findAll());
    }
}