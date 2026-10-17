

class WebSocketClient {
    constructor(wsUrl, clientId, messageCallback = null) {
        this.wsUrl = wsUrl;
        this.clientId = clientId;
        this.ws = null;
        this.running = false;
        this.messageCallback = messageCallback;
    }

    connect() {
        this.ws = new WebSocket(this.wsUrl);

        this.ws.onopen = () => {
            this.running = true;
            this.send(`${this.clientId} ENTERS`);
            console.log(`${this.clientId} connecté à ${this.wsUrl}`);
        };

        this.ws.onmessage = (event) => {
            if (this.messageCallback) {
                this.messageCallback(this.clientId, event.data);
            }
        };

        this.ws.onclose = () => {
            this.running = false;
            console.log(`${this.clientId} déconnecté.`);
        };

        this.ws.onerror = (error) => {
            console.error(`Erreur WebSocket (${this.clientId}) :`, error);
        };
    }

    send(message) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(message);
        }
    }

    setMessageCallback(callback) {
        this.messageCallback = callback;
    }

    close() {
        if (this.ws) {
            this.ws.close();
        }
    }
}

export default WebSocketClient;