const path = require('path') ;
const express = require('express');
const router = express.Router();
/**
 * Gère la route `/index` pour envoyer la page HTML "index" au client.
 * @param {Object} req - Objet Request contenant les informations de la requête HTTP.
 * @param {Object} res - Objet Response utilisé pour envoyer une réponse HTTP.
 * @returns {HTML} - Retourne le fichier `todo.html` en réponse à la requête.
 */
router.get('/', (req, res) => {
    res.status(200).sendFile(path.join(__dirname, '../public/html/index.html'));
});

module.exports = router;