import config.AppConfig;
import metier.IMetier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            // Les profils doivent etre actives avant refresh().
            if (args.length > 0) context.getEnvironment().setActiveProfiles(args);
            context.register(AppConfig.class);
            context.refresh();
            System.out.println("Resultat final : " + context.getBean(IMetier.class).calcul());
        }
    }
}
