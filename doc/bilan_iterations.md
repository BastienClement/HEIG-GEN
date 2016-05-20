#Bilan des Itérations#
##Bilan itération 1##
###Objectif###
Création de la base de données et mise en place d’une première communication simple entre client-serveur.
###Avancement###
La base de donnée est crée et le processus de déploiement du logiciel serveur est prêt.


Pas encore de communication client-serveur suite à un changement de plan après discussion avec Jonathan. Nous utiliserons une API REST pour la majorité des opérations.

Nous pensons conserver une interface Socket lorsque l’application est ouverte uniquement pour permettre des notifications push au client Android (au lieu d’utiliser les services GCM). Le protocol du socket est donc grandement simplifié et sera défini lors d’une futur itération.
###Bilans personnels (Temps prévus/ Temps consacré)###
####Antoine####
* Apprendre les bases du développement Scala
	* J’ai commencé à apprendre le langage Scala. Je vais devoir également étudier le framework Play que l’on va utiliser pour faire le serveur.
* Définition de la première version du protocole de communication
	* Nous avions d’abord prévu d’utiliser un connexion TCP ainsi qu’un protocole de communication binaire, mais après discussion avec l’assistant, nous allons mettre en place une communication REST en JSON et une communication TCP pour les notifications de type PUSH. Nous devons donc encore en parler avant de tout mettre en place.
	* J’ai également commencé à mettre en place une communication simple du côté client.
####Bastien####
* Aucun souci particulier à mentionner. La mise en place d’un hook GitHub pour automatiser le déployement du serveur permettra d’avoir facilement une version “stable” du serveur accessible à tout moment pour le développement de l’application Android.
* La communication côté serveur a été développée avec en tête un protocole entièrement basé sur un socket bidirectionnel. Après discussion, ces fonctionnalités ne seront pas utiles puisque nous pouvons développer une grande partie de l’application en utilisant une API REST, très simple à mettre en oeuvre avec Play.
####Guillaume (5h/6h)####
* Apprendre les bases du développement Android (3h)
Début de l’apprentissage d’Android. Création du projet client avec une première Activity. Plusieurs interrogations concernant la gestion des IO sur Android et la mise en place de l’API Rest à clarifier avec l’assistant.
* Mise en place de la communication côté client Pas encore de communication effective coté client (cf ci-dessus) Mise à jour du rapport (1h)
####Amel####
* Apprendre les bases du développement Android
	* Installation et configuration d’Android Studio, et d’un “device” pour tester et débugger les applications.
	* Prise en main de l’environnement de développement, de la structure des projets Android (ressources, contrôleurs en Java), etc. Un peu de peine à comprendre certaines notions, comme les fichiers de configuration “gradle” par exemple.
* Première ébauche de l’interface de connexion
	* Première version de l’activité Login, avec le placement de boutons, de champs textes et de boutons radio. Pas mal de recherche pour trouver les attributs nécessaires pour placer correctement les éléments (alignements, espaces entre les éléments, etc.).

##Bilan itération 2##
###Objectif###
Mise en place des fonctionnalités de création / suppression de compte et de connexion.
###Avancement###
###Bilans personnels (Temps prévus/ Temps consacré)###
####Antoine####
* Ajouter les fonctionnalités de gestion de compte et de connexion au protocole de communication
* Interfacer l’application serveur avec la base de données
	* J’ai créer la base de données et Bastien s’est occupé de l’interfacer avec la base de données.
####Bastien####
* Une gestion de compte relativement simple est disponible côté serveur. Les opérations de connexion et d’inscription sont disponibles.
####Guillaume (5h/5h)####
* Implémenter la gestion des comptes et de connexion au niveau du client
	* Envoi d’un register/login .
	* Pas encore de token retourné par le serveur
	* Replanification: Si possible discuter vendredi 20 mai avec Jonathan Bischof et Bastien Clément au sujet des IO pour déterminer le protocole à utiliser.
####Amel####
* Finaliser l’interface utilisateur pour la création de compte et la connexion
	* L’activité de Login a été complété avec quelques fonctionnalités :
		* l’affichage ou non du champ texte pour le nom du serveur prive ́est maintenant automatique, selon la sélection des boutons radio
		* un clic sur le bouton d’inscription lance maintenant l’activitéde *Subscription*
	* Création de l’activité de Subscription :
		* placement des différents éléments de l’interface
		* mise en place des évènements lors des saisies de texte, par exemple pour vérifier que les champs ne sont pas vides ou que les deux mots de passe sont identiques
		* début de réflexion concernant la validation du nom de l’utilisateur: il faut qu’on se mette d’accord sur le format (commence par une lettre, pas d’espaces, lettres autorisées?) et sur la vérification de doublons (contact avec le serveur pour interdire l’utilisation d’un username déjà existant par exemple)
* Commencer la rédaction du rapport final
	* Pas encore eu le temps de commencer


##Bilan itération 3##
###Objectif###
TODO
###Avancement###
TODO
###Bilans personnels (Temps prévus/ Temps consacré)###
TODO
####Antoine (TODO/TODO)####
* TODO
####Bastien (TODO/TODO)####
* TODO
####Guillaume (TODO/TODO)####
* TODO
####Amel (TODO/TODO)####
* TODO


##Bilan itération 4##
###Objectif###
TODO
###Avancement###
TODO
###Bilans personnels (Temps prévus/ Temps consacré)###
TODO
####Antoine (TODO/TODO)####
* TODO
####Bastien (TODO/TODO)####
* TODO
####Guillaume (TODO/TODO)####
* TODO
####Amel (TODO/TODO)####
* TODO
	


##Bilan itération 5##
###Objectif###
TODO
###Avancement###
TODO
###Bilans personnels (Temps prévus/ Temps consacré)###
TODO
####Antoine (TODO/TODO)####
* TODO
####Bastien (TODO/TODO)####
* TODO
####Guillaume (TODO/TODO)####
* TODO
####Amel (TODO/TODO)####
* TODO	


##Bilan itération 6##
###Objectif###
TODO
###Avancement###
TODO
###Bilans personnels (Temps prévus/ Temps consacré)###
TODO
####Antoine (TODO/TODO)####
* TODO
####Bastien (TODO/TODO)####
* TODO
####Guillaume (TODO/TODO)####
* TODO
####Amel (TODO/TODO)####
* TODO

