package bg.tu_varna.sit.usp.phone_sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PhoneSalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneSalesApplication.class, args);
    }
}
