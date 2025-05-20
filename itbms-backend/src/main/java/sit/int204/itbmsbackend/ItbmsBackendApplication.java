package sit.int204.itbmsbackend;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sit.int204.itbmsbackend.utils.ListMapper;

import java.util.TimeZone;

@SpringBootApplication
public class ItbmsBackendApplication {

    @PostConstruct
    public void init() {
        // Set the global timezone for the JVM
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // or "Asia/Kolkata"
        System.out.println("Timezone set to: " + TimeZone.getDefault().getID());
    }

    public static void main(String[] args) {
        SpringApplication.run(ItbmsBackendApplication.class, args);
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }
}
