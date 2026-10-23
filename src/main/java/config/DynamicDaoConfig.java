package config;
import dao.IDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
@Configuration
@Profile("!primary & !alias & !dev & !prod")
public class DynamicDaoConfig {
    @Bean @Primary
    public IDao dynamicDao(Environment env, ApplicationContext context) {
        String target = env.getProperty("dao.target", "dao");
        if ("dynamicDao".equals(target) || !context.containsBean(target)
                || !context.isTypeMatch(target, IDao.class)) {
            throw new IllegalArgumentException("Cible DAO inconnue : " + target);
        }
        IDao selected = context.getBean(target, IDao.class);
        System.out.println("[TRACE] Implementation DAO injectee : " + selected.getClass().getSimpleName());
        return selected;
    }
}
