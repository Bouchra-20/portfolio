from Bio import SeqIO
from Bio import Entrez
from Bio import motifs
import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from src.compareseq import compare_sequences


# Fonction pour afficher une section avec un titre
def print_section(title, content):
    """
    Affiche une section formatée avec un titre et un contenu.

    :param title: Titre de la section
    :param content: Contenu à afficher
    """
    print("\n" + "="*10 + f" {title} " + "="*10)
    print(content)

# Partie 1

# La fonction find_cds
def find_cds(seqRecord):
    """
    Trouve les positions des CDS (Coding Sequences) dans un objet SeqRecord.

    :param seqRecord: Objet SeqRecord de Biopython contenant les annotations
    :return: Liste de tuples contenant les positions de début et de fin des CDS
    """
    cds_positions = []

    for feature in seqRecord.features :
        if feature.type == "CDS":
            start = int(feature.location.start)
            end = int(feature.location.end)
            cds_positions.append((start, end))

    return cds_positions


# Partie 2
#Q3 : La fonction mrna_to_gene
def mrna_to_gene(numero):
    """
    Convertit un identifiant mRNA en identifiant de gène à l'aide de l'API Entrez.

    :param numero: Identifiant de l'ARNm (par exemple, un NM_)
    :return: Identifiant du gène correspondant
    :raises ValueError: Si aucun gène n'est trouvé pour l'accession donnée
    """
     
    handle = Entrez.elink(dbfrom="nucleotide", db="gene", id=numero)
    record = Entrez.read(handle)
    handle.close()
        
    try:
        return record[0]['LinkSetDb'][0]['Link'][0]['Id']
    except (IndexError, KeyError):
        raise ValueError(f"Aucun gène trouvé pour l'accession {numero}")

#Q3: La fonction upstream_gene_seq
def upstream_gene_seq(gene_id, length):
    """
    Récupère la séquence en amont d'un gène donné.

    :param gene_id: Identifiant du gène
    :param length: Longueur de la séquence en amont à récupérer
    :return: Séquence nucléotidique en amont du gène sous forme d'un objet Seq
    """
    try:
        handle = Entrez.esummary(db="gene", id=gene_id)
        summary = Entrez.read(handle)
        handle.close()

        genomic_info = summary["DocumentSummarySet"]["DocumentSummary"][0]["GenomicInfo"][0]
        chrom_accession = genomic_info["ChrAccVer"]
        start_pos = int(genomic_info["ChrStart"])
        end_pos = int(genomic_info["ChrStop"])

        if start_pos < end_pos:
            upstream_start = start_pos - length
            upstream_end = start_pos - 1
        else:
            upstream_start = end_pos + length
            upstream_end = end_pos + 1

        handle = Entrez.efetch(
            db="nucleotide",
            id=chrom_accession,
            rettype="fasta",
            seq_start=upstream_start,
            seq_stop=upstream_end
        )
        record = SeqIO.read(handle, "fasta")
        handle.close()

        if start_pos > end_pos:
            record.seq = record.seq.reverse_complement()

        return record.seq

    except Exception as e:
        print(f"Error: {e}")
        return None


#Q4
def FPM_to_PSSM(matrix):
    """
    Convertit une matrice de fréquence JASPAR (FrequencyPositionMatrix) en une PSSM.

    :param matrix: Objet FrequencyPositionMatrix (Biopython)
    :return: Objet PositionSpecificScoringMatrix (PSSM)
    """
    return matrix.counts.normalize(pseudocounts=0.1).log_odds()

