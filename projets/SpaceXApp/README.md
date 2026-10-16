# SpaceX App

![logo](/projets/SpaceXApp/docs/logo.png)

SpaceX App est une application Android qui permet de découvrir les lancements SpaceX et leurs informations techniques. Les données proviennent de l’API publique SpaceX, puis sont conservées localement pour rester consultables hors connexion.

L’application a été développée en Kotlin avec Jetpack Compose. Elle met en pratique une architecture organisée autour de l’interface, du ViewModel, de cas d’utilisation et d’un dépôt qui coordonne les données distantes et locales.


Pour consulter la documentation technique de l'app de plus en détails, vous pouvez lire ``Documentation_technique.pdf`` [Documentation_technique](/projets/SpaceXApp/Documentation_technique.pdf)

## Fonctionnalités

- **Lancements** : parcourir les missions et consulter leurs détails, dont le statut, les informations de vol, les boosters, les charges utiles et les incidents lorsqu’ils sont disponibles.

![lancements](/projets/SpaceXApp/docs/home.png)

- **Médias et liens** : afficher les images de mission et ouvrir les ressources associées, comme YouTube ou Wikipédia.

![détails2](/projets/SpaceXApp/docs/details2.png)

- **Recherche et filtres** : rechercher des lancements, filtrer par statut et trier par date.

- **Favoris** : enregistrer des missions, les retrouver dans un écran dédié et importer ou exporter les favoris au format JSON.

![favoris](/projets/SpaceXApp/docs/favorites.png)

- **Mode hors ligne** : consulter les données mises en cache avec Room. La connexion est surveillée et les données peuvent être actualisées au retour du réseau.

- **Corbeille** : mettre des lancements à la corbeille et les restaurer. Les éléments supprimés ne réapparaissent pas lors d’une synchronisation avec l’API.

![corbeille](/projets/SpaceXApp/docs/trash.png)

- **Actions rapides** : copier ou partager les informations d’une mission.

![détails](/projets/SpaceXApp/docs/details.png)

- **Interface adaptative** : navigation entre les écrans, thèmes clair et sombre et affichage en grille en mode paysage.

![bar](/projets/SpaceXApp/docs/bar.png)

## Technologies

- **Kotlin** et **Coroutines / Flow** pour la logique asynchrone et les flux de données.
- **Jetpack Compose** et **Material 3** pour l’interface.
- **ViewModel** et **Navigation Compose** pour la gestion de l’état et des écrans.
- **Retrofit** et **Moshi** pour les requêtes vers l’API SpaceX et le traitement des données JSON.
- **Room** pour la base de données locale et **DataStore** pour les préférences utilisateur.
- **Coil** pour le chargement des images.
- **Gradle** pour la configuration et la compilation du projet.

## Prérequis

- Android Studio.
- JDK 17 ou version ultérieure.
- Android SDK 36.
- Un émulateur ou un appareil Android 8.0 (API 26) ou ultérieur.

## Installation et lancement

1. Ouvrir le dossier `SpaceXApp` dans Android Studio et laisser Gradle
   synchroniser le projet.
2. Sélectionner un appareil ou un émulateur Android.
3. Lancer la configuration **app** avec **Run**.

Pour construire l’APK de débogage sous Windows, depuis le dossier du projet :

```powershell
.\gradlew.bat assembleDebug
```

L’APK est généré dans `app/build/outputs/apk/debug/app-debug.apk`.

## Organisation du code

Les sources de l’application se trouvent dans
`app/src/main/java/com/example/spacexapp/` :

```text
com/example/spacexapp/
├── data/        # Base Room, entités, préférences et mappers
├── domain/      # Cas d’utilisation : filtrage et gestion JSON des favoris
├── model/       # Modèles de lancement
├── network/     # API SpaceX et suivi de la connectivité
├── room/        # DAO et dépôt de données
├── ui/          # Écrans Compose, navigation et thème
├── util/        # Sérialisation et actions de partage/copie
└── viewmodel/   # État et actions de l’interface
```

## API

L’application utilise l’API publique SpaceX pour récupérer les informations sur les lancements. Une connexion Internet est nécessaire pour obtenir les données les plus récentes ; les lancements déjà enregistrés restent accessibles hors ligne.
