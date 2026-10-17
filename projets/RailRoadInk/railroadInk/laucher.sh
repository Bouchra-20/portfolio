#!/bin/bash

echo "Autorisation d'exécution du réflecteur"
chmod +x ./reflector
echo "Activation du réflecteur sur le port 10000"
./reflector --port 10000 &
PID=$!
echo "Lancement de la partie"
make game
echo "Arrêt du reflecteur"
kill $PID