#TP4
def download_promotors(l_mrna, taille_seq, dir="."):
    '''
    * l_mrna: une liste d'identifiants mrna,
    * taille_seq: une taille de sequence de gene a recuperer
    * dir: le repetoire destination

    Télécharge les séquences promotrices de ces mRNA et les enregistre en fichiers FASTA.
    '''
    Entrez.email ="bouchra.bouamama.etu@univ-lille.fr"
    for mrna in l_mrna:
        nom_fichier = str(mrna) + "_" + str(taille_seq) + ".fasta"
        chemin_fichier = os.path.join(os.getcwd(), dir, nom_fichier)
      
        id = mrna_to_gene(mrna)
        handle = Entrez.esummary(db="gene", id=id)
        summary = Entrez.read(handle)
        handle.close()

        genomic_info = summary["DocumentSummarySet"]["DocumentSummary"][0]["GenomicInfo"][0]
        chrom_accession = genomic_info["ChrAccVer"]
        start_pos = int(genomic_info["ChrStart"])
        end_pos = int(genomic_info["ChrStop"])

        if start_pos < end_pos:
            upstream_start = start_pos - taille_seq
            upstream_end = start_pos - 1
        else:
            upstream_start = end_pos + taille_seq
            upstream_end = end_pos + 1

        try:
            handle = Entrez.efetch(
                db="nucleotide",
                id=chrom_accession,
                rettype="fasta",
                seq_start=upstream_start,
                seq_stop=upstream_end
            )
            record = handle.read()
            handle.close()

            with open(chemin_fichier, "w") as file:
                    file.write(record)

            print(f"Fichier sauvegardé : {chemin_fichier}")

        except Exception as e:
            print(f"Erreur lors du téléchargement pour {mrna}: {e}")
    
    return [os.path.join(dir, str(mrna) + "_" + str(taille_seq) + ".fasta") for mrna in l_mrna]

