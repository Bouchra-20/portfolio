# Bioinformatique

## Introduction

Ce projet développe un outil Python (`putative_TFBS.py`) qui identifie des sites putatifs de liaison aux facteurs de transcription (TFBS) dans les promoteurs de gènes, à partir d’identifiants mRNA et d’une matrice JASPAR. Une page HTML/JavaScript interactive permet également de visualiser les résultats.

## Description de l'implémentation

Le script ``putative_TFBS.py`` permet d'identifier et d'extraire les séquences promotrices de mRNA spécifiés, d'appliquer une matrice PFM pour évaluer les scores d'occurrences et d'analyser ces résultats via des fenêtres glissantes.

### Les principales étapes du script sont :

- **Parsing des arguments :** Utilisation de ``argparse`` pour spécifier les paramètres comme le fichier PFM, les seuils de score, la taille de la fenêtre, etc.
- **Chargement de la matrice :** Lecture de la matrice JASPAR avec Bio.motifs et conversion en PSSM avec une fonction pwm2pssm (dans le module pwm).
- **Téléchargement des promoteurs :** Récupération des séquences promotrices via une fonction download_promotors (dans utils).
- **Scan des séquences :** Analyse de chaque séquence avec la PSSM pour détecter les TFBS au-dessus d’un seuil donné (`scan_sequence` dans le module `pwm`).
- **Analyse par fenêtre :** Identification de la fenêtre optimale avec best_window (dans le module pwm).
- **Exportation :** Génération d’un fichier JSON contenant les paramètres et les résultats.

### Paramètres du Programme

Le programme accepte plusieurs arguments en ligne de commande :

- *-m, --pfm :* Fichier contenant la matrice JASPAR.
- *-t, --threshold :* Seuil de score de matrice.
- *-l, --promotor-length :* Longueur du promoteur (par défaut 1000).
- *-w, --window-size :* Longueur de la fenêtre glissante (par défaut 40).
- *-s, --window-threshold :* Seuil de score de la fenêtre.
- *-p, --pseudocount :* Valeur du pseudo-poids (par défaut 0.1).
- *-o, --output :* Nom du fichier de sortie au format JSON.
- *mrna_ids :* Liste des identifiants mRNA.

### Exécution

Depuis le dossier `projets/BioInfo/Projet/`, exécuter la commande suivante pour générer le fichier JSON :

```
    Projet> python3 .\src\putative_TFBS.py -m data/MA0056.jaspar -t -20 -l 1000 -w 40 -s -100 -p 0.1 -o results.json NM_007389 NM_079420 NM_001267550 NM_002470 NM_003279 NM_005159 NM_003281 NM_002469 NM_004997 NM_004320 NM_001100 NM_006757 
```

### Résultats et Analyse

Le fichier JSON produit contient deux sections principales :
- `parameters`: Les paramètres utilisés pour obtenir les résultats.
- `results`: Les résultats des analyses, y compris les fenêtres d'intérêt et les occurrences.

```json
{
    "parameters": {
        "pfm_file": "data/MA0083.3.jaspar",
        "threshold": -20.0,
        "promotor_length": 1000,
        "window_size": 40,
        "window_threshold": -100.0,
        "pseudocount": 0.1,
        "output_file": "results.json",
        "mrna_ids": [
            "NM_007389",
            "NM_079420"
        ]
    },
    "results": [
        {
            "window_num": 1,
            "tf_name": "SRF",
            "window_range": "[-370:-330]",
            "window_score": -4.3073811531066895,
            "num_occurrences": 1,
            "occurrences": [
                [
                    1,
                    "NM_007389",
                    "SRF",
                    -370,
                    -4.3073811531066895
                ]
            ]
        },
        {
            "window_num": 2,
            "tf_name": "SRF",
            "window_range": "[-370:-330]",
            "window_score": -4.3073811531066895,
            "num_occurrences": 1,
            "occurrences": [
                [
                    2,
                    "NM_079420",
                    "SRF",
                    -370,
                    -4.3073811531066895
                ]
            ]
        }
    ]
}
```

Les résultats sont également affichés dans le terminal lors de l’exécution du script.

## Visualisation

### Description

Une page HTML avec JavaScript et Canvas a été développée pour visualiser les résultats du JSON :
- *Chargement :* Importation du fichier JSON via un champ <input type="file">.
- *Affichage :* Dessin des promoteurs (rectangles gris), fenêtres optimales (rectangles rouges), et occurrences (rectangles bleus) sur un canvas.
- *Interactivité :* Infobulles au survol pour afficher les détails (position, score).

### Lancement de la page

Pour lancer la page ``viewPutativeTFBS.html`` :

Exécuter :

```
    \Projet> python3 -m http.server 8000
```

Accéder à la page : 

```
    http://localhost:8000/viewPutativeTFBS.html
```


## Tests

Depuis le dossier `projets/BioInfo/Projet/`, lancer les tests avec les commandes suivantes :

- Pour lancer ``test_args.py``:

```
    python3 src/test_args.py -t -20 -l 1500 -w 30 NM_007389
```

- Pour lancer ```test_utils`` :

````
    python3 -m unittest tests\test_utils.py
````

- Pour lancer ``test_pwm.py`` :

````
    python3 -m unittest tests\test_pwm.py
````

## Exécution d'autres fichiers

- **`scan_pwm.py`** :
Pour exécuter le fichier ``scan_pwm.py``, on prend en argument dans cet ordre :
1. un fichier contenant une matrice JASPAR ;
2. un identifiant Genbank d’un mRNA ;
3. une taille pour la séquence promotrice ;
4. un seuil de score.

````
    Projet> python3 src/scan_pwm.py data/MA0083.3.jaspar NM_000451 500 -20
````

La commande lance le fichier `data/MA0083.3.jaspar`, on peut prendre d'autres matrices JASPAR.

- **`utils.py`** :

````
    Projet> python3 src/utils.py
````

- **`pwm.py`** :

````
    Projet> python3 src/pwm.py
````
