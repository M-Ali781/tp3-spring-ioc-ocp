# TP3 — Inversion de Contrôle (IoC) & Principe Ouvert/Fermé (OCP) avec Spring

**Auteur :** Ali Mahraoui  
**Technologies :** Java 11+, Spring Framework 5.3, Maven, JUnit 4

---

## 📌 Présentation du Projet

Ce projet illustre la mise en œuvre pratique de l'**Inversion de Contrôle (IoC)** et du **Principe Ouvert/Fermé (OCP - Open/Closed Principle)** en utilisant le framework **Spring**.

L'objectif principal est de permettre l'extension dynamique des sources de données (DAO) sans jamais avoir à modifier la logique métier existante (`MetierImpl`).

---

## 🏛️ Architecture & Conception

### 1. Couche Métier (`metier`)
* **`IMetier`** : Interface exposant la méthode de calcul `calcul()`.
* **`MetierImpl`** : Implémentation métier principale. Elle dépend d'une abstraction `IDao` injectée par constructeur et applique un coefficient multiplicateur ($\times 2.0$).
* **`MetierMultiple`** : Extension permettant d'agréger plusieurs implémentations DAO injectées sous forme de `List<IDao>` et de `Map<String, IDao>`.

### 2. Couche d'Accès aux Données (`dao`)
Quatre implémentations de l'interface `IDao` simulent différentes sources de données :

| Nom du Bean | Classe | Source simulée | Valeur brute | Résultat métier ($\times 2$) |
|---|---|---|---|---|
| `dao` | `DaoImpl` | Base de données (Production) | 100.0 | **200.0** |
| `dao2` | `DaoImpl2` | Base de données (Développement) | 150.0 | **300.0** |
| `daoFile` | `DaoFile` | Fichier texte local | 180.0 | **360.0** |
| `daoApi` | `DaoApi` | API REST externe | 220.0 | **440.0** |

### 3. Couche de Configuration (`config`)
La séparation des configurations évite les conflits d'ambiguïté de beans `@Primary` :
* **`AppConfig`** : Configuration centrale et scan des composants (`@ComponentScan`).
* **`DynamicDaoConfig`** : Résolution dynamique du bean DAO via `ApplicationContext.getBean(target)` sans structure conditionnelle `switch` (respect strict du principe OCP).
* **`ProfileDaoConfig`** : Activation par profils Spring (`primary`, `prod`, `dev`).
* **`DaoAliasConfig`** : Profil `alias` mappant vers le bean `daoApi`.

---

## ⚙️ Ordre de Priorité de la Configuration

Lorsque aucun profil n'est spécifié, la cible DAO est résolue dynamiquement selon la priorité suivante (du plus prioritaire au moins prioritaire) :

1. **Propriété Système Java** (`-Ddao.target=...`)
2. **Variable d'Environnement** (`DAO_TARGET=...`)
3. **Fichier de configuration** (`app.properties` $\rightarrow$ par défaut : `daoApi`)

> [!NOTE]
> La résolution s'effectue à l'initialisation du contexte Spring. Toute modification de configuration nécessite un redémarrage de l'application.

---

## 🚀 Compilation & Exécution

### Prérequis
* **JDK 11** ou supérieur
* **Apache Maven 3.6+**

### 1. Compilation et tests
```powershell
mvn clean verify
```

### 2. Exécution selon différents modes

#### A. Sélection dynamique par défaut (fichier `app.properties` : `daoApi`)
```powershell
java -jar target/Tp3-1.0-SNAPSHOT.jar
# Résultat attendu : 440.0
```

#### B. Sélection via propriété système Java
```powershell
java -Ddao.target=daoFile -jar target/Tp3-1.0-SNAPSHOT.jar
# Résultat attendu : 360.0
```

#### C. Sélection via variable d'environnement
```powershell
$env:DAO_TARGET="dao2"
java -jar target/Tp3-1.0-SNAPSHOT.jar
# Résultat attendu : 300.0
Remove-Item Env:DAO_TARGET
```

#### D. Exécution avec les profils Spring
```powershell
# Profil 'primary' (DaoImpl - Prod) -> 200.0
java -jar target/Tp3-1.0-SNAPSHOT.jar primary

# Profil 'prod' (DaoImpl - Prod) -> 200.0
java -jar target/Tp3-1.0-SNAPSHOT.jar prod

# Profil 'dev' (DaoImpl2 - Dev) -> 300.0
java -jar target/Tp3-1.0-SNAPSHOT.jar dev

# Profil 'alias' (DaoApi - API REST) -> 440.0
java -jar target/Tp3-1.0-SNAPSHOT.jar alias
```

---

## 🧪 Tests Unitaires (`OcpSelectionTest`)

Les tests automatisés JUnit vérifient la robustesse de l'architecture :
* **`toutesLesSources()`** : Valide l'injection correcte et le calcul attendu pour chacun des 4 DAOs.
* **`profils()`** : Contrôle le bon fonctionnement des profils (`primary`, `alias`, `dev`, `prod`).
* **`cibleInconnue()`** : Vérifie la levée d'une exception `IllegalArgumentException` si un nom de bean inexistant est passé.
* **`injectionMultiple()`** : Vérifie l'injection multiple dans `MetierMultiple` (total cumulé des 4 sources = **650.0**).
