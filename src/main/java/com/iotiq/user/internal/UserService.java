package com.iotiq.user.internal;

import com.iotiq.commons.domain.AbstractMapper;
import com.iotiq.commons.exceptions.RequiredFieldMissingException;
import com.iotiq.commons.util.PasswordUtil;
import com.iotiq.user.domain.User;
import com.iotiq.user.domain.authorities.UserManagementAuthority;
import com.iotiq.user.exceptions.DuplicateUserDataException;
import com.iotiq.user.exceptions.InvalidCredentialException;
import com.iotiq.user.exceptions.UserNotFoundException;
import com.iotiq.user.messages.request.*;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ExpressionMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static com.iotiq.commons.util.NullHandlerUtil.setIfNotNull;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserService {

    private final UserMapper userMapper = new UserMapper();
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final PasswordEncoder passwordEncoder;

    public Page<User> findAll(UserFilter userFilter, Sort sort) {
        return userRepository.findAll(userFilter.buildSpecification(), userFilter.buildPageable(sort));
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findByUserName(String username) {
        return userRepository.findByAccountInfoUsername(username).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public boolean existByUserName(String username) {
        return userRepository.existsByAccountInfoUsername(username);
    }

    @Transactional
    public User create(UserCreateDto request) {
        validateUniqueUserData(request.getUsername(), request.getEmail());

        User user = new User();

        userMapper.map(request, user);
        setIfNotNull(user::setPassword, () -> passwordUtil.encode(request.getPassword()), request.getPassword());
        setIfNotNull(user::setRole, request::getRole);
        setIfNotNull(user::setUsername, request::getUsername);
        setIfNotNull(s -> user.getPersonalInfo().setFirstName(s), request::getFirstname);
        setIfNotNull(s -> user.getPersonalInfo().setLastName(s), request::getLastname);
        setIfNotNull(s -> user.getPersonalInfo().setEmail(s), request::getEmail);

        return userRepository.save(user);
    }

    @Transactional
    public User update(UUID id, UserUpdateDto request) {
        validateUniqueUserDataWithExclusion(id, request.getUsername(), request.getEmail());

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        user.setUsername(request.getUsername());
        user.getPersonalInfo().setFirstName(request.getFirstname());
        user.getPersonalInfo().setLastName(request.getLastname());
        user.getPersonalInfo().setEmail(request.getEmail());
        user.setRole(request.getRole());

        return user;
    }
    private void validateUniqueUserData(String username, String email) {
        boolean exists = userRepository.existsByAccountInfoUsernameOrPersonalInfoEmail(username, email);
        if (exists) {
            throw new DuplicateUserDataException("usernameOrEmail");
        }
    }

    public void validateUniqueUserDataWithExclusion(UUID id, String username, String email) {
        if (userRepository.existsByAccountInfoUsernameAndIdIsNot(username, id)) {
            throw new DuplicateUserDataException("username");
        }
        if (userRepository.existsByPersonalInfoEmailAndIdIsNot(email, id)) {
            throw new DuplicateUserDataException("email");
        }
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User changePassword(UUID id, UpdatePasswordDto request) {
        if (request.getNewPassword() == null) {
            throw new RequiredFieldMissingException("newPassword");
        }
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal.getAuthorities().contains(UserManagementAuthority.CHANGE_PASSWORD)) {
            setPassword(user, request.getNewPassword());
        } else if (Objects.equals(principal.getId(), user.getId())) {
            updatePassword(user, request);
        } else {
            throw new InvalidCredentialException();
        }
        userRepository.save(user);

        return user;
    }

    private void updateIfAllowed(UserUpdateDto request, User user) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getAuthorities().contains(UserManagementAuthority.UPDATE)) {
            user.setUsername(request.getUsername());
            user.setRole(request.getRole());
        }
    }

    private void updatePassword(User user, UpdatePasswordDto request) {
        if (StringUtils.isBlank(request.getOldPassword())) {
            throw new RequiredFieldMissingException("oldPassword");
        }
        boolean matches = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (matches) {
            setPassword(user, request.getNewPassword());
        } else {
            throw new InvalidCredentialException();
        }
    }

    private void setPassword(User user, String newPassword) {
        user.setPassword(passwordUtil.encode(newPassword));
    }

    protected static class UserMapper extends AbstractMapper<UserUpdateDto, User> {

        protected UserMapper() {
            super(UserUpdateDto.class, User.class);
        }

        @Override
        protected ExpressionMap<UserUpdateDto, User> getMappings() {
            return mapping -> {
                mapping.skip(User::setPassword);
                mapping.skip(User::setRole);
                mapping.skip(User::setUsername);
            };
        }
    }

    public User getCurrentUser() {
        // Retrieve the currently logged-in user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new InvalidCredentialException();
        return findByUserName(userDetails.getUsername());
    }

    @Transactional
    public User updateProfile(User user, ProfileUpdateRequest request) {
        if (userRepository.existsByAccountInfoUsernameAndIdIsNot(request.getUsername(), user.getId())) {
            throw new DuplicateUserDataException("username");
        }
        user.setUsername(request.getUsername());
        user.getPersonalInfo().setFirstName(request.getFirstname());
        user.getPersonalInfo().setLastName(request.getLastname());

        return user;
    }
}
