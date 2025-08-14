import torch
from torchvision.models.detection import ssd300_vgg16
from torchvision.transforms import functional as F
from PIL import Image
import time

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = ssd300_vgg16(pretrained=True).to(device)
model.eval()

CLASSES = [
    '__background__', 'person', 'bicycle', 'car', 'motorcycle', 'airplane', 'bus',
    'train', 'truck', 'boat', 'traffic light', 'fire hydrant', 'stop sign',
    'parking meter', 'bench', 'bird', 'cat', 'dog', 'horse', 'sheep', 'cow',
    'elephant', 'bear', 'zebra', 'giraffe', 'backpack', 'umbrella', 'handbag',
    'tie', 'suitcase', 'frisbee', 'skis', 'snowboard', 'sports ball', 'kite',
    'baseball bat', 'baseball glove', 'skateboard', 'surfboard', 'tennis racket',
    'bottle', 'wine glass', 'cup', 'fork', 'knife', 'spoon', 'bowl', 'banana',
    'apple', 'sandwich', 'orange', 'broccoli', 'carrot', 'hot dog', 'pizza',
    'donut', 'cake', 'chair', 'couch', 'potted plant', 'bed', 'dining table',
    'toilet', 'tv', 'laptop', 'mouse', 'remote', 'keyboard', 'cell phone',
    'microwave', 'oven', 'toaster', 'sink', 'refrigerator', 'book', 'clock',
    'vase', 'scissors', 'teddy bear', 'hair drier', 'toothbrush', 'car', 'sofa'
]
# Detect objects using SSD model
# This function takes an image path, processes the image, and returns detected objects with their bounding boxes, labels, and confidence scores.
def detect_objects_ssd(image_path):
    print("▶ SSD wird verwendet")
    start_time = time.time()
    image = Image.open(image_path).convert("RGB")
    image_tensor = F.to_tensor(image).unsqueeze(0).to(device)

    with torch.no_grad():
        predictions = model(image_tensor)[0]

    duration = (time.time() - start_time) * 1000  # ms

    result = []
    for box, label_idx, score in zip(predictions["boxes"], predictions["labels"], predictions["scores"]):
        if score.item() > 0.5:
            box = box.tolist()
            idx = int(label_idx - 1)# ohne minus eins gibt es ein offset von eins und zeigt statt hund eine katze
            if 0 <= idx < len(CLASSES):
                label = CLASSES[idx]
            else:
                label = f"unknown_{idx}"
            result.append({
                "bbox": box,
                "label": label,
                "confidence": float(score)
            })

    print(f"✅ SSD fertig in {round(duration,2)}ms, {len(result)} Objekte gefunden")
    return result, duration
