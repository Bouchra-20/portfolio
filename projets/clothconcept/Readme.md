# ClotheConcep


![Accueil](/projets/clothconcept/docs/Accueil.png)

---

# Table des matières

1. [Introduction](#1-introduction)
2. [État de l'art](#2-état-de-lart)
3. [Description de l'application](#3-description-de-lapplication)
4. [Conception](#4-conception)
5. [Outils utilisés et justification](#5-outils-utilisés-et-justification)
6. [Mise en place](#6-mise-en-place)
7. [Planning et méthodologie](#7-planning-et-méthodologie)
8. [Conclusion](#8-conclusion)
9. [Défis rencontrés et améliorations futures](#9-défis-rencontrés-et-améliorations-futures)

# 1. Introduction

## Contexte du projet

L'industrie de la mode connaît une transformation digitale majeure. Les consommateurs modernes, de plus en plus exigeants, recherchent des produits personnalisés qui reflètent leur identité unique. Pourtant, le processus de conception de vêtements reste majoritairement réservé aux professionnels du secteur (stylistes, modélistes, couturiers), créant une barrière significative entre l'utilisateur final et la création de ses propres vêtements.

Dans le cadre de mon projet de fin d'études en Master 1 E-Services, j'ai développé **ClotheConcep**, une application web innovante qui démocratise la conception de vêtements en permettant à tout utilisateur de créer sa propre pièce sur mesure, de visualiser le résultat en temps réel grâce à l'intelligence artificielle générative.

# 2. État de l'art
## Problématique
Dans le monde du prêt-à-porter, les vêtements disponibles sur le marché répondent aux préférences et standards de chaque marque, laissant peu de place à la personnalisation. L'option de concevoir ses propres créations reste limitée aux professionnels et stylistes du domaine, tandis que l'utilisateur lambda n'a pas la possibilité de concrétiser ses propres combinaisons et imaginations.

Les outils existants se divisent en deux catégories :
1. **Outils professionnels de CAO** (Conception Assistée par Ordinateur) : très précis mais réservés aux experts
2. **Configurateurs e-commerce basiques** : simples mais limités en termes de personnalisation et de rendu visuel

Il existe donc un véritable fossé entre ces deux mondes, et aucune solution grand public ne permet de concevoir un vêtement avec un rendu photo-réaliste instantané.

## Solution proposée
ClotheConcep comble ce vide en offrant une plateforme qui permet aux utilisateurs simples comme aux professionnels de créer leur propre pièce de vêtement selon leurs goûts, et de visualiser le résultat de leur création sous une forme réaliste grâce à l'IA, avant même de la produire physiquement.

Pour les stylistes, cela représente un outil de prototypage rapide et économique. Pour les utilisateurs finaux, c'est une expérience créative immersive. À terme, cette plateforme pourra être liée à une boutique en ligne permettant aux utilisateurs de concevoir leur pièce, la commander, et la faire coudre par un tailleur partenaire qui recevra toutes les informations techniques nécessaires à la fabrication.

## Analyse de marché
Pour valider la pertinence et l'originalité du projet, j'ai réalisé une étude comparative des solutions de conception de mode et de patronage assistée par ordinateur déjà existantes sur le marché.

### Analyse comparative : L'approche Tailornova
**Tailornova** est l'une des plateformes web les plus avancées en matière de création de patrons 3D et de customisation de vêtements en ligne. Elle repose sur un pipeline de conception géométrique paramétrique :

**Le fonctionnement** : L'utilisateur ajuste un mannequin virtuel (avatar) à ses mensurations, sélectionne des options de design prédéfinies (cols, manches, jupes), et l'algorithme recalcule mathématiquement les lignes du patron de couture en 2D tout en affichant une prévisualisation 3D du vêtement texturé.

**Les limites constatées** : Bien que Tailornova soit un outil métier redoutable pour la précision technique du patronage, son système de rendu visuel repose sur un moteur d'affichage 3D classique (WebGL/Canvas) qui est plus dirigé vers les professionnels que vers les utilisateurs normaux. Le rendu final des tissus et des textures manque cruellement de réalisme photographique. De plus, l'utilisateur est strictement limité aux combinaisons de modèles codées en dur par les développeurs de la plateforme ; il est impossible de générer un style ou une texture qui n'a pas été modélisée au préalable en 3D. Enfin, Tailornova offre une expérience gratuite limitée, les options complètes de la plateforme étant payantes.

### Tableau comparatif des solutions

| Critères | Applications CAO Classiques (Tailornova) | Configurateurs E-commerce (Nike By You) | Mon Application (React + FastAPI + Stable Diffusion) |
|----------|------------------------------------------|----------------------------------------|------------------------------------------------------|
| **Technique de rendu** | Rendu 3D vectoriel / WebGL standard (aspect "jeu vidéo") | Rendu 2D avec textures pré-calculées | Inférence par Diffusion (rendu photo-réaliste de qualité studio) |
| **Gestion des textiles** | Application d'une texture 2D répétitive sur un maillage 3D | Textures pré-définies, pas de personnalisation | Compréhension contextuelle de la matière (plis, reflets, drapé naturel) |
| **Infrastructure & Coût** | Modèle SaaS payant, calcul lourd sur serveurs cloud propriétaires | Infrastructure e-commerce classique | Architecture locale/hybride, inférence à petit prix via API cloud (10$/1000 credits) |
| **Temps de génération** | Instantané (rendu 3D temps réel) | Instantané (textures pré-calculées) | ~30 secondes (pipeline 2 étapes via API Stability AI) |
| **Cible métier** | Modélistes techniques (génération de fichiers .PDF/.DXF) | Grand public (personnalisation limitée) | Aide à la décision créative, prototypage rapide, e-commerce |
| **Personnalisation couleur** | Limitée aux palettes prédéfinies | Palette restreinte | Color picker libre (hexadécimal) |
| **Historique des créations** | Oui (abonné payant) | Oui (compte utilisateur) | Oui (base PostgreSQL locale) |

---

# 3. Description de l'application
## Vue d'ensemble
ClotheConcep est une application web de conception de vêtements personnalisés qui permet aux utilisateurs de créer leur propre pièce sur mesure et de visualiser le résultat en temps réel grâce à l'intelligence artificielle générative.

## Fonctionnalités principales
### 1. Sélection du vêtement de base
L'utilisateur commence par choisir le type de vêtement qu'il souhaite concevoir parmi 6 catégories, la sélection est filtrée dynamiquement selon le genre choisi (WOMENSWEAR, MENSWEAR, GIRLSWEAR, BOYSWEAR, JUNIOR_GIRLS, JUNIOR_BOYS).
### 2. Personnalisation du design
Une fois le vêtement sélectionné, l'utilisateur accède au studio de personnalisation avec 4 axes de modification :
**Silhouette** : Choix de la forme globale du vêtement
**Encolure** : Style du col
**Manches** : Type et longueur des manches
**Taille** : Position de la taille
Chaque modification met à jour instantanément le croquis technique central grâce à un système intelligent de combinaisons d'images avec fallback progressif.
### 3. Sélection du tissu et de la couleur
L'utilisateur choisit parmi les types de tissus :
- **Coton Uni** : Tissu uni avec color picker libre (hexadécimal)
- **Jeans Denim** : Texture denim avec couleur verrouillée
- **Lin Sauvage** : Texture lin naturel avec couleur verrouillée
- **Cuir Souple** : Texture cuir avec couleur verrouillée

*(D'autres tissus peuvent être ajoutés via la base de données)*

Pour les tissus unis, un color picker dynamique permet de choisir n'importe quelle couleur (format hexadécimal).
### 4. Génération IA photo-réaliste
Au clic sur "Générer avec l'IA ", l'application :
1. Envoie tous les choix au backend via une API REST
2. Le backend construit un prompt optimisé en anglais
3. Appel à l'API Stability AI (modèle Stable Diffusion XL)
4. Génération d'une image photo-réaliste en quelques secondes
5. Sauvegarde automatique sur disque et en base de données
6. Affichage du résultat dans le panneau droit
### 5. Téléchargement de la création
Le bouton "Enregistrer le Look" permet de télécharger l'image générée au format PNG avec un nom de fichier horodaté.

## Interface utilisateur
L'interface suit un design moderne et épuré avec :
- **Header** : Navigation et bouton de génération
- **Sidebar gauche** : Onglets de personnalisation
- **Zone centrale** : Croquis technique en temps réel
- **Zone droite** : Rendu photo-réaliste IA
- **Panneau tissu/couleur** : Sélection matière et couleur
- **Boutons de scroll** : Navigation verticale fluide
Le design utilise Tailwind CSS avec une palette de couleurs rose/gray pour une expérience utilisateur cohérente et professionnelle.

## Architecture technique
```
Frontend (React + TypeScript + Vite)
    ↓ HTTP POST JSON
Backend (FastAPI + Python)
    ↓ Étape 1 : control/sketch
Stability AI (Croquis → Silhouette)
    ↓ Étape 2 : control/structure
Stability AI (Silhouette + Tissu → Image finale)
    ↓
PostgreSQL (Persistance)
```
---

# 4. Conception
## Diagramme de classes

![Diagramme_classes](/projets/clothconcept/docs/diagram_class.png)

Le système s'articule autour de trois modèles principaux :
### Modèle Frontend : `DesignerState`
Représente l'état complet de l'interface utilisateur à un instant donné. Il contient :
- Le genre sélectionné (WOMENSWEAR, MENSWEAR, etc.)
- Le template de vêtement choisi
- L'onglet actif dans le panneau de personnalisation
- L'objet `customization` regroupant tous les choix de design
### Modèle Backend : `DesignSelection` (Pydantic)
DTO (Data Transfer Object) qui valide et structure les données envoyées par le frontend vers le backend. Il assure la cohérence des types et fournit des valeurs par défaut pour les champs optionnels.
### Modèle de persistance : `GeneratedDesignModel` (SQLAlchemy)
Représente une ligne dans la table `generated_designs` de PostgreSQL. Chaque génération d'image crée un nouvel enregistrement avec toutes les métadonnées associées (choix de design, prompt utilisé, chemin de l'image, timestamp).

## Diagrammes de séquence
### Séquence principale : Génération d'image

![Diagramme_sequence_génération_image](/projets/clothconcept/docs/diagram_seq_prinp_gener_img.png)

Ce diagramme illustre le pipeline complet : depuis la sélection utilisateur jusqu'à la réception de l'image finale, en passant par les deux appels successifs à l'API Stability AI (`control/sketch` puis `control/structure`).

## Diagramme de flux utilisateur

![Diagramme_flux_utilisateur](/projets/clothconcept/docs/diagram_activite.png)

Le flux utilisateur complet couvre le parcours depuis l'arrivée sur la plateforme jusqu'au téléchargement de la création finale, en passant par toutes les étapes de personnalisation et de génération.

---

# 5. Outils utilisés et justification
## Synthèse des outils

| Catégorie | Outil | Rôle | Justification |
|-----------|-------|------|---------------|
| **Langages** | TypeScript 5.x | Frontend | Typage statique, maintenance |
| | Python 3.11 | Backend + IA | Écosystème IA riche |
| | SQL | BDD | Standard relationnel |
| **Frontend** | React 18 | UI | Composants réutilisables, hooks |
| | Vite 5 | Build | Démarrage rapide, HMR |
| | Tailwind CSS 3 | Styling | Utility-first, développement rapide |
| **Backend** | FastAPI 0.109 | API | Async, validation auto, Swagger |
| | SQLAlchemy 2 | ORM | Abstraction SQL, multi-SGBD |
| | Pydantic 2 | Validation | Intégration native FastAPI |
| **IA** | Stability AI API | Génération | Pipeline 2 étapes (sketch + structure), 6-10s |
| | Pillow | Traitement images | Normalisation, conversion |
| **BDD** | PostgreSQL 16 | SGBD | Robustesse, performance, gratuit |
| | DBeaver 24 | Admin BDD | Interface graphique universelle |
| **Dev** | VS Code, Git, npm, pip, venv | Développement | Standard industrie |

## Justification des choix
**React** : Écosystème mature, TypeScript natif, hooks pour gestion d'état complexe du wizard de personnalisation.
**FastAPI** : Combine simplicité Flask + fonctionnalités modernes (async, validation Pydantic auto, documentation Swagger). Élimine la validation manuelle des entrées.
**PostgreSQL** : Meilleur compromis performance/fonctionnalités/robustesse. Support des séquences essentiel pour l'auto-incrément de `generated_designs`.
**Stability AI API** : Après tests de Stable Diffusion local (4 min/image CPU, incompatibilité SD Turbo), l'API cloud offre rapidité (~30s via pipeline 2 étapes), qualité SDXL, et intégration simple. Pipeline séquentiel `control/sketch` → `control/structure` pour maîtrise totale structure + texture.
**PostgreSQL vs alternatives** : Supérieur à MySQL (fonctionnalités avancées JSON), MongoDB (conformité SQL requise), SQLite (scalabilité limitée pour production).
**Stability AI vs alternatives** : Supérieur à SD local (performance CPU), DALL-E 3 (coût élevé), Midjourney (pas d'API REST, Discord uniquement). Pipeline 2 étapes permet contrôle fin sur structure ET texture séparément.

---

# 6. Mise en place

## Documentation du développement

### Étape 1 : Génération de la bibliothèque de patrons

J'ai commencé par générer ma propre bibliothèque de patrons à l'aide de l'IA. Cette étape a nécessité la génération de plusieurs styles de patrons avec différentes combinaisons (cols, manches, silhouettes). Le défi majeur était de maintenir une cohérence visuelle entre tous les patrons générés (mêmes dimensions, même style de trait, même angle de vue).

### Étape 2 : Établissement de l'interface et du Workspace Scindé (Split Workspace)

J'ai utilisé React et TypeScript pour concevoir l'interface globale. J'ai développé la première page de l'application où l'utilisateur peut sélectionner le type de pièce (le vêtement de base) qu'il veut construire.
J'ai mis en place une interface divisée en deux zones distinctes, avec une barre latérale pour naviguer entre les différentes catégories d'options (Silhouette, Encolure, Manches, Taille).
À l'intérieur de l'espace de travail scindé, la zone de gauche affiche instantanément le croquis technique de base au premier coup (dès la sélection de la robe, sans attendre les clics de l'utilisateur). La zone de droite est un espace dédié qui accueille le rendu photo-réaliste final.

### Étape 3 : Fusion Métier du Panneau "Textile et Couleur"

J'ai rassemblé la gestion de la matière et de la couleur au sein d'un seul et même composant intelligent. Si l'utilisateur choisit un textile uni (Coton), un sélecteur de couleur (Color Picker) apparaît dynamiquement pour lui permettre de teinter le tissu sur mesure.
Si l'utilisateur choisit une matière texturée fixe (comme le Jeans Denim ou le Cuir), la couleur se verrouille automatiquement sur sa teinte naturelle pour simplifier l'expérience.

### Étape 4 : Moteur de Clés Dynamiques et Connexion Locale avec Stable Diffusion

J'ai développé un algorithme capable de générer des clés de combinaisons à taille variable (combinant 2 ou 3 critères de coupe) pour faire correspondre exactement les choix de l'utilisateur avec la bibliothèque d'images.
J'ai créé un serveur intermédiaire en Python (FastAPI) qui réceptionne ces choix, fabrique un prompt textuel ultra-précis, et communique en direct avec l'API d'AUTOMATIC1111 (Stable Diffusion) installé en local sur ma machine. La génération se fait ainsi sur ma propre carte graphique, de manière 100% gratuite et privée.

### Étape 5 : Résolution des problèmes de configuration Technique et Dépendances de l'IA

Pour rendre le moteur de génération opérationnel en local, j'ai déployé l'interface AUTOMATIC1111 au sein d'un environnement virtuel Python 3.11. Cette étape a nécessité la résolution de plusieurs conflits critiques liés à l'évolution de l'écosystème open-source de Stable Diffusion.

### Étape 6 : Évolution du modèle IA (text2img -> img2img -> API Cloud)

J'ai utilisé initialement le modèle texte-to-image de Stable Diffusion qui se focalise sur la préparation d'un prompt détaillé avec toutes les informations sur la pièce. La cause derrière ce choix était la simplicité de son utilisation en local ainsi que la gratuité totale du modèle.
Cependant, j'ai constaté que l'image générée ne respectait pas le patron choisi par l'utilisateur, car un prompt ne permet pas de décrire précisément et exactement la pièce en question. J'ai donc dû switcher vers le modèle image-to-image (img2img).
L'utilisation de img2img a nécessité plusieurs tests sur les paramètres de configuration pour finalement tomber sur les bons paramètres qui allaient générer l'image souhaitée (notamment le `denoising_strength` et le `cfg_scale`).

### Étape 7 : Optimisation des performances

J'ai rencontré un problème de temps de génération qui était trop long (4 minutes par image sur CPU). J'ai trouvé qu'il existait une solution pour accélérer la génération d'image par l'utilisation du modèle SD Turbo. Au début, cette solution a marché, mais après un certain temps, une erreur de compatibilité avec le setup CPU que j'utilise sur mon PC est apparue (`RuntimeError: "addmm_impl_cpu_" not implemented for 'Half'`).
Le travail d'une semaine sur le côté IA s'est donc retrouvé à zéro et j'ai été forcée d'utiliser une API cloud.

### Étape 8 : Migration vers l'API Stability AI

J'ai commencé à utiliser l'API de Stable Diffusion sur **Stability AI**, configuré mon code backend en conséquence, ce qui m'a permis de générer une image en seulement 15 secondes maximum. L'intégration a nécessité :
- La création d'un compte et l'obtention d'une clé API
- La configuration du fichier `.env` avec `python-dotenv`
- L'adaptation du code pour utiliser l'endpoint `/v2beta/stable-image/control/sketch`

### Étape 9 : Pipeline de génération en 2 étapes (Architecture finale)

L'approche initiale consistait à envoyer une image composite (croquis + texture superposée) à un seul appel API. Cependant, cette méthode présentait plusieurs limites :
- La texture était mal interprétée par l'IA (couleurs altérées, motifs déformés)
- Le contrôle de la structure du vêtement était insuffisant
- Les résultats étaient incohérents d'une génération à l'autre

J'ai donc conçu un pipeline en 2 étapes exploitant deux endpoints spécialisés de Stability AI :
**Étape 1** — `control/sketch` : Transformation du croquis technique en silhouette réaliste
- Génération d'une première image avec la bonne structure globale du vêtement
- Durée : ~13-14 secondes
**Étape 2** — `control/structure` : Application de la texture du tissu sur la silhouette
- Utilise l'image de l'étape 1 comme entrée
- Préservation de la forme tout en appliquant fidèlement la texture et la couleur du tissu
- Durée : ~13-15 secondes

Cette approche séquentielle permet une maîtrise totale du processus créatif : la structure du vêtement est d'abord figée, puis le tissu est appliqué sans déformer le design initial. Le résultat est une image photo-réaliste qui respecte à la fois les choix de coupe de l'utilisateur et les caractéristiques du tissu sélectionné.

### Étape 10 : Base de données PostgreSQL

J'ai créé ma base de données PostgreSQL pour ma plateforme, et configuré mon code pour pouvoir enregistrer dans la table chaque image générée et les informations associées. L'implémentation a nécessité :
- L'installation de PostgreSQL 16
- La configuration de la connexion dans DBeaver
- La création du modèle SQLAlchemy `GeneratedDesignModel`
- La gestion des transactions (commit/rollback)
- La résolution du problème d'auto-incrément avec `Sequence`

### Étape 11 : Système de gestion des tissus

Pour garantir une description précise et cohérente des tissus, j'ai mis en place une base de données dédiée (`fabrics`) qui stocke :
- Les caractéristiques de chaque tissu, le chemin vers l'image de texture haute résolution, la couleur par défaut et l'autorisation de personnalisation
Un service dédié (`FabricService`) a été développé pour récupérer les informations du tissu sélectionné et servir les images de texture via une API REST dédiée
Cette approche permet d'ajouter de nouveaux tissus sans modifier le code, simplement en insérant la photo et une ligne dans la base de données.

### Étape 12 : Fonctionnalité de téléchargement

J'ai développé le bouton **"Enregistrer le Look"** qui permet de télécharger l'image finale générée par l'IA. L'implémentation utilise un lien `<a>` dynamique avec un data URI en base64, déclenchant le téléchargement côté client sans nécessiter de requête serveur supplémentaire.

# 7. Planning et méthodologie

## Méthodologie adoptée : Agile Scrum

### Diagramme de Gantt

![Diagramme_Gantt](/projets/clothconcept/docs/diagram_gantt.png)

Le projet a été développé en 4 sprints de 1 semaine, avec une méthodologie Agile Scrum. Les tâches critiques (en rouge) correspondent aux problèmes techniques rencontrés (incompatibilité SD Turbo avec CPU) qui ont nécessité une réorientation vers l'API cloud.

# 8. Conclusion
## Bilan du projet

Le projet ClotheConcep a permis de développer une application web complète et fonctionnelle qui répond aux objectifs initiaux : démocratiser la conception de vêtements en offrant une visualisation photo-réaliste instantanée grâce à l'intelligence artificielle générative.

### Objectifs atteints
**Interface utilisateur intuitive** : Wizard de personnalisation en plusieurs étapes avec feedback visuel instantané  
**Intégration IA réussie** : Génération d'images photo-réalistes en ~30 secondes via pipeline 2 étapes (API cloud)
**Architecture moderne** : Stack React + FastAPI + PostgreSQL respectant les bonnes pratiques  
**Persistance des données** : Sauvegarde automatique de l'historique des créations  
**Documentation complète** : Code commenté, README détaillé

# 9. Défis rencontrés et améliorations futures
## Défis rencontrés

### 1. Génération de la bibliothèque de patrons
L'utilisation de l'IA pour créer des patrons de style similaire qui respectent des critères stricts (mêmes dimensions pour tous les calques, même position du mannequin/silhouette de base, fond 100% transparent, même style de trait, même angle de vue) s'est avérée trop difficile. À chaque fois, le résultat généré était différent même avec le même prompt.

### 2. Explosion combinatoire
Il fallait générer toutes les combinaisons possibles par catégorie pour créer le patron qui serait ensuite envoyé à l'IA. Avec 3 types de cols × 2 types de manches × 2 types de tailles = 12 combinaisons minimum, et cela explose rapidement avec plus d'options.

### 3. Solution tentée : Layered Composition
Pour régler ce problème, j'ai essayé d'utiliser la méthode de **Layered Composition** utilisée dans l'industrie, qui sert d'assemblage du patron : générer une image par option (couche) puis les combiner dynamiquement (côté backend) par superposition des différents éléments, ce qui permet de générer UNE image finale à partir des couches au lieu de stocker une image par combinaison.

**Problème rencontré** : La génération des images d'option sur les mêmes critères par l'IA était impossible (il fallait consacrer plus de temps et essayer plusieurs prompts pour trouver le bon qui permet de générer toutes les options avec les mêmes critères). Puis le problème de la superposition de ces options pour pouvoir créer un patron réel et pas juste les parties superposées.

### 4. Problèmes de performance IA locale
Le temps de génération sur CPU était trop long (4 minutes par image). La tentative d'optimisation avec SD Turbo a fonctionné initialement mais a rencontré une erreur de compatibilité avec le setup CPU (`RuntimeError: "addmm_impl_cpu_" not implemented for 'Half'`), annulant le travail d'une semaine.

### 5. Optimisation du transfert de texture
La première approche consistait à créer une image composite (croquis + texture superposée avec opacité réduite) et à l'envoyer à un seul appel API. Les résultats étaient décevants : La texture était mal interprétée (couleurs altérées, motifs déformés), le contrôle de la structure du vêtement était insuffisant et l'IA "mélangeait" les deux sources d'information de manière imprévisible. 

### 6. Gestion des crédits API
L'API Stability AI fonctionne avec un système de crédits. Après épuisement des crédits gratuits, il faut acheter des crédits supplémentaires, ce qui représente un coût pour une utilisation en production.

## Améliorations futures

**Court terme** :
- Implémentation de la composition par calques (Layered Composition) pour une architecture modulaire
- Ajout d'options de personnalisation (cols, manches, tailles, types de pièces, tailles de vêtement)
- Amélioration du système de fallback pour la recherche de croquis
**Moyen terme** :
- Système d'authentification et galerie de créations partagées
- Export de patrons techniques (PDF avec mesures et instructions)
- Intégration e-commerce (paiement, réseau de tailleurs partenaires)
**Long terme** :
- Essayage virtuel 3D avec avatar personnalisable
- Recommandations IA basées sur tendances et préférences
- Application mobile native (React Native)
- Marketplace de designs entre utilisateurs
- Export vers logiciels de CAO professionnels pour fabrication industrielle

# Prérequis techniques

## Installation
Il faut installer :
- **Python** version minimum 3.10
- **Node.js** (version 18 ou supérieure)
- **PostgreSQL** (version 16 recommandée)
- Installer les différents packages nécessaires (voir sections ci-dessous)
- Avoir une clé d'API dans Stability AI (https://platform.stability.ai/)

## Commandes de lancement

### Frontend (React + TypeScript)
```bash
cd frontend
npm install
npm run dev
```
L'application sera accessible sur http://localhost:5173

### Backend (FastAPI + Python)
J'ai utilisé un environnement virtuel Python :

```bash
cd backend
python -m venv venv
venv\Scripts\activate  # Windows
# source venv/bin/activate  # Linux/Mac

pip install -r requirements.txt
uvicorn main:app --reload
```

L'API sera accessible sur http://localhost:8000  
Documentation Swagger : http://localhost:8000/docs

### Base de données (PostgreSQL)
```bash
# Créer la base de données
createdb clotheConcept

# Ou via DBeaver :
# Host: localhost
# Port: 5432
# Database: clotheConcept
# Username: postgres
# Password: [votre mot de passe]
```

### Configuration de l'API Stability AI
Créer un fichier `.env` dans le dossier `backend/` :

```env
STABILITY_API_KEY=sk-votre-cle-api-ici
```

## Annexes

### A. Diagramme séquentiel complémentaires

Diagramme de sauvegarde dans la base de données :

![Diagramme_seq_sauvegarde_BD](/projets/clothconcept/docs/diagram_seq_sauvegard_bd.png)

## B. Références

- **React Documentation** : https://react.dev/
- **FastAPI Documentation** : https://fastapi.tiangolo.com/
- **Stability AI API** : https://platform.stability.ai/docs
- **PostgreSQL Documentation** : https://www.postgresql.org/docs/
- **Tailwind CSS** : https://tailwindcss.com/
- **SQLAlchemy** : https://www.sqlalchemy.org/
- **Stable Diffusion** : https://stability.ai/stable-image

---

**Fin du rapport**