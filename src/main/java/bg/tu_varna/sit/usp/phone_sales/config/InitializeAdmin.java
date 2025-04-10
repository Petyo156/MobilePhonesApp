package bg.tu_varna.sit.usp.phone_sales.config;

import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class InitializeAdmin implements CommandLineRunner {
    private final UserService userService;

    @Autowired
    public InitializeAdmin(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.userCountMoreThanZero()){
            return;
        }

        userService.insertAdmin();
    }
}
