package metier;
import dao.IDao;
import java.util.List;
import java.util.Map;
public class MetierMultiple implements IMetier {
    private final List<IDao> listeDaos;
    private final Map<String, IDao> mapDaos;
    public MetierMultiple(List<IDao> listeDaos, Map<String, IDao> mapDaos) {
        this.listeDaos = listeDaos; this.mapDaos = mapDaos;
    }
    public double calcul() { return listeDaos.stream().mapToDouble(IDao::getValue).sum(); }
    public Map<String, IDao> getDaoMap() { return java.util.Collections.unmodifiableMap(mapDaos); }
}
