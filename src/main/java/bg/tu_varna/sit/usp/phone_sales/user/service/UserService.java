package bg.tu_varna.sit.usp.phone_sales.user.service;

import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.repository.UserRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(RegisterRequest registerRequest) {
        User user = new User();
        userRepository.save(user);
    }
}
