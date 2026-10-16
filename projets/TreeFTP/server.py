from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer

def main():
    authorizer = DummyAuthorizer()

    authorizer.add_user("bouchra", "12345", r"C:\Users\bouam\m1\SR1\sr1_tp1_bouamama_bouchra", perm="elradfmwMT")

    authorizer.add_anonymous(r"C:\Users\bouam\m1\SR1\sr1_tp1_bouamama_bouchra")

    handler = FTPHandler
    handler.authorizer = authorizer

    server = FTPServer(("127.0.0.1", 2121), handler)
    server.serve_forever()

if __name__ == "__main__":
    main()
