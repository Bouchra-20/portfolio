function throws(ws, id, tiles) {
    /**
     * Envoie une commande pour lancer des tuiles.
     * @param {WebSocket} ws - WebSocket utilisé pour envoyer le message.
     * @param {string} id - Identifiant de l'auteur.
     * @param {Array<string>} tiles - Liste des tuiles lancées.
     */
    const tilesListe = tiles.join(' ');
    ws.send(`${id} THROWS ${tilesListe}`);
}

function places(ws, id, tile, orientation, cell) {
    /**
     * Envoie une commande pour qu’un joueur place une tuile sur le plateau.
     */
    ws.send(`${id} PLACES ${tile} ${orientation} ${cell}`);
}

function yields(ws, id) {
    /**
     * Envoie une commande pour qu’un joueur passe son tour.
     */
    ws.send(`${id} YIELDS`);
}

function scores(ws, id, rank, score, points) {
    /**
     * Envoie le score d’un joueur à la fin d’une manche ou d’une partie.
     */
    ws.send(`${id} SCORES ${rank} ${score} ${points}`);
}

function blames(ws, id, rank) {
    /**
     * Conteste un message.
     */
    ws.send(`${id} BLAMES ${rank}`);
}

function grants(ws, id, player, message) {
    /**
     * Accorde le droit à un autre joueur d'émettre un message spécifique.
     */
    ws.send(`${id} GRANTS ${player.id} ${message}`);
}

function agrees(ws, id, variante, varianteStr) {
    /**
     * Indique le choix d'une variante du jeu.
     */
    ws.send(`${id} AGREES ${varianteStr[variante]}`);
}

function elects(ws, id, players) {
    /**
     * Envoie une commande établissant la liste des participants.
     */
    const playerListe = players.map(player => player.name).join(' ');
    ws.send(`${id} ELECTS ${playerListe}`);
}

export { throws, places, yields, scores, blames, grants, agrees, elects };
