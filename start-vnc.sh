#!/bin/bash

export DISPLAY=:1
export RESOLUTION=1920x1080

mkdir -p ~/.vnc

echo "Iniciando servidor VNC..."
vncserver :1 -geometry $RESOLUTION -depth 24 -localhost no -SecurityTypes None --I-KNOW-THIS-IS-INSECURE

echo "Esperando a que VNC inicie..."
sleep 3

echo "Iniciando noVNC (interfaz web)..."
/opt/noVNC/utils/novnc_proxy --vnc localhost:5901 --listen 80 &

echo "Esperando a que noVNC inicie..."
sleep 2

echo "Iniciando gestor de ventanas..."
DISPLAY=:1 fluxbox &

sleep 2

echo "Iniciando aplicacion Pong Evolved..."
cd /app
DISPLAY=:1 mvn javafx:run

tail -f /dev/null
