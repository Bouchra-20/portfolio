import sys
import os
from random import randint
from MainExp import LARGEUR, HAUTEUR

TAILLE_SEQUENCE = 15 # Nombre de clics requis pour l'expérience
GLOBAL_FOLDER = "exps" # Dossier racine pour stocker les condit

def generateNameFolder(number_of_targets, size_of_targets, space_between_targets):
    """
    Crée une hiérarchie de dossiers pour organiser les fichiers CSV par condition.
    Ex: exps/Exp_60_12_5/
    """
    name_folder = f"{GLOBAL_FOLDER}/Exp_{number_of_targets}_{size_of_targets}_{space_between_targets}"
    if not os.path.exists(GLOBAL_FOLDER):
        os.mkdir(GLOBAL_FOLDER)
    if not os.path.exists(name_folder):
        os.mkdir(name_folder)
    return name_folder

def generateTargetsRandomly(number_of_targets, size_of_targets, space_between_targets):
    """
    Génère des cibles aléatoires sans chevauchement.
    Utilise l'algorithme de vérification de distance euclidienne.
    """
    targets = []

    while len(targets) < number_of_targets:
        size = size_of_targets

        # On utilise 'size' comme marge pour que la cible ne dépasse pas du bord de l'écran.
        x = randint(size, LARGEUR - size)
        y = randint(size, HAUTEUR - size)

        valid = True
        for target in targets:
            other_x, other_y, other_size = target
            # Calcul de la distance euclidienne entre les deux centres
            distance = ((x - other_x)**2 + (y - other_y)**2)**0.5
            # La distance doit être supérieure à la somme des rayons + l'espace minimal requis.
            if distance < (size + other_size) / 2 + space_between_targets:
                valid = False
                break

        if valid:
            targets.append((x, y, size))

    return targets

def exportTargetsAsCSV(folderName, targets):
    """
    Sauvegarde la liste des cibles (x, y, size) dans un fichier targets.csv.
    """
    with open(f"{folderName}/targets.csv", "w") as file:
        for x, y, size in targets:
            file.write(f"{x},{y},{size}\n")

def generateSequenceRandomly(targets):
    """
    Crée une séquence d'indices aléatoires parmi les cibles générées.
    Garantit que chaque cible de la séquence est unique (pas de répétition).
    """
    sequence = []
    copy_targets = [False for target in targets]

    while len(sequence) < TAILLE_SEQUENCE:
        index = randint(0, len(targets) - 1)
        if not copy_targets[index]:
            copy_targets[index] = True
            sequence.append(index)

    return sequence

def exportSequenceAsCSV(folderName, sequence):
    """
    Sauvegarde la séquence d'indices (l'ordre des clics) dans sequence.csv.
    """
    with open(f"{folderName}/sequence.csv", "w") as file:
        for index in sequence:
            file.write(f"{index}\n")



def main():
    if len(sys.argv) != 4:
        print("Usage: python GenerateExp.py <number_of_targets> <size_of_targets> <space_between_targets>")

    number_of_targets = int(sys.argv[1])
    size_of_targets = int(sys.argv[2])
    space_between_targets = int(sys.argv[3])

    folderName = generateNameFolder(number_of_targets, size_of_targets, space_between_targets)

    targets = generateTargetsRandomly(number_of_targets, size_of_targets, space_between_targets)
    exportTargetsAsCSV(folderName, targets)

    sequence = generateSequenceRandomly(targets)
    exportSequenceAsCSV(folderName, sequence)

if __name__ == "__main__":
    main()