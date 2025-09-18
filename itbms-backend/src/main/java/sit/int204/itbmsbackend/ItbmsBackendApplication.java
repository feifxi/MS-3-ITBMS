package sit.int204.itbmsbackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import sit.int204.itbmsbackend.util.ListMapper;


@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ItbmsBackendApplication {

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
