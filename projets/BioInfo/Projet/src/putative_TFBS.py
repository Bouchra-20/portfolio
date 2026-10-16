import argparse
import json
import numpy as np
from Bio import SeqIO, motifs
import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))
from src.pwm import *
from src.utils import *

def main():
    parser = argparse.ArgumentParser(description="Recherche d’occurrences de matrices PFM dans un ensemble de promoteurs.")
    
    parser.add_argument('-m', '--pfm', required=True, help="Fichier contenant la matrice JASPAR")
    parser.add_argument('-t', '--threshold', required=True, type=float, help="Seuil de score de matrice")
    parser.add_argument('-l', '--promotor-length', default=1000, type=int, help="Longueur du promoteur (par défaut 1000)")
    parser.add_argument('-w', '--window-size', default=40, type=int, help="Longueur de la fenêtre glissante (par défaut 40)")
    parser.add_argument('-s', '--window-threshold', required=True, type=float, help="Seuil de score de fenêtre")
    parser.add_argument('-p', '--pseudocount', default=0.1, type=float, help="Valeur du pseudo-poids (par défaut 0.1)")
    parser.add_argument('-o', '--output', required=True, help="Nom du fichier de sortie au format JSON")
    parser.add_argument("mrna_ids", nargs="+", help="Liste d'identifiants mRNA.")

    args = parser.parse_args()

    with open(args.pfm) as handle:
        matrix = motifs.parse(handle, "jaspar")[0]

    pssm = pwm2pssm(matrix, pseudocounts=args.pseudocount)

    output_dir = "data"
    download_promotors(args.mrna_ids, args.promotor_length, output_dir)

    sequences = []
    for mrna_id in args.mrna_ids:
        fasta_file = os.path.join(output_dir, f"{mrna_id}_{args.promotor_length}.fasta")
        if os.path.exists(fasta_file):
            seq_record = SeqIO.read(fasta_file, "fasta")
            sequences.append(seq_record.seq)
        else:
            print(f"Impossible de trouver la séquence pour {mrna_id}.", file=sys.stderr)

    window_results = []

    for i, seq in enumerate(sequences):
        mrna_id = args.mrna_ids[i]
        
        scan_results = scan_sequence(pssm, seq, threshold=args.threshold)

        if not scan_results:
            print(f"Aucune occurrence trouvée pour {mrna_id}.", file=sys.stderr)
            continue

        best_window_result = best_window(scan_results, args.window_size)
        if best_window_result[0] is not None:
            (best_start, best_end), best_score = best_window_result
            
            if best_score > args.window_threshold:
                num_occurrences = sum(
                    1 for pos, score in scan_results 
                    if best_start <= pos < best_end
                )
                
                window_results.append({
                    "window_num": None,
                    "tf_name": matrix.name,
                    "window_range": f"[{best_start}:{best_end}]",
                    "window_score": float(best_score),
                    "num_occurrences": int(num_occurrences),
                    "occurrences": [
                        (
                            None,
                            mrna_id,
                            matrix.name,
                            int(pos),
                            float(score)
                        )
                        for pos, score in scan_results 
                        if best_start <= pos < best_end
                    ]
                })
            else:
                print(f"Aucune fenêtre avec un score suffisant trouvée pour {mrna_id}.", file=sys.stderr)
        else:
            print(f"Aucune fenêtre trouvée pour {mrna_id}.", file=sys.stderr)

    window_groups = {}
    for result in window_results:
        window_range = result["window_range"]
        if window_range not in window_groups:
            window_groups[window_range] = {
                "tf_name": result["tf_name"],
                "window_range": window_range,
                "window_score": 0,
                "num_occurrences": 0,
                "occurrences": []
            }
        window_groups[window_range]["window_score"] += result["window_score"]
        window_groups[window_range]["num_occurrences"] += result["num_occurrences"]
        window_groups[window_range]["occurrences"].extend(result["occurrences"])

    grouped_results = []
    for window_num, (window_range, group) in enumerate(window_groups.items()):
        group["window_num"] = window_num
        group["occurrences"] = [
            (
                window_num,
                occ[1],
                occ[2],
                occ[3],
                occ[4]
            )
            for occ in group["occurrences"]
        ]
        grouped_results.append(group)

    # Afficher les résultats sur le terminal
    for result in grouped_results:
        print(f"{result['window_num']} {result['tf_name']} {result['window_range']} {result['window_score']:.2f} {result['num_occurrences']}")
    print()

    for result in grouped_results:
        for occurrence in result["occurrences"]:
            print(f"{occurrence[0]} {occurrence[1]} {occurrence[2]} {occurrence[3]} {occurrence[4]:.2f}")
        print()

    # Sauvegarder dans le fichier JSON avec la structure correcte
    export_data = {
        "parameters": {
            "pfm_file": args.pfm,
            "threshold": args.threshold,
            "promotor_length": args.promotor_length,
            "window_size": args.window_size,
            "window_threshold": args.window_threshold,
            "pseudocount": args.pseudocount,
            "output_file": args.output,
            "mrna_ids": args.mrna_ids
        },
        "results": grouped_results
    }

    with open(args.output, "w") as json_file:
        json.dump(export_data, json_file, indent=4)

if __name__ == "__main__":
    main()
