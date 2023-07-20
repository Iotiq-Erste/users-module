package com.iotiq.user;


import com.iotiq.user.domain.authorities.BaseRole;
import com.iotiq.user.internal.UserRepository;
import com.iotiq.user.internal.UserService;
import com.iotiq.user.messages.request.UserCreateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppSetup implements ApplicationRunner {
    private final String adminPass;
    private final UserRepository userRepository;
    private final UserService userService;

    public AppSetup(
            //@Value("${seed.admin.pass}") final String adminPass,
            UserRepository userRepository,
            UserService userService
    ) {
        this.userRepository = userRepository;
        //this.adminPass = adminPass;
        this.adminPass = "pass";
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
    }

    private void seedUsers() {
        if (!userRepository.existsByAccountInfoUsername("admin")) {
            UserCreateDto request = new UserCreateDto();
            request.setUsername("admin");
            request.setPassword(adminPass);
            request.setRole(BaseRole.ADMIN);
            request.setEmail("email@m.com");
            request.setFirstname("fn");
            request.setLastname("ln");
            userService.create(request);
        }
    }
}
