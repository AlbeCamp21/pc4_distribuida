# Script de inferencia para clasificacion de imagenes
# Recibe una imagen y devuelve la clase detectada

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
    """Cargar el modelo de clasificacion entrenado"""
    
    ruta_modelo = 'modelos/modelo_entrenado.pt'
    
    # Si no existe el modelo entrenado
    if not os.path.exists(ruta_modelo):
        print("NADA", file=sys.stderr)
        return None
    
    try:
        modelo = YOLO(ruta_modelo)
        return modelo
    except Exception as e:
        print("NADA", file=sys.stderr)
        return None


def clasificar_imagen(ruta_imagen):
    """Ejecutar clasificacion en una imagen"""
    
    if not os.path.exists(ruta_imagen):
        print("NADA")
        return
    
    # Cargar modelo
    modelo = cargar_modelo()
    if modelo is None:
        print("NADA")
        return
    
    try:
        # Ejecutar prediccion de clasificacion
        resultados = modelo.predict(
            source=ruta_imagen,
            verbose=False
        )
        
        # Extraer clase predicha
        if resultados and len(resultados) > 0:
            resultado = resultados[0]
            
            # En clasificacion, probs contiene las probabilidades por clase
            if hasattr(resultado, 'probs') and resultado.probs is not None:
                # Obtener clase con mayor probabilidad
                clase_id = int(resultado.probs.top1)
                confianza = float(resultado.probs.top1conf)
                
                # Mapear ID a nombre de clase
                nombres_clases = {
                    0: 'Celular',
                    1: 'Perro', 
                    2: 'Persona'
                }
                
                # Umbral de confianza minimo
                if confianza >= 0.3:
                    if clase_id in nombres_clases:
                        nombre_clase = nombres_clases[clase_id]
                        print(nombre_clase)
                        return
                
        print("NADA")
    
    except Exception as e:
        print("NADA", file=sys.stderr)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("NADA")
        sys.exit(1)
    
    ruta_imagen = sys.argv[1]
    clasificar_imagen(ruta_imagen)
