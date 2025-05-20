package bg.tu_varna.sit.usp.phone_sales.user.service;

import bg.tu_varna.sit.usp.phone_sales.cart.model.Cart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.exception.*;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.model.UserRole;
import bg.tu_varna.sit.usp.phone_sales.user.repository.UserRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ChangePasswordRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.PersonalInformationResponse;
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
import java.util.Objects;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CartService cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
        log.info("Attempting to register user: {}", registerRequest.getEmail());

        checkIfEmailAlreadyExists(registerRequest.getEmail());
        checkIfPasswordsMatch(registerRequest.getPassword(), registerRequest.getConfirmPassword());

        User user = setupUser(registerRequest);
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

            return new UserWithEmailDoesntExistException(ExceptionMessages.USER_WITH_EMAIL_DOESNT_EXIST);
        });
    }

    public User getAuthenticatedUser(AuthenticationMetadata auth) {
        if (null == auth) {
            return null;
        }

        return getByEmail(auth.getEmail());
    }

    public boolean userCountMoreThanZero() {
        return userRepository.count() > 0;
    }

    public void updateUserPersonalInformationPreference(String zipCode, String address, String city, String phoneNumber, User user) {
        boolean isNew = user.getAddress() == null || user.getCity() == null || user.getPhoneNumber() == null;

        boolean isUnchanged = Objects.equals(address, user.getAddress()) &&
                Objects.equals(city, user.getCity()) &&
                Objects.equals(phoneNumber, user.getPhoneNumber()) &&
                Objects.equals(zipCode, user.getZipCode());


        if (!isNew && isUnchanged) {
            log.info("User address, city, and phone have not changed since last order");
            return;
        }

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent() && !Objects.equals(phoneNumber, user.getPhoneNumber())) {
            throw new UserWithThisPhoneNumberAlreadyExistsException(ExceptionMessages.USER_WITH_THIS_PHONE_NUMBER_ALREADY_EXISTS);
        }

        user.setAddress(address);
        user.setCity(city);
        user.setPhoneNumber(phoneNumber);
        user.setZipCode(zipCode);

        log.info("Updating user address, city, and phone number preference");
        userRepository.save(user);
    }

    public void updateUserFirstAndLastNamePreference(String firstName, String lastName, User user) {
        boolean isNew = user.getFirstName() == null || user.getLastName() == null;
        boolean isUnchanged = Objects.equals(firstName, user.getFirstName()) &&
                Objects.equals(lastName, user.getLastName());
        if (!isNew && isUnchanged) {
            log.info("User first and last name have not changed since last order");
            return;
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        log.info("Updating user first and last name preference");
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest, User user) {
        String oldPassword = changePasswordRequest.getOldPassword();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                userRepository.save(user);
                log.info("Successfully changed password");
                return;
            }
        }
        throw new InvalidChangePasswordRequestException(ExceptionMessages.INVALID_CHANGE_PASSWORD_REQUEST);
    }

    public void insertAdmin() {
        User admin = initializeAdmin();
        log.info("Inserting admin user");

        userRepository.save(admin);
    }

    public PersonalInformationResponse getPersonalInformationResponse(User user) {
        return initializePersonalInformationResponse(user);
    }

    @Transactional
    public User setupUser(RegisterRequest registerRequest) {
        User user = userRepository.save(initializeUser(registerRequest));

        Cart cart = cartService.initializeCartForUser(user);
        user.setCart(cart);

        return userRepository.save(user);
    }

    private PersonalInformationResponse initializePersonalInformationResponse(User user) {
        return PersonalInformationResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .zipCode(user.getZipCode())
                .build();
    }

    private User initializeAdmin() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("admin")
                .password("admin")
                .confirmPassword("admin")
                .build();

        User user = initializeUser(registerRequest);
        user.setRole(UserRole.ADMIN);

        Cart cart = cartService.initializeCartForUser(user);
        user.setCart(cart);

        return userRepository.save(user);
    }


    private void checkIfPasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordsDoNotMatchException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
        }
        log.info("Passwords match");
    }

    private void checkIfEmailAlreadyExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserWithEmailAlreadyExistsException(ExceptionMessages.USER_WITH_EMAIL_ALREADY_EXISTS);
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
