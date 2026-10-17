const express = require('express');
const router = express.Router();
const Groupe = require('../models/group');
const Etudiant = require('../models/student');
/**
 * Contrôleur pour la gestion des groupes.
 * Permet d’affecter des étudiants à un groupe, de lister les membres, ou de les retirer.
 */
class GroupeController {

    /**
     * Récupère tous les étudiants qui ne sont affectés à aucun groupe.
     * Méthode GET sur `/groups` sans paramètre
     */
    async getStudentsWithoutGroup(req, res) {
        try {
            const assignedStudents = await Groupe.find().distinct('etudiantId');
            const students = await Etudiant.find({ _id: { $nin: assignedStudents } });
            res.json(students);
        } catch (err) {
            res.status(500).json({ message: err.message });
        }
    };

    /**
     * Récupère les étudiants affectés à un groupe donné.
     * Méthode GET sur `/groups/:num`
     */
    async getStudents(req, res) {
        try {
            const groupes = await Groupe.find({ groupe: req.params.num }).populate('etudiantId');
            const validGroupes = groupes.filter(g => g.etudiantId !== null);
            if (validGroupes.length === 0) {
                return res.json([]);
            }
            res.json(validGroupes);
        } catch (err) {
            res.status(500).json({ message: err.message });
        }
    };

    /**
     * Affecte un étudiant à un groupe donné.
     * Méthode POST sur `/groups`
     */
    async addStudent(req, res) {
        try {
            const { etudiantId, groupe } = req.body;
            const assignation = new Groupe({ etudiantId, groupe });
            await assignation.save();
            res.status(201).json(assignation);
        } catch (err) {
            res.status(400).json({ message: err.message });
        }
    };

    /**
     * Supprime l’affectation d’un étudiant à un groupe.
     * Méthode DELETE sur `/groups/:id`
     */
    async deleteStudent(req, res) {
        try {
            const result = await Groupe.findOneAndDelete({ etudiantId: req.params.id });
            if (!result) {
                return res.status(404).json({ message: 'Aucune assignation trouvée pour cet étudiant' });
            }
            res.json({ message: 'Étudiant retiré du groupe' });
        } catch (err) {
            console.error('Erreur dans deleteStudent :', err);
            res.status(500).json({ message: err.message });
        }
    };
}
module.exports = new GroupeController();
