const express = require('express');
const router = express.Router();
const groupeController = require('../controllers/groupeController');

// Récupérer les étudiants sans groupe
router.get('/', groupeController.getStudentsWithoutGroup);

// Récupérer les étudiants d'un groupe
router.get('/:num', groupeController.getStudents);

// Ajouter un étudiant à un groupe
router.post('/', groupeController.addStudent);

// Retirer un étudiant d'un groupe
router.delete('/:id', groupeController.deleteStudent);

module.exports = router;
