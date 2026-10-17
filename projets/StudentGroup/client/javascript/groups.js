document.addEventListener('DOMContentLoaded', () => {
    const studentList = document.getElementById('students');
    const messageDiv = document.getElementById('message');
    const groupItems = document.querySelectorAll('.group-item');
    let currentGroup = '';

    fetchStudents('');

    groupItems.forEach(item => {
        item.addEventListener('click', () => {
            groupItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            currentGroup = item.dataset.group;
            fetchStudents(currentGroup);
        });
    });

    /**
     * Récupère et affiche les étudiants :
     * - soit tous ceux sans groupe si groupNumber est vide
     * - soit ceux appartenant à un groupe spécifique
     */
    async function fetchStudents(groupNumber = '') {
        try {
            const response = await fetch(`/groups${groupNumber ? `/${groupNumber}` : ''}`);
            if (!response.ok) throw new Error('Erreur lors de la récupération des données');
            const students = await response.json();
            studentList.innerHTML = '';

            students.forEach(student => {

                if (groupNumber && (!student.etudiantId || !student.etudiantId.nom)) {
                    console.warn('Entrée invalide trouvée dans le groupe :', student);
                    return;
                }
                if (!groupNumber && (!student.nom || !student.numero)) {
                    console.warn('Entrée invalide trouvée dans aucun groupe :', student);
                    return;
                }
                
                const li = document.createElement('li');
                const studentInfo = student.etudiantId 
                    ? `${student.etudiantId.nom} ${student.etudiantId.prenoms} (${student.etudiantId.numero})`
                    : `${student.nom} ${student.prenoms} (${student.numero})`;
                
                li.textContent = studentInfo;

                if (groupNumber) {
                    const removeButton = document.createElement('button');
                    removeButton.textContent = 'X';
                    removeButton.addEventListener('click', () => removeFromGroup(student.etudiantId._id));
                    li.appendChild(removeButton);
                } else {
                    const circleContainer = document.createElement('span');
                    circleContainer.className = 'group-circles';
                    
                    for (let i = 1; i <= 6; i++) {
                        const circle = document.createElement('span');
                        circle.className = 'group-circle';
                        circle.textContent = i;
                        circle.dataset.group = i;
                        circle.addEventListener('click', () => addToGroup(student._id, i));
                        circleContainer.appendChild(circle);
                    }
                    li.appendChild(circleContainer);
                }
                studentList.appendChild(li);
            });
        } catch (error) {
            messageDiv.textContent = error.message;
            setTimeout(() => messageDiv.textContent = '', 5000);
        }
    }

    /**
     * Ajoute un étudiant à un groupe
     * @param {string} etudiantId - L'identifiant MongoDB de l'étudiant
     * @param {number} groupe - Le numéro de groupe (1 à 6)
     */
    async function addToGroup(etudiantId, groupe) {
        try {
            const response = await fetch('/groups', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ etudiantId, groupe })
            });

            if (!response.ok) throw new Error('Erreur lors de l\'ajout au groupe');
            messageDiv.textContent = 'Étudiant ajouté au groupe avec succès';
            fetchStudents('');
        } catch (error) {
            messageDiv.textContent = error.message;
        } finally {
            setTimeout(() => messageDiv.textContent = '', 5000);
        }
    }

    /**
     * Supprime un étudiant d’un groupe
     * @param {string} studentId - L'identifiant de l'étudiant à retirer du groupe
     */
    async function removeFromGroup(studentId) {
        try {
            const response = await fetch(`/groups/${studentId}`, {
                method: 'DELETE'
            });

            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || 'Erreur lors de la suppression du groupe');
            }
            messageDiv.textContent = 'Étudiant retiré du groupe avec succès';
            fetchStudents(currentGroup);
        } catch (error) {
            console.error('Erreur dans removeFromGroup :', error);
            messageDiv.textContent = error.message;
        } finally {
            setTimeout(() => messageDiv.textContent = '', 5000);
        }
    }
});