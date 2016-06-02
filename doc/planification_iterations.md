#Plan des Itérations#
##Plan itération 1##

###Objectif général###
Création de la base de données et mise en place d’une première communication simple entre client-serveur
###Objectifs détaillés###
* Apprendre les bases du développement Scala et Android (Gestion)
* Mettre en place la base de données (Développement de l’infrastructure)
* Définir une première version du protocole de communication (Gestion : Conception)
* Réaliser une première communication entre le client et le serveur (Développement de l’infrastructure)
Cet objectif est un pré-requis de tous les cas d’utilisation. A l’issue de l’itération, il sera possible de vérifier qu’un message simple a bien été transmis du client au serveur.

###Durée###
2 semaines
###Date de début###
vendredi 22 avril
###Date de fin###
lundi 9 mai
###Partage du travail, les heures sont indiquées par semaine###
####Antoine####
* Apprendre les bases du développement Scala 3h o Définition de la première version du protocole de communication 2h

####Bastien####
* Création de la base de données 2h o Mise en place de la communication côté serveur 2h

####Guillaume####
* Apprendre les bases du développement Android 3h o Mise en place de la communication côté client 2h

####Amel####
* Apprendre les bases du développement Android 3h o Première ébauche de l’interface de connexion 2h

###Temp consacré###
environ 19 heures par semaines (38 heures au total)

##Plan itération 2##
###Objectif général###
Mise en place des fonctionnalités de création / suppression de compte et de connexion
###Objectifs détaillés###
* Ajouter les fonctionnalités de gestion de compte et de connexion au protocole de communication (Gestion : Conception)
* Interfacer l’application serveur avec la base de données (Développement de l’infrastructure)
* Implémenter la gestion des comptes et de connexion au niveau du serveur et du client (Développement des fonctionnalités)
* Cas d’utilisation réalisés complètement : Création de compte, Connexion à l’application
* Cas d’utilisation réalisés partiellement : Supprimer son compte, Se déconnecter
	* Ces fonctionnalités seront implémentées au niveau du code, mais leur intégration à l’interface utilisateur se fera lors des itérations suivantes (itérations 3 et 5).
	*Il sera néamoins possible de voir qu’une commande de déconnection est recue coté serveur.
	*Lors d’une suppression de compte, il sera possible de voir qu’une commande de suppression est recue coté serveur et que les informations du compte sont effectivement supprimées de la base de données.
* Commencer la rédaction du rapport final, avec la structure des chapitres (Gestion : Rédaction).

###Durée###
1 semaine
###Date de début###
mardi 10 mai 
###Date de fin###
lundi 16 mai 

###Partage du travail, les heures sont indiquées par semaine###

####Antoine####
* Ajouter les fonctionnalités de gestion de compte et de connexion au
protocole de communication 3h
* Interfacer l’application serveur avec la base de données 2h

####Bastien####
* Implémenter la gestion des comptes et de connexion au niveau du serveur 5h

####Guillaume####
* Implémenter la gestion des comptes et de connexion au niveau du client 5h

####Amel####
* Finaliser l’interface utilisateur pour la création de compte et la connexion 3h 
* Commencer la rédaction du rapport final 2h

###Temp consacré###
environ 20 heures (5 heures par personne)

##Plan itération 3##
###Objectif général###
Mise en place des fonctionnalités de recherche et de gestion de contacts
###Objectifs détaillés###
* Ajouter les fonctionnalités de recherche et de gestion de contacts au protocole de communication (Gestion : Conception)
* Implémenter la recherche et de gestion de contact au niveau du serveur et du client, ainsi que les interfaces utilisateur correspondantes (Développement des fonctionnalités)
* Cas d’utilisation réalisés complètement : Gérer les contacts
* Cas d’utilisation réalisés partiellement : Démarrer une discussion privée, Ajouter
un utilisateur dans un groupe 
* La fonctionnalité de recherche est utilisée dans plusieurs cas d’utilisation. La réalisation complète de ces cas d’utilisation se fera lors des itérations suivantes.
* Continuer la rédaction du rapport final (Gestion : Rédaction).

###Durée###
1 semaine
###Date de début###
mardi 17 mai
###Date de fin###
lundi 23 mai

###Partage du travail, les heures sont indiquées par semaine###

####Antoine####
* Ajouter les fonctionnalités de recherche et de gestion de contact au protocole de communication. 3h
* Implémenter la recherche et de gestion de contact au niveau du client. 2h

####Bastien####
* Implémenter la recherche et de gestion de contact au niveau du serveur

####Guillaume####
* Implémenter la gestion des comptes et de connexion au niveau du client o Implémenter la recherche et la gestion de contact au niveau du client
* Ajout de l’interface de recherche de contact

####Amel####
* Ajout de l’interface de gestion de contact
* Ajout de l’interface de recherche de contact

###Temp consacré###
environ 20 heures (5 heures par personne)

