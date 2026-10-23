# TP3 — Spring IoC et OCP

**Réalisé par :** Ali Mahraoui

Le métier existant reste inchangé. Les DAO fichier et API sont des simulations, conformément au TP.

## Exécution (JDK 11+, Maven)

```powershell
mvn clean verify
java -jar target/Tp3-1.0-SNAPSHOT.jar
java -Ddao.target=daoFile -jar target/Tp3-1.0-SNAPSHOT.jar
$env:DAO_TARGET="dao2"
java -jar target/Tp3-1.0-SNAPSHOT.jar
Remove-Item Env:DAO_TARGET
java -jar target/Tp3-1.0-SNAPSHOT.jar primary
java -jar target/Tp3-1.0-SNAPSHOT.jar alias
java -jar target/Tp3-1.0-SNAPSHOT.jar dev
java -jar target/Tp3-1.0-SNAPSHOT.jar prod
```

Sans profil : sélection par `dao.target` (app.properties : daoApi). Résultats : dao=200, dao2=300, daoFile=360, daoApi=440. La propriété système Java prime sur la variable environnement, qui prime sur app.properties. Le choix est effectué au démarrage : redémarrer pour le changer. Spring Framework ne recharge pas automatiquement les propriétés.

Choisir un seul profil de sélection : primary/prod utilisent dao, alias utilise daoApi, dev utilise dao2. Les configurations sont séparées pour éviter plusieurs candidats primaires. Les beans de sélection réutilisent les instances gérées par Spring ; ils ne créent pas de nouveaux DAO. La sélection dynamique résout un nom de bean sans switch : une nouvelle implémentation peut être ajoutée sans modifier le métier ni le sélecteur.

La trace du choix dynamique est dans la configuration pour conserver MetierImpl inchangé. Les profils sont activés avant refresh(). @Qualifier est utilisé uniquement dans la configuration pour désigner une dépendance précise.

L'extension MetierMultiple est enregistrée dans un contexte séparé par le test : les quatre DAO sont injectés dans List et Map et donnent une somme de 650. Cela évite de compter aussi le bean de sélection et de créer une ambiguïté entre deux IMetier.

Les tests vérifient les quatre sources, les profils, le rejet des noms inconnus et l'agrégation. Ils vérifient les comportements avec le même métier, sans prétendre prouver à eux seuls que son fichier source reste inchangé.
