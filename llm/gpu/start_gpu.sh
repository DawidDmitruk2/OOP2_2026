#!/bin/bash
echo "=== PRZYGOTOWANIE ŚRODOWISKA GPU (VULKAN) ==="
echo "Kopiowanie plików z USB na dysk lokalny (dla szybkości)..."
cp -r . ~/Downloads/llm_gpu_workspace
cd ~/Downloads/llm_gpu_workspace

echo "Nadawanie uprawnień do uruchomienia..."
chmod +x llama-server

echo "Uruchamianie serwera Qwen na KARCIE GRAFICZNEJ..."
# Flaga -ngl 99 przerzuca całą sztuczną inteligencję do pamięci VRAM na karcie
./llama-server -m qwen2.5-coder-3b-instruct-q4_k_m.gguf -c 8192 -ngl 99