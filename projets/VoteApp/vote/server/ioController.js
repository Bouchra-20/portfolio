/**
 * Controller class that handles Socket.IO connections and voting logic
 */
export default class IOController {
    #io;
    #clients;
    #admin;
    #voteEnCours;
    #votes;
    #currentVoteSubject;
    #votesParUtilisateur;

    /**
     * Creates a new IOController instance
     * @param {SocketIO.Server} io - The Socket.IO server instance
     */
    constructor(io) {
        this.#io = io;
        this.#clients = new Map();
        this.#admin = null;
        this.#voteEnCours = false;
        this.#votes = {
            pour: 0,
            contre: 0,
            nppv: 0,
            abstention: 0
        };
        this.#currentVoteSubject = '';
        this.#votesParUtilisateur = new Map();
    }
  
    /**
     * Registers a new socket connection
     * @param {SocketIO.Socket} socket - The socket to register
     */
    registerSocket(socket) {
        console.log(`new connection with id ${socket.id}`);
        this.setupListeners(socket);
    }
  
    /**
     * Sets up event listeners for a socket
     * @param {SocketIO.Socket} socket - The socket to setup listeners for
     */
    setupListeners(socket) {
        socket.emit('Hello!', 'Hello!')
        socket.on( 'adminMessage'  , () => this.adminMessage(socket) );
        socket.on('votantMessage', user => this.sendMessage(socket, user) );
        socket.on('disconnect' , () => this.leave(socket) );
        socket.on('start vote', (subject) => {
            if (socket === this.#admin) {
                console.log('Vote started by admin');
                this.#voteEnCours = true;
                this.#currentVoteSubject = subject;
                this.#votes = { pour: 0, contre: 0, nppv: 0, abstention: 0 };   
                this.#votesParUtilisateur.clear();
                this.#io.emit('vote start', subject);
                this.#admin.emit('update votants', this.#clients.size, this.#votesParUtilisateur.size);
            }
        });

        socket.on('end vote', (data) => {
            if (socket === this.#admin) {
                console.log('Vote ended by admin');
                this.#voteEnCours = false;
                this.#io.emit('vote ended', data);
            }
        });

        socket.on('vote', (voteType) => {
            if (this.#voteEnCours && this.#clients.has(socket.id)) {
                console.log(`Vote reçu de ${socket.id}: ${voteType}`);
                const ancienVote = this.#votesParUtilisateur.get(socket.id);
                if (ancienVote) {
                    this.#votes[ancienVote]--;
                }   
                this.#votes[voteType]++;
                this.#votesParUtilisateur.set(socket.id, voteType);
                if (this.#admin) {
                    this.#admin.emit('vote update', this.#votes, socket.id);
                    this.#admin.emit('update votants', this.#clients.size, this.#votesParUtilisateur.size);
                }
            }
        });
    }

    /**
     * Handles admin connection attempts
     * @param {SocketIO.Socket} socket - The socket attempting to connect as admin
     */
    adminMessage(socket) {
        if (this.#admin === null) {
            this.#admin = socket;
            socket.emit('accepted'); 
            console.log(`Admin connecté: ${socket.id}`);
            socket.emit('update votants', this.#clients.size, this.#votesParUtilisateur.size);
        } else {
            socket.emit('rejected'); 
            console.log(`Connexion admin refusée: ${socket.id}`);
        }
    }
  
    /**
     * Handles new user connections and messages
     * @param {SocketIO.Socket} socket - The socket of the connecting user
     * @param {Object} user - The user information
     */
    sendMessage(socket, user) {
        const userName = user && user.name ? user.name : 'Anonymous';

        console.log(`Message received from ${userName} (id : ${socket.id}) : ${user}`);
        this.#clients.set(socket.id, userName);
        socket.broadcast.emit('new user', userName);
        this.#io.emit('update votants', this.#clients.size, this.#votesParUtilisateur.size);

        if (this.#voteEnCours) {
            socket.emit('vote start', this.#currentVoteSubject);
        }
    }
  
    /**
     * Handles socket disconnections
     * @param {SocketIO.Socket} socket - The socket that is disconnecting
     */
    leave(socket) {
        if (this.#admin === socket) {
            this.#admin = null;
            this.#voteEnCours = false;
            this.#io.emit('vote end');
        } else {
            const userName = this.#clients.get(socket.id) || 'unknown';
            console.log(`disconnection from ${socket.id} (user : ${userName})`);
            this.#clients.delete(socket.id);
            this.#io.emit('update votants', this.#clients.size, this.#votesParUtilisateur.size);
        }
    }
  }
