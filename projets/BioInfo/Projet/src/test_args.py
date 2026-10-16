import argparse

def main():
    parser = argparse.ArgumentParser(description="Analyse des gènes avec des paramètres définis.")
    
    parser.add_argument('-t', '--threshold', type=float, help="Seuil de score", default=None)
    parser.add_argument('-l', '--promotor-length', type=int, help="Longueur du promoteur", default=1000)
    parser.add_argument('-w', '--window-size', type=int, help="Taille de la fenêtre glissante", default=40)
    
    parser.add_argument('mRNA_ids', nargs='+', help="Identifiants des mRNA à analyser")

    args = parser.parse_args()
    
    print(f"Seuil de score (threshold) : {args.threshold}")
    print(f"Longueur du promoteur : {args.promotor_length}")
    print(f"Taille de la fenêtre glissante : {args.window_size}")
    
    print(f"Identifiants des mRNA : {args.mRNA_ids}")

if __name__ == "__main__":
    main()
