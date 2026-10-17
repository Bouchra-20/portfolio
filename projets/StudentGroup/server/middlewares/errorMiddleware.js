/**
 * Middleware de gestion des erreurs 404.
 *
 * Ce middleware doit être appelé *après* toutes les routes.
 * Il intercepte les requêtes qui ne correspondent à aucune route définie
 * et renvoie une réponse HTML simple avec un code de statut 404.
 *
 * @param {Request} req - Objet de la requête HTTP.
 * @param {Response} res - Objet de la réponse HTTP.
 */
module.exports = function errorHandler(req, res) {
    res.status(404).send('<h1>404 - Page not found</h1>');
  };
  