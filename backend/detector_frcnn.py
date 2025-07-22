import torch
from torchvision import transforms
from PIL import Image
from torchvision.models.detection import fasterrcnn_resnet50_fpn
import time

COCO_INSTANCE_CATEGORY_NAMES = [
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

model = fasterrcnn_resnet50_fpn(pretrained=True)
model.eval()

def detect_objects_frcnn(image_path: str):
    print("▶ Faster R-CNN wird verwendet")
    start_time = time.time()
    image = Image.open(image_path).convert("RGB")
    transform = transforms.ToTensor()
    image_tensor = transform(image)
    with torch.no_grad():
        predictions = model([image_tensor])[0]

    duration = (time.time() - start_time) * 1000  # ms

    result = []
    for i in range(len(predictions["boxes"])):
        score = predictions["scores"][i].item()
        label_idx = int(predictions["labels"][i].item())
        print(f"Detection {i}: label_idx={label_idx}, score={score}")
        if score >= 0.5:
            if 0 <= label_idx < len(COCO_INSTANCE_CATEGORY_NAMES):
                label = COCO_INSTANCE_CATEGORY_NAMES[label_idx - 1]
            else:
                label = f"unknown_{label_idx}"
            box = predictions["boxes"][i].tolist()
            result.append({
                "label": label,
                "confidence": round(score, 2),
                "bbox": box
            })

    print(f"✅ Faster R-CNN fertig in {round(duration,2)}ms, {len(result)} Objekte gefunden")
    return result, duration
