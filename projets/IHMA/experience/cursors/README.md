# IIHM_Bouchra_Bouamama

## Auteur

Bouchra BOUAMAMA

## Description
Ce projet est une étude pratique sur les techniques d'interaction en IHM. L'objectif est de comparer l'efficacité du pointage classique par rapport au **Bubble Cursor** qui adapte dynamiquement la zone de sélection du curseur pour capturer la cible la plus proche.

## Fonctionnalités
Le projet implémente trois types de curseurs :
* **Normal Cursor** : Sélection classique. L'utilisateur doit cliquer précisément à l'intérieur de la cible (collision standard).
* **Bubble Cursor** : Une bulle semi-transparente entoure le curseur et s'étend jusqu'au bord de la cible la plus proche.
* **Rope Cursor** : Une variante visuelle du Bubble Cursor où un segment relie le curseur au centre de la cible sélectionnée.

## Architecture du Code
Le projet repose sur le framework **PyQt5** et suit une structure orientée objet :
* `Target.py` : Gère les propriétés des cibles (position, taille, états de couleur).
* `BubbleCursor.py` & `NormalCursor.py` : Contiennent les algorithmes de calcul de distance et de détection de collision.
* `BubbleWidget.py` & `NormalWidget.py` : Gèrent l'affichage graphique, le chargement des données CSV et le chronométrage des performances.
* `RopeCursor.py` : Utilise l'héritage pour modifier le rendu visuel sans changer la logique métier.


## Utilisation

Pour utiliser ce projet, exécutez le fichier principal :

````
iihm_bouchra_bouamama> python3 MainBubble.py
````