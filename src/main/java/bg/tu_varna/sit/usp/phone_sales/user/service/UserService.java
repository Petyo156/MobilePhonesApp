package bg.tu_varna.sit.usp.phone_sales.user.service;

import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.City;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.model.UserRole;
import bg.tu_varna.sit.usp.phone_sales.user.repository.UserRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ChangePasswordRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
        log.info("Attempting to register user: {}", registerRequest.getEmail());

        checkIfEmailAlreadyExists(registerRequest.getEmail());
        checkIfPasswordsMatch(registerRequest.getPassword(), registerRequest.getConfirmPassword());

        User user = initializeUser(registerRequest);
        userRepository.save(user);

        log.info("Successfully registered user");
    }

    //method override from UserDetailsService interface
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by email...");
        User user = getByEmail(email);

        return new AuthenticationMetadata(user.getId(), email, user.getPassword(), user.getRole());
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {

            log.error("User with email '{}' does not exist", email);

            return new UsernameNotFoundException(ExceptionMessages.USER_WITH_EMAIL_DOESNT_EXIST);
        });
    }

    public User getAuthenticatedUser(AuthenticationMetadata auth) {
        if (auth == null) {
            return null;
        }

        return getByEmail(auth.getEmail());
    }

    public boolean userCountMoreThanZero() {
        return userRepository.count() > 0;
    }

    public void updateUserAddressPreference(String address, City city, User user) {
        if(user.getAddress().equals(address) && user.getCity().equals(city)) {
            log.info("User address and city have not changed since last order");
            return;
        }
        user.setAddress(address);
        user.setCity(city);
        log.info("Updating user address and city preference");
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest, User user) {
        String oldPassword = changePasswordRequest.getOldPassword();
        if(passwordEncoder.matches(oldPassword, user.getPassword())) {
            if(changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                userRepository.save(user);
                log.info("Successfully changed password");
                return;
            }
        }
        throw new DomainException(ExceptionMessages.INVALID_CHANGE_PASSWORD_REQUEST);
    }

    public void insertAdmin() {
        User admin = initializeAdmin();
        log.info("Inserting admin user");

        userRepository.save(admin);
    }

    private User initializeAdmin() {
        return User.builder()
                .email("admin")
                .password(passwordEncoder.encode("admin"))
                .createdAt(LocalDateTime.now())
                .role(UserRole.ADMIN)
                .build();
    }

    private void checkIfPasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new DomainException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
        }
        log.info("Passwords match");
    }

    private void checkIfEmailAlreadyExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DomainException(ExceptionMessages.USER_WITH_EMAIL_ALREADY_EXISTS);
        }
        log.info("Email is valid");
    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .createdAt(LocalDateTime.now())
                .role(UserRole.USER)
                .build();
    }
}
