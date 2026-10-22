# AURA Cosmétiques — Backend API Spring Boot 3 & Java 21

API REST complète pour l'application e-commerce **AURA Cosmétiques**.

---

## 🛠️ Stack Technique

- **Java 21 LTS**
- **Spring Boot 3.4.x** (Spring Web, Spring Data JPA, Hibernate, Validation)
- **Base de données** :
  - **MySQL 8** (profil `mysql` actif par défaut)
  - **PostgreSQL** (profil `postgres` prêt à l'emploi)
  - **H2 in-memory** (profil `h2` sans aucune installation nécessaire)
- **Initialisation automatique des données** (`DataInitializer.java`) avec rituels botaniques, catégories et commandes de test
- **Configuration CORS permissive** (`CorsConfig.java`) autorisant le web desktop et les smartphones sur le réseau local Wi-Fi

---

## 🚀 Lancement Rapide

### 1. Démarrer avec MySQL (Défaut)
Assurez-vous que votre service MySQL tourne sur le port `3306`. La base de données `cosmetics_db` sera automatiquement créée si elle n'existe pas.

```bash
cd grav_back
mvn spring-boot:run
```

Si le mot de passe de votre base MySQL locale est différent de `root`, modifiez simplement :
`src/main/resources/application-mysql.properties` :
```properties
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

---

### 2. Démarrer avec PostgreSQL
Pour utiliser PostgreSQL (port `5432`) :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

### 3. Démarrer avec H2 (Sans aucune configuration ni base externe)
Pour tester immédiatement sans MySQL ni PostgreSQL :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
Console H2 Web : `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:cosmetics_db`, Utilisateur: `sa`, Mot de passe vide).

---

## 📡 Endpoints REST (Base : `http://localhost:8080`)

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/products` | Liste de tous les soins cosmétiques |
| `POST` | `/api/products` | Créer un nouveau soin |
| `GET` | `/api/products/{id}` | Détail d'un soin |
| `PUT` | `/api/products/{id}` | Modifier un soin |
| `DELETE` | `/api/products/{id}` | Supprimer un soin |
| `GET` | `/api/categories` | Liste des catégories |
| `POST` | `/api/orders` | Passer une commande (Checkout) |
| `GET` | `/api/orders` | Liste de toutes les commandes (Admin) |
| `PUT` | `/api/orders/{id}/status` | Changer le statut d'une commande (`PAYEE`, `EXPEDIEE`, etc.) |
| `GET` | `/api/dashboard/stats` | Chiffre d'affaires, volume commandes et alertes stocks |
