# Script de entrenamiento con YOLOv8
# Entrena un modelo de deteccion de objetos usando Transfer Learning

import os
import sys
from pathlib import Path

try:
    from ultralytics import YOLO
    import torch
except ImportError:
    print("ERROR: Instalar dependencias con: pip install ultralytics torch torchvision")
    sys.exit(1)


def crear_yaml_dataset():
    """Crear archivo de configuracion del dataset para YOLO"""
    
    yaml_content = """# Configuracion del dataset para entrenamiento
path: dataset  # Ruta raiz del dataset
train: .  # Ruta de entrenamiento (relativa a 'path')
val: .  # Ruta de validacion (relativa a 'path')

# Clases
names:
  0: persona
  1: perro
  2: celular
"""
    
    with open('dataset.yaml', 'w') as f:
        f.write(yaml_content)
    
    print("[CONFIGURACION] Archivo dataset.yaml creado")


def verificar_dataset():
    """Verificar que existan imagenes en el dataset"""
    
    clases = ['persona', 'perro', 'celular']
    dataset_path = Path('dataset')
    
    total_imagenes = 0
    
    for clase in clases:
        clase_path = dataset_path / clase
        if clase_path.exists():
            imagenes = list(clase_path.glob('*.jpg')) + list(clase_path.glob('*.png'))
            cantidad = len(imagenes)
            total_imagenes += cantidad
            print(f"[DATASET] Clase '{clase}': {cantidad} imagenes")
        else:
            print(f"[ADVERTENCIA] No existe carpeta para clase '{clase}'")
    
    if total_imagenes == 0:
        print("[ERROR] No hay imagenes en el dataset. Agregar imagenes antes de entrenar.")
        return False
    
    print(f"[DATASET] Total de imagenes: {total_imagenes}")
    return True


def entrenar_modelo():
    """Entrenar modelo YOLOv8 con el dataset"""
    
    print("\n" + "="*50)
    print("  INICIANDO ENTRENAMIENTO DEL MODELO")
    print("="*50 + "\n")
    
    # Verificar dataset
    if not verificar_dataset():
        return
    
    # Crear configuracion del dataset
    crear_yaml_dataset()
    
    try:
        # Cargar modelo preentrenado YOLOv8 (nano version para rapidez)
        print("[MODELO] Cargando YOLOv8n preentrenado...")
        modelo = YOLO('yolov8n.pt')
        
        # Configurar entrenamiento
        print("[ENTRENAMIENTO] Iniciando...")
        
        resultados = modelo.train(
            data='dataset.yaml',
            epochs=50,  # Numero de epocas
            imgsz=640,  # Tamaño de imagen
            batch=8,    # Tamaño del batch
            name='modelo_objetos',
            patience=10,  # Early stopping
            save=True,
            plots=True,
            verbose=True
        )
        
        # Guardar modelo entrenado
        modelo_entrenado = YOLO('runs/detect/modelo_objetos/weights/best.pt')
        modelo_entrenado.save('modelos/modelo_entrenado.pt')
        
        print("\n" + "="*50)
        print("  ENTRENAMIENTO COMPLETADO")
        print("  Modelo guardado en: modelos/modelo_entrenado.pt")
        print("="*50 + "\n")
        
    except Exception as e:
        print(f"[ERROR] Durante el entrenamiento: {str(e)}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    entrenar_modelo()
