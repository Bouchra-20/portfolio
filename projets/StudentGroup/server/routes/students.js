const express = require('express');
const router = express.Router();
const etudiantController = require('../controllers/etudiantController');

// Récupérer tous les étudiants
router.get('/', etudiantController.allStudents);

// Ajouter un étudiant
router.post('/', etudiantController.addStudent);

// Mettre à jour un étudiant
router.put('/:id', etudiantController.updateStudent);

// Supprimer un étudiant
router.delete('/:id', etudiantController.deleteStudent);

module.exports = router;
