package sit.int204.itbmsbackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sit.int204.itbmsbackend.utils.ListMapper;


@SpringBootApplication
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