if __name__ == "__main__":

    #Q1: Lecture des séquences
    genbank45 = SeqIO.read("data/sequence_NM_000451.gb", "genbank")

    genbank73 = SeqIO.read("data/sequence_NM_007389.gb", "genbank")

    fasta45 = SeqIO.read("data/sequence_NM_000451.fasta", "fasta")

    fasta73 = SeqIO.read("data/sequence_NM_007389.fasta", "fasta")


    # Q2: Affichage de la séquence fasta et comparaison
    print_section("Séquence FASTA", str(fasta45.seq))
    print_section("Comparaison Séquences", compare_sequences(fasta45.seq, genbank45.seq))

    # Q3: Reverse complement
    rc_fasta45 = fasta45.reverse_complement(id=fasta45.id + "_rc")
    print_section("Reverse Complement", rc_fasta45.seq)

    # Q4: Annotations et Features
    print_section("Annotations FASTA", fasta45.annotations)
    print_section("Annotations GenBank", genbank45.annotations)
    print_section("Features FASTA", fasta45.features)
    print_section("Features GenBank", genbank45.features)

    #Q5: Première feature
    first_feature = genbank45.features[0]
    print_section("Première Feature - Position", f"Start: {first_feature.location.start}, End: {first_feature.location.end}")


    cds_positions = find_cds(genbank45)
    print_section("Positions des CDS", cds_positions)

    ##Partie2
    #efetch
    #Q1 : Récupération de l'entrée NM_007389
    Entrez.email = "bouchra.bouamama.etu@univ-lille.fr"

    handle1 = Entrez.efetch(db="nucleotide", id="NM_007389", rettype="gb", retmode="text")
    handle_genbank = SeqIO.read(handle1, "genbank")
    print_section("Séquence récupérée - GenBank", handle_genbank)

    handle2 = Entrez.efetch(db="nucleotide", id="NM_007389", rettype="fasta", retmode="text")
    handle_fasta = SeqIO.read(handle2, "fasta")
    print_section("Séquence récupérée - FASTA", handle_fasta)

    #Q2: Comparaison Séquences
    print_section("Comparaison Séquences Efetch", compare_sequences(handle_fasta.seq, handle_genbank.seq))

    #Q3: Recherche des CDS sur la séquence téléchargée
    find1 = find_cds(genbank73)
    find2 = find_cds(handle_genbank)

    print_section("Comparaison CDS locaux vs Efetch", compare_sequences(find1, find2))

    #elink
    #Q1 : Récupération du géne correspondant à l'entrée NM_007389
    handle1 = Entrez.elink(dbfrom="nucleotide", db="gene", id="NM_007389")
    record1 = Entrez.read(handle1)
    handle1.close()
    #Q2 : Recupération de l'identifiant du gène
    gene_id1 = record1[0]["LinkSetDb"][0]["Link"][0]['Id']
    print_section("Identifiant du gène - NM_007389", gene_id1)

    #Q3 : La fonction mrna_to_gene

    gene_id2 = mrna_to_gene("NM_000451")
    print_section("Identifiant du gène - NM_000451", gene_id2)

    handle_451 = Entrez.elink(dbfrom="nucleotide", id="NM_000451", db="gene")
    record_451 = Entrez.read(handle_451)
    handle_451.close()

    print_section("Comparaison Séquences Elink",compare_sequences(gene_id2, record_451[0]['LinkSetDb'][0]['Link'][0]['Id']))

    ##Partie 2 - ESummary

    #Q1 : Récupération des informations génomiques
    # SHOX
    handle2 = Entrez.esummary(db="gene", id="6473")
    record2 = Entrez.read(handle2)
    handle2.close()

    genomic_info = record2["DocumentSummarySet"]["DocumentSummary"][0]["GenomicInfo"][0]
    chrom_accession = genomic_info["ChrAccVer"]
    start_pos = genomic_info["ChrStart"]
    end_pos = genomic_info["ChrStop"]

    print_section("Infos Génomiques SHOX", f"Chromosome: {chrom_accession}, Start: {start_pos}, End: {end_pos}")


    # CHRNA1
    handle2 = Entrez.esummary(db="gene", id="11435")
    record2 = Entrez.read(handle2)
    handle2.close()

    genomic_info = record2["DocumentSummarySet"]["DocumentSummary"][0]["GenomicInfo"][0]
    chrom_accession = genomic_info["ChrAccVer"]
    start_pos = genomic_info["ChrStart"]
    end_pos = genomic_info["ChrStop"]

    print_section("Infos Génomiques CHRNA1", f"Chromosome: {chrom_accession}, Start: {start_pos}, End: {end_pos}")

    #Q2: Récupération de la séquence en amont d'un gène
    gene_id = "NC_000068.8"
    start_position = 73409681
    end_position = 73410681

    handle = Entrez.efetch(db="nucleotide", id=gene_id, 
                        rettype="gb", retmode="text", 
                        seq_start=start_position, seq_stop=end_position)

    record = SeqIO.read(handle, "genbank")
    handle.close()
    print_section("Séquence récupérée - Efetch", record.seq)

    #Q3: La fonction upstream_gene_seq

    print_section("Séquence en amont du gène", upstream_gene_seq("11435", 1000))


    # Partie 3

    #Q1
    print("=== Chargement de fichier JASPAR ===")
    i = 0
    with open('data/JASPAR2024_CORE_vertebrates_non-redundant_pfms_jaspar.jaspar') as handle:
        for m in motifs.parse(handle, "jaspar"):
            i = i + 1
            print(m.consensus)
        print(i)

    #Q2
    print("=== Position-weight matrix ===")
    with open("data/JASPAR2024_CORE_vertebrates_non-redundant_pfms_jaspar.jaspar") as handle:
        jaspar_motifs = motifs.parse(handle, "jaspar")

    for motif in jaspar_motifs:
        pwm = motif.counts.normalize(pseudocounts=0.1)
        print(pwm)

    #Q3
    print("=== PositionSpecificScoringMatrix ===")
    with open("data/JASPAR2024_CORE_vertebrates_non-redundant_pfms_jaspar.jaspar") as handle:
        jaspar_motifs = motifs.parse(handle, "jaspar")

    for motif in jaspar_motifs:
        pwm = motif.counts.normalize(pseudocounts=0.1)
        pssm = pwm.log_odds()
        print(pssm)

    #Q4

    print_section("Function FPM_to_PSSM", FPM_to_PSSM(jaspar_motifs[len(jaspar_motifs)-1]))

    #Q5
    pwm = jaspar_motifs[len(jaspar_motifs)-41].counts.normalize(pseudocounts=0.1)
    pssm = pwm.log_odds()

    sequence_exemple = "AGCTGACCTGAGGTTACAGTGCAGT"

    result = []

    for position, score in pssm.search(sequence_exemple, threshold=0.0):
        result.append((position, score))
    
    results = [(int(pos), round(float(score), 2)) for pos, score in result]
    print_section("Occurrences trouvées :", results)

    list = ["NM_007389", "NM_079420", "NM_001267550", "NM_002470", "NM_003279", "NM_005159", "NM_003281", "NM_002469", "NM_004997", "NM_004320", "NM_001100", "NM_006757"]
    download_promotors(list, 100, "data")

