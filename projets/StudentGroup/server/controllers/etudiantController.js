const path = require('path');
const Etudiant = require('../models/student');

/**
 * Contrôleur pour gérer les opérations sur les étudiants.
 * Fournit les méthodes pour les routes REST : GET, POST, PUT, DELETE.
 */
class EtudiantController {
    /**
     * Récupère tous les étudiants depuis la base de données.
     * Méthode GET sur /students
     */
    async allStudents(req, res) {
        try {
            const etudiants = await Etudiant.find();
            res.json(etudiants);
        } catch (err) {
            res.status(500).json({ message: err.message });
        }
    };

    /**
     * Ajoute un nouvel étudiant à la base de données.
     * Méthode POST sur /students
     */
    async addStudent(req, res) {
        try {
            const { numero, nom, prenoms } = req.body;
            if (!Number.isInteger(Number(numero)) || Number(numero) < 0) {
                return res.status(400).json({ 
                    message: "Le numéro étudiant doit être un entier positif." 
                });
            }
            const existingStudent = await Etudiant.findOne({ numero });
            if (existingStudent) {
                return res.status(409).json({ 
                    message: "Création impossible, le numéro étudiant doit être unique" 
                });
            }
            const etudiant = new Etudiant({ numero, nom, prenoms });
            await etudiant.save();
            res.status(201).json(etudiant);
        } catch (err) {
            res.status(400).json({ message: err.message });
        }
    };

     /**
     * Supprime un étudiant par son ID.
     * Méthode DELETE sur /students/:id
     */
    async deleteStudent(req, res) {
        try {
            await Etudiant.findByIdAndDelete(req.params.id);
            res.json({ message: 'Étudiant supprimé' });
        } catch (err) {
            res.status(500).json({ message: err.message });
        }
    };

    /**
     * Met à jour les données d’un étudiant existant.
     * Méthode PUT sur /students/:id
     */
    async updateStudent(req, res) {
        try {
            const { numero, nom, prenoms } = req.body;
            if (!Number.isInteger(Number(numero)) || Number(numero) < 0) {
                return res.status(400).json({ 
                    message: "Le numéro étudiant doit être un entier positif." 
                });
            }
            const student = await Etudiant.findByIdAndUpdate(
                req.params.id,
                { numero, nom, prenoms },
                { new: true, runValidators: true }
            );
            if (!student) {
                return res.status(404).json({ message: 'Étudiant non trouvé' });
            }
            res.json({ message: 'Étudiant mis à jour avec succès', student });
        } catch (err) {
            if (err.code === 11000) {
                res.status(400).json({ message: 'Mise à jour impossible, le numéro doit être unique' });
            } else {
                res.status(400).json({ message: err.message });
            }
        }
    }
}

module.exports = new EtudiantController();