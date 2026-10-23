package dao;
import org.springframework.stereotype.Component;
@Component("daoApi")

public class DaoApi implements IDao{
    @Override
    public double getValue(){
        System.out.println("[DAO] Appel a l'API REST externe ");
        return 220.0;
    }
}
