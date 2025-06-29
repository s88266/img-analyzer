import tensorflow as tf
import numpy as np
from keras.applications.mobilenet_v2 import preprocess_input, decode_predictions, MobileNetV2
from keras.preprocessing import image as keras_image
import time

model = MobileNetV2(weights='imagenet')

def detect_objects_tf(image_path: str)-> list:
    start_time = time.time()
    
    img = keras_image.load_img(image_path, target_size=(224, 224))
    img_array = keras_image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = preprocess_input(img_array)

    preds = model.predict(img_array)
    decoded = decode_predictions(preds, top=3)[0]  # Top 3 Vorhersagen

    duration = (time.time() - start_time) * 1000  # Dauer in ms

    result = []
    for label_id, label, confidence in decoded:
        result.append({
            "label": label,
            "confidence": float(confidence),
            "bbox": [0, 0, 0, 0] 
        })

    return result, duration
