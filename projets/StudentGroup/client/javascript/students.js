const displayMessage = msg => document.getElementById('message').textContent = msg;
const ajoutButton = document.getElementById('createUpdate');
const supprimeButton = document.getElementById('clear');


let isEditing = false;
let editingStudentId = null;

/**
 * Charge et affiche la liste des étudiants depuis l'API
 */
async function chargerEtudiants() {
    try{
        const response = await fetch('/students');
        if (!response.ok) throw new Error('Erreur lors du chargement des étudiants');
        const etudiants = await response.json();
        
        const list = document.getElementById('list');
        list.innerHTML = '';
        
        etudiants.forEach(etudiant => {
            const li = document.createElement('li');
            const studentInfo = document.createElement('span');
            li.textContent = `${etudiant.nom} ${etudiant.prenoms.join(', ')} (${etudiant.numero})`;

            const buttonContainer = document.createElement('div');
            buttonContainer.className = 'button-group';

            const editButton = document.createElement('button');
            editButton.textContent = 'U';
            editButton.addEventListener('click', () => preparerModification(etudiant));
            buttonContainer.appendChild(editButton);

            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'X';
            deleteButton.addEventListener('click', () => supprimeEtudiant(etudiant._id));
            buttonContainer.appendChild(deleteButton);

            li.appendChild(studentInfo);
            li.appendChild(buttonContainer);
            list.appendChild(li);
        });
    }catch(error){
        displayMessage(`Erreur : ${error.message}`);
    }
}

/**
 * Ajoute un nouvel étudiant ou met à jour un étudiant existant
 */
async function ajouterOuModifierEtudiant() {
    const numero = document.getElementById('number').value.trim();
    const nom = document.getElementById('name').value.trim();
    const prenoms = document.getElementById('firstnames').value.split(',').map(p => p.trim());

    if (!numero) {
        displayMessage('Erreur : Le numéro étudiant est requis.');
        return;
    }
    if (!/^\d+$/.test(numero)) {
        displayMessage('Erreur : Le numéro étudiant doit être un nombre entier.');
        return;
    }
    if (!nom) {
        displayMessage('Erreur : Le nom est requis.');
        return;
    }
    if (prenoms.length === 0 || prenoms.every(p => p === '')) {
        displayMessage('Erreur : Le prénom est requis.');
        return;
    }

    try{
        let response;
        if (isEditing) {
            response = await fetch(`/students/${editingStudentId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ numero, nom, prenoms })
            });
        } else {
            response = await fetch('/students', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ numero : numero, nom : nom, prenoms : prenoms })
            });
        }

        const result = await response.json();
        if (!response.ok) {
            displayMessage(`Erreur : ${result.message}`);
            return;
        }

        displayMessage(result.message || (isEditing ? 'Étudiant mis à jour avec succès' : 'Étudiant ajouté avec succès'));
        chargerEtudiants();
        isEditing = false;
        editingStudentId = null;
        ajoutButton.textContent = 'Créer';
        viderChamps();

    }catch(error){
        displayMessage(`Erreur : ${error.message}`);
    }
}


/**
 * Supprime un étudiant à partir de son ID
 */
async function supprimeEtudiant(id) {
    try{
        const response = await fetch(`/students/${id}`, { method: 'DELETE'});
        if (!response.ok) {
            throw new Error('Erreur lors de la suppression.');
        }

        const result = await response.json();
        displayMessage(result.message || 'Étudiant supprimé avec succès');    
        chargerEtudiants();
        viderChamps();
    }catch(error){
        displayMessage(`Erreur : ${error.message}`);
    }

}

/**
 * Prépare les champs pour la modification d'un étudiant
 */
function preparerModification(etudiant) {
    document.getElementById('number').value = etudiant.numero;
    document.getElementById('name').value = etudiant.nom;
    document.getElementById('firstnames').value = Array.isArray(etudiant.prenoms) ? etudiant.prenoms.join(',') : etudiant.prenoms;
    
    isEditing = true;
    editingStudentId = etudiant._id;
    ajoutButton.textContent = 'Mettre à jour';
    displayMessage('Modifiez les informations et cliquez sur "Mettre à jour"');
}

/**
 * Vide tous les champs de saisie et réinitialise le mode édition
 */
function viderChamps() {
    document.getElementById('number').value = '';
    document.getElementById('name').value = '';
    document.getElementById('firstnames').value = '';
    if (isEditing) {
        isEditing = false;
        editingStudentId = null;
        ajoutButton.textContent = 'Créer';
    }
}

ajoutButton.addEventListener('click', ajouterOuModifierEtudiant);
supprimeButton.addEventListener('click', viderChamps);
chargerEtudiants();
