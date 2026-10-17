/**
 * Client-side voting interface script
 * Handles vote submission, status updates and result display
 */

import io from 'socket.io-client';
import { createOrUpdateChart } from '../shared/chartConfig.js';

/**
 * Initialize socket connection to server
 */
const socket = io('http://localhost:8080');

/**
 * Reference to the results chart instance
 */
let resultChart = null;

/**
 * Handle socket connection event
 */
socket.on('connect', () => {
  console.log('Connecté au serveur');
  socket.emit('votantMessage', 'Hello from votant');
});

/**
 * Handle new user connection event
 */
socket.on('new user', newUser => {
  console.log('Nouvel utilisateur :', newUser);
});

/**
 * Setup voting interface when DOM is loaded
 */
document.addEventListener('DOMContentLoaded', () => {
    const voteForm = document.getElementById('vote-form');
    const voteButtons = document.querySelectorAll('.vote-button');
    const voteStatus = document.getElementById('vote-status');
    const voteSubject = document.getElementById('vote-subject');
    let voteStarted = false;

    voteForm.classList.add('hidden');

    /**
     * Handle vote start event
     * @param {string} subject - The subject of the vote
     */
    socket.on('vote start', (subject) => {
        voteStarted = true;
        voteStatus.textContent = subject;
        voteSubject.textContent = 'Vote en cours: ' + subject;
        enableVoteButtons();
        voteForm.classList.remove('hidden');
        document.getElementById('resultats-chart').style.display = 'none';
        if (resultChart) {
          resultChart.destroy();
          resultChart = null;
        }
    });

    /**
     * Handle vote end event
     * @param {Object} data - The final vote results data
     */
    socket.on('vote ended', (data) => {
        voteStarted = false;
        voteStatus.textContent = 'Vote terminé - Résultats';
        voteSubject.textContent = '';
        disableVoteButtons();
        voteForm.classList.add('hidden');
        resultChart = createOrUpdateChart(resultChart, data, 'resultats-chart');
        document.getElementById('resultats-chart').style.display = 'block';
    });

    /**
     * Setup click handlers for vote buttons
     */
    voteButtons.forEach(button => {
        button.addEventListener('click', () => {
            if (!voteStarted) {
                alert('Le vote n\'a pas encore commencé');
                return;
            }
            const voteType = button.dataset.vote;
            socket.emit('vote', voteType);
          
          voteStatus.textContent = 'Vous avez voté pour ' + voteType;
        });
    });

    /**
     * Enable all vote buttons
     */
    function enableVoteButtons() {
        voteButtons.forEach(button => button.disabled = false);
    }

    /**
     * Disable all vote buttons
     */
    function disableVoteButtons() {
        voteButtons.forEach(button => button.disabled = true);
    }

    disableVoteButtons();
});