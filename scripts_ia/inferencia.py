# Script de inferencia para deteccion de objetos
# Recibe una imagen y devuelve los objetos detectados

import os
import sys
from pathlib import Path

try:
    from ultralytics import YOLO
    import torch
except ImportError:
    print("NADA")
    sys.exit(1)


def cargar_modelo():
    """Cargar el modelo entrenado"""
    
    ruta_modelo = 'modelos/modelo_entrenado.pt'
    
    # Si no existe el modelo entrenado, usar modelo base
    if not os.path.exists(ruta_modelo):
        print("NADA", file=sys.stderr)
        return None
    
    try:
        modelo = YOLO(ruta_modelo)
        return modelo
    except Exception as e:
        print("NADA", file=sys.stderr)
        return None


def detectar_objetos(ruta_imagen):
    """Ejecutar deteccion en una imagen"""
    
    if not os.path.exists(ruta_imagen):
        print("NADA")
        return
    
    # Cargar modelo
    modelo = cargar_modelo()
    if modelo is None:
        print("NADA")
        return
    
    try:
        # Ejecutar prediccion
        resultados = modelo.predict(
            source=ruta_imagen,
            conf=0.25,  # Umbral de confianza
            verbose=False
        )
        
        # Extraer objetos detectados
        objetos_detectados = set()
        
        for resultado in resultados:
            if resultado.boxes is not None and len(resultado.boxes) > 0:
                for box in resultado.boxes:
                    clase_id = int(box.cls[0])
                    confianza = float(box.conf[0])
                    
                    # Mapear ID a nombre de clase
                    nombres_clases = {
                        0: 'Persona',
                        1: 'Perro',
                        2: 'Celular'
                    }
                    
                    if clase_id in nombres_clases:
                        nombre_clase = nombres_clases[clase_id]
                        objetos_detectados.add(nombre_clase)
        
        # Imprimir resultado
        if objetos_detectados:
            resultado_texto = ', '.join(sorted(objetos_detectados))
            print(resultado_texto)
        else:
            print("NADA")
    
    except Exception as e:
        print("NADA", file=sys.stderr)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("NADA")
        sys.exit(1)
    
    ruta_imagen = sys.argv[1]
    detectar_objetos(ruta_imagen)
