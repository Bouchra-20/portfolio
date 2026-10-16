from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer
import os
import argparse

def main():
    parser = argparse.ArgumentParser(description="Serveur FTP FlopBox")
    parser.add_argument("--port", type=int, default=2121, help="Port d'écoute (default: 2121)")
    args = parser.parse_args()

    root_path = "C:\\Users\\bouam\\m1\\SR2\\sr2_tp2_bouamama_bouchra\\FlopBox\\server2\\FTP_DATA"

    base_path = os.path.join(root_path)

    if not os.path.exists(base_path):
        os.makedirs(base_path)
        print(f"Dossier créé pour le port {args.port} : {base_path}")

    authorizer = DummyAuthorizer()

    authorizer.add_user("user", "12345", base_path, perm="elradfmwMT")
    authorizer.add_user("admin", "678910", base_path, perm="elradfmwMT")
    authorizer.add_anonymous(base_path)

    handler = FTPHandler
    handler.authorizer = authorizer
    handler.encoding = 'utf-8'
    handler.passive_ports = range(60000, 61000)

    server = FTPServer(("0.0.0.0", args.port), handler)

    print(f"=== Serveur FTP FlopBox démarré ===")
    print(f"Adresse : 127.0.0.1:{args.port}")
    print(f"Dossier : {base_path}")
    print(f"Identifiants : user / 12345")
    print("===================================")

    server.serve_forever()

if __name__ == "__main__":
    main()