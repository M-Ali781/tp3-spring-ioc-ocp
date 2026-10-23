package test;
import config.AppConfig;
import metier.*;
import dao.IDao;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import java.util.*;
import static org.junit.Assert.*;
public class OcpSelectionTest {
    private AnnotationConfigApplicationContext context(String target, String... profiles) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles(profiles);
        ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Collections.singletonMap("dao.target", target)));
        ctx.register(AppConfig.class);
        return ctx;
    }
    @Test public void toutesLesSources() {
        String[] names = {"dao", "dao2", "daoFile", "daoApi"};
        double[] expected = {200, 300, 360, 440};
        for(int i=0;i<names.length;i++) try(AnnotationConfigApplicationContext ctx=context(names[i])) {
            ctx.refresh(); assertEquals(expected[i],ctx.getBean(IMetier.class).calcul(),0.001);
            assertSame(ctx.getBean(names[i]), ctx.getBean("dynamicDao"));
        }
    }
    @Test public void profils() {
        String[] profiles={"primary","alias","dev","prod"};
        double[] expected={200,440,300,200};
        for(int i=0;i<profiles.length;i++) try(AnnotationConfigApplicationContext ctx=context("daoFile",profiles[i])) {
            ctx.refresh(); assertEquals(expected[i],ctx.getBean(IMetier.class).calcul(),0.001);
        }
    }
    @Test public void cibleInconnue() {
        try(AnnotationConfigApplicationContext ctx=context("inconnue")) {
            try { ctx.refresh(); fail("La configuration invalide doit echouer"); }
            catch(org.springframework.beans.BeansException ex) { assertTrue(ex.getMostSpecificCause() instanceof IllegalArgumentException); }
        }
    }
    @Test public void injectionMultiple() {
        try(AnnotationConfigApplicationContext ctx=new AnnotationConfigApplicationContext()) {
            ctx.scan("dao"); ctx.register(MetierMultiple.class); ctx.refresh();
            MetierMultiple metier=ctx.getBean(MetierMultiple.class);
            assertEquals(4,metier.getDaoMap().size()); assertEquals(650,metier.calcul(),0.001);
        }
    }
}
