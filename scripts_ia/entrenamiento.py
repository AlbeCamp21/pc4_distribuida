# Script de entrenamiento con YOLOv8 - Clasificacion
# Entrena un modelo de clasificacion de imagenes usando Transfer Learning

import os
import sys
from pathlib import Path

try:
    from ultralytics import YOLO
    import torch
except ImportError:
    print("ERROR: Instalar dependencias con: pip install ultralytics torch torchvision")
    sys.exit(1)


def verificar_dataset():
    """Verificar que existan imagenes en el dataset"""
    
    clases = ['persona', 'perro', 'celular']
    dataset_path = Path('dataset')
    
    total_imagenes = 0
    
    for clase in clases:
        clase_path = dataset_path / clase
        if clase_path.exists():
            imagenes = list(clase_path.glob('*.jpg')) + list(clase_path.glob('*.png')) + list(clase_path.glob('*.jpeg'))
            cantidad = len(imagenes)
            total_imagenes += cantidad
            print(f"[DATASET] Clase '{clase}': {cantidad} imagenes")
        else:
            print(f"[ADVERTENCIA] No existe carpeta para clase '{clase}'")
    
    if total_imagenes == 0:
        print("[ERROR] No hay imagenes en el dataset. Agregar imagenes antes de entrenar.")
        return False
    
    print(f"[DATASET] Total de imagenes: {total_imagenes}")
    
    if total_imagenes < 30:
        print("[ADVERTENCIA] Se recomiendan al menos 30 imagenes por clase para mejor precision")
    
    return True


def entrenar_modelo():
    """Entrenar modelo YOLOv8-cls para clasificacion de imagenes"""
    
    print("\n" + "="*50)
    print("  INICIANDO ENTRENAMIENTO - CLASIFICACION")
    print("="*50 + "\n")
    
    # Verificar dataset
    if not verificar_dataset():
        return
    
    try:
        # Cargar modelo de CLASIFICACION YOLOv8 (nano version)
        print("[MODELO] Cargando YOLOv8n-cls preentrenado para clasificacion...")
        modelo = YOLO('yolov8n-cls.pt')
        
        # Configurar entrenamiento
        print("[ENTRENAMIENTO] Iniciando entrenamiento de clasificacion...")
        print("[INFO] El dataset debe estar en: dataset/persona/, dataset/perro/, dataset/celular/")
        
        resultados = modelo.train(
            data='dataset',  # Carpeta raiz con subcarpetas por clase
            epochs=50,       # Numero de epocas
            imgsz=224,       # Tamaño de imagen para clasificacion
            batch=16,        # Tamaño del batch
            name='modelo_clasificacion',
            patience=10,     # Early stopping
            save=True,
            plots=True,
            verbose=True,
            workers=4
        )
        
        # Crear carpeta de modelos si no existe
        os.makedirs('modelos', exist_ok=True)
        
        # Guardar modelo entrenado
        ruta_mejor = 'runs/classify/modelo_clasificacion/weights/best.pt'
        if os.path.exists(ruta_mejor):
            # Copiar a carpeta modelos
            import shutil
            shutil.copy(ruta_mejor, 'modelos/modelo_entrenado.pt')
            print(f"[OK] Modelo copiado a: modelos/modelo_entrenado.pt")
        
        print("\n" + "="*50)
        print("  ENTRENAMIENTO COMPLETADO")
        print("  Modelo guardado en: modelos/modelo_entrenado.pt")
        print(f"  Resultados en: runs/classify/modelo_clasificacion/")
        print("="*50 + "\n")
        
    except Exception as e:
        print(f"[ERROR] Durante el entrenamiento: {str(e)}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    entrenar_modelo()
