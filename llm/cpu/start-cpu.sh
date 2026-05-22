#!/bin/bash
echo "=== PRZYGOTOWANIE ŚRODOWISKA CPU ==="
echo "Kopiowanie plików z USB na dysk lokalny (dla szybkości)..."
cp -r . ~/Downloads/llm_cpu_workspace
cd ~/Downloads/llm_cpu_workspace

echo "Nadawanie uprawnień do uruchomienia..."
chmod +x llama-server

echo "Uruchamianie serwera Qwen na PROCESORZE (CPU)..."
# Flaga -ngl 0 wymusza CPU, -c 8192 daje duże bezpieczne okno pamięci
./llama-server -m qwen2.5-coder-3b-instruct-q4_k_m.gguf -c 8192 -ngl 0