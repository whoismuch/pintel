import random
COCOCategories = {
    0: 'N/A', 1: 'person', 2: 'bicycle', 3: 'car', 4: 'motorcycle', 5: 'airplane', 6: 'bus', 7: 'train', 8: 'truck', 9: 'boat', 10: 'traffic light',
    11: 'fire hydrant', 12: 'N/A', 13: 'stop sign', 14: 'parking meter', 15: 'bench', 16: 'bird', 17: 'cat', 18: 'dog', 19: 'horse', 20: 'sheep',
    21: 'cow', 22: 'elephant', 23: 'bear', 24: 'zebra', 25: 'giraffe', 26: 'N/A', 27: 'backpack', 28: 'umbrella', 29: 'N/A', 30: 'N/A',
    31: 'handbag', 32: 'tie', 33: 'suitcase', 34: 'frisbee', 35: 'skis', 36: 'snowboard', 37: 'sports ball', 38: 'kite', 39: 'baseball bat', 40: 'baseball glove',
    41: 'skateboard', 42: 'surfboard', 43: 'tennis racket', 44: 'bottle', 45: 'N/A', 46: 'wine glass', 47: 'cup', 48: 'fork', 49: 'knife', 50: 'spoon',
    51: 'bowl', 52: 'banana', 53: 'apple', 54: 'sandwich', 55: 'orange', 56: 'broccoli', 57: 'carrot', 58: 'hot dog', 59: 'pizza', 60: 'donut',
    61: 'cake', 62: 'chair', 63: 'couch', 64: 'potted plant', 65: 'bed', 66: 'N/A', 67: 'dining table', 68: 'N/A', 69: 'N/A', 70: 'toilet',
    71: 'N/A', 72: 'tv', 73: 'laptop', 74: 'mouse', 75: 'remote', 76: 'keyboard', 77: 'cell phone', 78: 'microwave', 79: 'oven', 80: 'toaster',
    81: 'sink', 82: 'refrigerator', 83: 'N/A', 84: 'book', 85: 'clock', 86: 'vase', 87: 'scissors', 88: 'teddy bear', 89: 'hair drier', 90: 'toothbrush',
}

BaseColors = {
    'red': [255,0,0], 'blue': [0,0,255],
    'green': [0,255,0], 'yellow': [255,255,0],
    'orange': [255,165,0],
    'purple': [255,0,255], 'brown': [165,42,42],
    'black': [0,0,0], 'white': [255,255,255]
}
ColorNames = list(BaseColors.keys())

def choose_tags():
    content_tags = [COCOCategories[random.randint(0, len(COCOCategories)-1)] for _ in range(random.randint(1, 4))]
    color_tags = [ColorNames[random.randint(0, len(BaseColors)-1)] for _ in range(3)]
    if 'N/A' in content_tags: content_tags.remove('N/A')
    return list(set([*color_tags, *content_tags]))