##Plan itération 4##
###Objectif général###
Mise en place des discussions privées 
###Objectifs détaillés###
* Ajouter les fonctionnalités de création de discussion privée, d’envoi de messages et d’historique au protocole de communication (Gestion : Conception)
* Implémenter la création / suppression de discussion, l’envoi de messages et l’affichage de l’historique au niveau du serveur et du client, ainsi que les interfaces utilisateur correspondantes (Développement des fonctionnalités)
* Cas d’utilisation réalisés complètement : Démarrer une discussion privée, Envoyer un message, Consulter les messages, Charger les messages de l’historique
* Cas d’utilisation réalisés partiellement : Démarrer une discussion de groupe
* Une discussion pde groupe est une extension d’une discussion privée, donc certaines fonctionnalités seront identiques. La réalisation complète de ce cas d’utilisation se fera lors des itérations suivantes.
* Continuer la rédaction du rapport final (Gestion : Rédaction).

###Durée###
1 semaine
###Date de début###
mardi 24 mai 
###Date de fin###
lundi 30 mai 
###Partage du travail, les heures sont indiquées par semaine###

####Antoine####
* Ajouter les fonctionnalités de création de discussion privée, d’envoi de
messages et d’historique au protocole de communication. 2h
* Implémenter la gestion de l’historique de discussion au niveau du serveur. 3h

####Bastien####
* Implémenter la création / suppression de discussion, l’envoi de messages au
niveau du serveur

####Guillaume####
* Implémenter la création / suppression de discussion, l’envoi de messages et l’affichage de l’historique au niveau du client
* Continuer la rédaction du rapport

####Amel####
* Ajout de l’interface de création et d’affichage de discussion.

###Temp consacré###
environ 20 heures (5 heures par personne)

##Plan itération 5##
###Objectif général###
Création de discussion publique (discussion de groupe) 
###Objectifs détaillés###
* Ajouter les fonctionnalités de discussion publique (créer, supprimer, rejoindre), et de gestion des membres (ajouter, supprimer, promouvoir administrateur de groupe) d’une discussion publique au protocole de communication (Gestion : Conception)
* Implémenter toutes les fonctionnalités relatives aux discussions publiques au niveau du serveur et du client, ainsi que les interfaces utilisateur correspondantes (Développement des fonctionnalités)
* Cas d’utilisation réalisés complètement : Rejoindre une discussion de groupe, Démarrer une discussion de groupe, Bannir un utilisateur d’un groupe de discussion public, Quitter un groupe, Ajouter un utilisateur dans un groupe
* Continuer la rédaction du rapport final (Gestion : Rédaction).

###Durée###
1 semaines
###Date de début###
mardi 31 mai 
###Date de fin###
lundi 6 juin 
###Partage du travail, les heures sont indiquées par semaine###

####Antoine####
* Ajouter toutes les fonctionnalités relatives aux discussions publiques au
protocole de communication. 3h
* Aider Bastien et Guillaume pour implémenter les fonctionnalités au niveau serveur ou client. 2h

####Bastien####
* Implémenter toutes les fonctionnalités relatives aux discussions publiques au
niveau du serveur

####Guillaume####
* Implémenter toutes les fonctionnalités relatives aux discussions publiques au niveau du client
* Extension de l’interface d’affichage de discussion privée pour gérer les discussions publiques

####Amel####
* Ajout de l’interface de gestion de groupes

###Temp consacré###
environ 20 heures (5 heures par personne)

##Plan itération 6##
###Objectif général###
Signalement et blocage 
###Objectifs détaillés###
* Ajouter les fonctionnalités de signalement de message et de blocage d’utilisateur au protocole de communication (Gestion : Conception)
* Implémenter toutes les fonctionnalités relatives au signalement de message et de blocage d’utilisateur au niveau du serveur et du client, ainsi que les interfaces utilisateur correspondantes (Développement des fonctionnalités)
* Cas d’utilisation réalisés complètement : Consulter les signalements d’un groupe public, Consulter les signalements d’une discussion priveé, Signale un utilisateur
* Terminer la première version du rapport final (Gestion : Rédaction).

###Durée###
1 semaines
###Date de début###
mardi 7 juin
###Date de fin###
lundi 13 juin
###Partage du travail, les heures sont indiquées par semaine###

####Antoine####
* Ajouter les fonctionnalités de signalement de message et de blocage d’utilisateur au protocole de communication. 3h
* Terminer la première version du rapport final. 2h

####Bastien####
* Implémenter toutes les fonctionnalités relatives au signalement de message et de blocage d’utilisateur au niveau du serveur
* Finaliser l’application serveur

####Guillaume####
* Implémenter toutes les fonctionnalités relatives au signalement de message et de blocage d’utilisateur au niveau du client
* Finaliser l’application cliente et les différentes interfaces

####Amel####
* Ajouter les options de signalement et de blocage aux différentes interfaces utilisateur
* Finaliser les différentes interfaces.

###Temp consacré###
environ 20 heures (5 heures par personne)


