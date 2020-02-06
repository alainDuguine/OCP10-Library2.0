# Open Library Project

Ce repository est la mise en application du projet numéro 7 du parcours Java d'OpenClassrooms [Développez le nouveau système d’information de la bibliothèque d’une grande ville.](https://openclassrooms.com/fr/projects/124/assignment)

## Spécifications

Projet Spring-boot maven multi module contenant :

  * __Une Api Rest__ permettant la gestion d'une bibliothèque.
  * __Un client Web__ pour consulter les emprunts en cours ainsi que les livres disponibles.
  * __Un client Batch__ pour l'envoi de mails automatiques en cas d'emprunt en retard.
  
## Installation

  * Prérequis :
    * Java Jdk 8
    * Spring Boot 2.1.9
    * Spring Security
    * Apache Maven 3.6.1
    * PostgreSql 11
    * Apache Tomcat 9.0.26
    * Swagger 2.3.1
    * Thymeleaf

## Déploiement

  Création de la base de donnée :
  
  * Création de la base de données :
    * Créer un utilisateur username : "adm_library", password : "admin"
    * Créer une base de donnée "DB_LIBRARY" en utf-8
    * Exécuter le script "01_Schema.sql" (présent dans le dossier sql)
    * Exécuter le script "02_Data.sql" (présent dans le dossier sql)

  Variables d'environnement :
  
   Pour l'exécution via Intelli-j Ultimate plusieurs variables d'environnement sont nécessaires :
   
   * Pour le module Library-api :
    
   ```IP_SERVER``` : pour du local "localhost"    
   
   ```API_PORT``` : par exemple 8080
   
   ```POSTGRESQL_ADDON_URI```: url de la base de données (jdbc:postgresql://localhost:5432/DB_LIBRARY)
   
   ```POSTGRESQL_ADDON_USER```: utilisateur de la base de données (adm_library)
   
   ```POSTGRESQL_ADDON_PASSWORD```: mot de passe de l'utilisateur de la base de données (admin)
   
   * Pour le module Library-batch :
   
   ```API_URL``` : url de library-api par exemple http://localhost:8080/
   
   ```BATCH_USERNAME``` : nom d'utilisateur enregistré en tant qu'admin (admin@openlibrary.fr)
   
   ```BATCH_PASSWORD```: mot de passe (admin)
   
   ```BATCH_PORT```: par exemple 8082
   
   * Pour le module Library-webapp :
   
   ```API_URL``` : url de library-api par exemple http://localhost:8080/
   
   ```WEBAPP_PORT```: par exemple 9090
   
    
   
    
