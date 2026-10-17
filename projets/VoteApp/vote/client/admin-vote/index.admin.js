/**
 * Import chart creation utility function
 */
import { createOrUpdateChart } from '../shared/chartConfig.js';

/**
 * Initialize socket connection to server
 */
const socket = io('http://localhost:8080');

/**
 * Track number of voters and connected users
 */
let nombreVotants = 0;
let nombreConnectes = 0;

/**
 * Reference to the chart instance
 */
let myChart = null;

/**
 * Updates the display of voter and connection counts
 */
function updateNombre() {
  const elementVotants = document.getElementById('nombre-votants');
  const elementConnectes = document.querySelectorAll('#nombre-connectes');
  
  if (elementVotants && elementConnectes) {
    elementVotants.textContent = nombreVotants;
    elementConnectes.forEach(element => {
      element.textContent = nombreConnectes;
    });
  }
}

/**
 * Socket event handlers
 */
socket.on('Hello!', () => {
  console.log('Hello reçu du serveur');
  socket.emit('adminMessage','Hi! I\'m admin');
});

socket.on('disconnectUser', () => {
  console.log('Utilisateur déconnect');
  nombreConnectes--;
  updateNombre();
});

socket.on('update votants', (connectes, votants) => {
  nombreConnectes = connectes;
  nombreVotants = votants;
  updateNombre();
});

socket.on('accepted', () => {
  console.log('Vous êtes administrateur');
 
  document.getElementById('rejected').style.display = 'none';

  document.getElementById('admin-controls').style.display = 'block';
});

socket.on('rejected', () => {
  console.log('Connexion admin refusée'); 
  document.getElementById('rejected').style.display = 'block';
  document.getElementById('admin-controls').style.display = 'none';
});

/**
 * Sets up event listeners for vote control buttons
 */
const setup = () => {
  document.getElementById('Demarrer le vote').addEventListener('click', demarrer);
  document.getElementById('Clore vote').addEventListener('click', cloreVote);
}

/**
 * Initialize chart and setup on page load
 */
document.addEventListener('DOMContentLoaded', () => {
    const emptyData = {
        votes: { pour: 0, contre: 0, nppv: 0, abstention: 0 },
        subject: document.getElementById('text du vote').value
    };
    myChart = createOrUpdateChart(null, emptyData, 'myChart');
    myChart.canvas.style.display = "none";
    setup();
});

/**
 * Starts a new vote
 */
const demarrer = () => {
  const emptyData = {
    votes: [0, 0, 0, 0],
    subject: document.getElementById('text du vote').value
  };
  myChart = createOrUpdateChart(myChart, emptyData, 'myChart');

  document.getElementById('Demarrer le vote').style.display = "none";
  document.getElementById('Clore vote').style.display = "block";

  nombreVotants = 0;

  socket.emit('start vote', emptyData.subject);
  myChart.canvas.style.display = "block";
}

/**
 * Ends the current vote
 */
const cloreVote = () => {  
  const finalData = {
    votes: myChart.data.datasets[0].data,
    subject: document.getElementById('text du vote').value
};
  socket.emit('end vote', finalData);
  document.getElementById('Demarrer le vote').style.display = "block";
  document.getElementById('Clore vote').style.display = "none";
  const votantInfo = document.getElementById('votant-info');
  votantInfo.textContent = `Vote est clos`;
}

/**
 * Updates chart when new votes are received
 * @param {Object} votes - Vote counts for each option
 * @param {string} socketId - ID of the socket that voted
 */
socket.on('vote update', (votes, socketId) => {
  if (!myChart) {
    console.log('Chart non initialisé!');
    return;
}

try {
  const dataWithSubject = {
    votes: [votes.pour, votes.contre, votes.nppv, votes.abstention],
    subject: document.getElementById('text du vote').value
  };
    myChart = createOrUpdateChart(myChart, dataWithSubject, 'myChart');
    console.log('Chart mis à jour avec succès');
    const votantInfo = document.getElementById('votant-info');
        if (votantInfo) {
            votantInfo.textContent = `Vote de : ${socketId}`;
        }
} catch (error) {
    console.error('Erreur mise à jour chart:', error);
}
});
