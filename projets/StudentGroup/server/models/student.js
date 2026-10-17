const mongoose = require('mongoose');

/**
 * Schéma Mongoose pour représenter un étudiant.
 * 
 * Contraintes :
 * - `numero` : unique, requis, chaîne, ≥ 0
 * - `nom` : requis, en majuscules, sans espaces en début ou fin
 * - `prenoms` : tableau de chaînes, capitalisé automatiquement
 */
const etudiantSchema = new mongoose.Schema({
    numero: { type: String, unique: true, required: true, min: 0 },
    nom: { 
        type: String, 
        required: true, 
        trim: true, 
        uppercase: true 
    },
    prenoms: {
        type: [String],
        required: true,
        set: prenoms => prenoms.map(p => p.trim().charAt(0).toUpperCase() + p.slice(1).toLowerCase()),
        get: prenoms => prenoms.join(', ')
    }
});

module.exports = mongoose.model('Student', etudiantSchema);
