import argparse
import sys
from Bio import motifs
from Bio.Seq import Seq
from pwm import *
from utils import *
from Bio import Entrez

if __name__ == "__main__":
    
    Entrez.email = "bouchra.bouamama.etu@univ-lille.fr"
    parser = argparse.ArgumentParser(description="Help:", formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument("jasparMatrix", help="File containing the matrix in jaspar format")
    parser.add_argument("gbId", help="GenBank ID of the gene")
    parser.add_argument("length", help="length of the promoter sequence upstream")
    parser.add_argument("threshold", help="Score threshold")
    args = parser.parse_args()
    config = vars(args)

    jasparMatrix = config["jasparMatrix"]
    gbId = config["gbId"]
    length = int(config["length"])
    threshold = float(config["threshold"])
    pseudoCount = 0.1
    with open(jasparMatrix) as handle:
        motif = motifs.read(handle, "jaspar")
    geneId = mrna_to_gene(gbId)
    seq = upstream_gene_seq(geneId, length)
    pssm = pwm2pssm(motif, pseudoCount)
    occ = scan_sequence(pssm, seq, threshold)
    for pos, score in occ:
        print(f"{motif.name} {pos} {score}")
