package metier;
import dao.IDao;
import org.springframework.stereotype.Service;
@Service
public class MetierImpl implements IMetier{
    private final IDao daoSource;
    public MetierImpl(IDao daoSource) {
        this.daoSource = daoSource;
    }

    @Override
    public double calcul() {
        double valeur = daoSource.getValue();return valeur * 2.0;
    }
}
