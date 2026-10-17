const mongoose = require('mongoose');

/**
 * Schéma Mongoose pour représenter l'appartenance d’un étudiant à un groupe.
 * 
 * Contraintes :
 * - Chaque document relie un étudiant à un groupe numéroté de 1 à 6.
 * - Un étudiant peut apparaître une seule fois dans cette collection.
 */
const groupeSchema = new mongoose.Schema({
    etudiantId: { type: mongoose.Schema.Types.ObjectId, ref: 'Student', required: true },
    groupe: { type: Number, min: 1, max: 6, required: true }
});

module.exports = mongoose.model('Groupe', groupeSchema);
