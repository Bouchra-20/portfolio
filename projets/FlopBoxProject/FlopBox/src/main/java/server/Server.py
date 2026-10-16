from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer
import os

def main():
    base_path = "C:\\Users\\bouam\\m1\\SR2\\sr2_tp2_bouamama_bouchra\\FlopBox\\src\\main\\java\\server\\FTP_DATA"
    if not os.path.exists(base_path):
        os.makedirs(base_path)
        print(f"ATTENTION : Le dossier était absent, il a été créé : {base_path}")

    authorizer = DummyAuthorizer()

    authorizer.add_user("bouchra", "12345", base_path, perm="elradfmwMT")
    authorizer.add_user("admin", "678910", base_path, perm="elradfmwMT")

    authorizer.add_anonymous(base_path)

    handler = FTPHandler
    handler.authorizer = authorizer
    handler.encoding = 'utf-8'

    handler.passive_ports = range(60000, 61000)

    print(f"Serveur FTP démarré sur 127.0.0.1:2121")
    print(f"Dossier racine : {base_path}")

    server = FTPServer(("127.0.0.1", 2121), handler)
    server.serve_forever()

if __name__ == "__main__":
    main()