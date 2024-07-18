package com.iotiq.user.integration;

import com.iotiq.user.domain.User;
import com.iotiq.user.domain.authorities.BaseRole;
import com.iotiq.user.internal.UserRepository;
import com.iotiq.user.messages.request.UserCreateDto;
import com.iotiq.user.messages.request.UserUpdateDto;
import com.iotiq.user.util.TestUtil;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "classpath:init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:de-init.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@WithUserDetails("admin")
class UserApplicationTests extends IntegrationTestBase {

    private static String id;
    String MAIL = "email@email.com";
    String FIRSTNAME = "First";
    String LASTNAME = "Last";
    String USERNAME = "firstlast";
    String PASSWORD = "pass";


    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

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
        userCreateDto.setRole(BaseRole.ADMIN);

        ResultActions result = mockMvc.perform(
                post("/api/v1/users")
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
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
            assertThat(testUser.getAccountSecurity().getRole()).isEqualTo(BaseRole.ADMIN);
        });
    }

    @Test
    @Order(2)
    void findById() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        assertThat(databaseSizeBeforeCreate).isEqualTo(2);

        ResultActions result = mockMvc.perform(
                get("/api/v1/users/" + id)
                        .with(csrf().asHeader())
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

    @Test
    @Order(3)
    void update() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        assertThat(databaseSizeBeforeCreate).isEqualTo(2);

        UserUpdateDto userUpdateDto = new UserUpdateDto();

        userUpdateDto.setEmail(MAIL + "updated");
        userUpdateDto.setFirstname(FIRSTNAME + "updated");
        userUpdateDto.setLastname(LASTNAME + "updated");
        userUpdateDto.setRole(BaseRole.ADMIN);

        ResultActions result = mockMvc.perform(
                put("/api/v1/users/" + id)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userUpdateDto))
        );

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id", is(id)),
                        jsonPath("$.email", is(MAIL + "updated")),
                        jsonPath("$.firstname", is(FIRSTNAME + "updated")),
                        jsonPath("$.lastname", is(LASTNAME + "updated"))
                );

        assertPersistedUsers(users -> {
            Optional<User> optionalUser = userRepository.findById(UUID.fromString(id));
            assertTrue(optionalUser.isPresent());

            User testUser = optionalUser.get();
            assertThat(testUser.getPersonalInfo().getEmail()).isEqualTo(MAIL + "updated");
            assertThat(testUser.getPersonalInfo().getFirstName()).isEqualTo(FIRSTNAME + "updated");
            assertThat(testUser.getPersonalInfo().getLastName()).isEqualTo(LASTNAME + "updated");
            assertThat(testUser.getAccountSecurity().getRole()).isEqualTo(BaseRole.ADMIN);
        });
    }

    @Test
    @Order(4)
    void remove() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        assertThat(databaseSizeBeforeCreate).isEqualTo(2);

        ResultActions result = mockMvc.perform(
                delete("/api/v1/users/" + id)
                        .with(csrf().asHeader())
        );

        result.andExpect(status().isOk());

        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate - 1));
    }

    private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
        userAssertion.accept(userRepository.findAll());
    }
}
