# Open Library Project

Ce repository est la mise à jour du projet numéro 7 du parcours Java d'OpenClassrooms [Développez le nouveau système d’information de la bibliothèque d’une grande ville.](https://openclassrooms.com/fr/projects/124/assignment)

## Spécifications

Projet Spring-boot maven multi module contenant :

  * __Une Api Rest__ permettant la gestion d'une bibliothèque.
  * __Un client Web__ pour consulter les emprunts en cours ainsi que les livres disponibles.
  * __Un client Batch__ pour l'envoi de mails automatiques en cas d'emprunt en retard.
  
## Installation

  * Prérequis :
    * Java Jdk 8
    * Spring Boot 2.2.4
    * Spring Security
    * Apache Maven 3.6.1
    * PostgreSql 11
    * Apache Tomcat 9.0.26
    * Swagger 2.3.1
    * Thymeleaf

## Déploiement

 Déploiement de la base de données avec Docker:
 
  à partir du répertoire : ```/docker/dev/```
  exécutez la commande : ```docker-compose up```

  Variables d'environnement :
  
   Pour l'exécution via Maven plusieurs variables d'environnement sont nécessaires :
   
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
   
## Démarrage des applications :
   
   * Pour l'api rest - Library-api :
   
       exécuter la commande ```mvn spring-boot:run``` depuis ```/Library-api/library-api-service/```
       
   * Pour les batchs - Library-batch :
   
       exécuter la commande ```mvn spring-boot:run``` depuis ```/Library-batch/```
       
   * Pour la webapp - Library-webapp :
   
       exécuter la commande ```mvn spring-boot:run``` depuis ```/Library-webapp/```
       
 *Les commandes ```mvn``` peuvent être remplacées par le wrapper Maven ```mvnw```*
