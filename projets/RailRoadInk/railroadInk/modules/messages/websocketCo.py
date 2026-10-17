import websocket
import threading

class WebSocketClient:
    def __init__(self, ws_url, client_id, message_callback=None):
        self.ws_url = ws_url
        self.client_id = client_id
        self.ws = None
        self.running = False
        self.message_callback = message_callback
        self.listen_thread = None

    def connect(self):
        """Établit la connexion WebSocket et démarre l'écoute en parallèle."""
        try:
            self.ws = websocket.WebSocket()
            self.ws.connect(self.ws_url)
            self.running = True

            self.send(f"{self.client_id} ENTERS")

            # Démarrer un thread pour écouter les messages en continu
            self.listen_thread = threading.Thread(target=self._listen, daemon=True)
            self.listen_thread.start()
            print(f"{self.client_id} connecté à {self.ws_url}")
        except Exception as e:
            print(f"Erreur de connexion ({self.client_id}) : {e}")
            self.close()

    def send(self, message):
        """Envoie un message via WebSocket."""
        if not message:
            return
            
        try:
            if self.ws:
                self.ws.send(message)
        except Exception as e:
            print(f"Erreur d'envoi ({self.client_id}) : {e}")
            self.close()

    def set_message_callback(self, callback):
        """
        Définit une fonction callback pour traiter les messages reçus.

        :param callback: Fonction prenant un message en paramètre
        """
        if callable(callback):
            self.message_callback = callback

    def _listen(self):
        """Écoute les messages du serveur et déclenche le callback si défini."""
        while self.running:
            try:
                message = self.ws.recv()
                if message and self.message_callback:
                    try:
                        self.message_callback(self.client_id, message)
                    except Exception as e:
                        print(f"Erreur dans le callback ({self.client_id}) : {e}")
            except websocket.WebSocketConnectionClosedException:
                print(f"{self.client_id} déconnecté.")
                self.close()
            except Exception as e:
                print(f"Erreur WebSocket ({self.client_id}) : {e}")
                self.close()

    def close(self):
        """Ferme la connexion WebSocket proprement."""
        self.running = False
        if self.ws:
            try:
                self.ws.close()
            except:
                pass
            self.ws = None
        print(f"{self.client_id} déconnecté.")