package config;
import dao.IDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
@Configuration
public class ProfileDaoConfig {
    @Bean @Primary @Profile("primary | prod")
    public IDao productionDao(@Qualifier("dao") IDao dao) { return dao; }
    @Bean @Primary @Profile("dev")
    public IDao developmentDao(@Qualifier("dao2") IDao dao) { return dao; }
}
