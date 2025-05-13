import insightface
import numpy as np
from PIL import Image
import os
import shutil

SELFIE_DIR = 'C:/Users/KIIT/Desktop/facefinder2/uploads/selfie'
PHOTOS_DIR = 'C:/Users/KIIT/Desktop/facefinder2/uploads/photos'
RESULTS_DIR = 'C:/Users/KIIT/Desktop/facefinder2/uploads/results'

def get_embedding(model, img_path):
      img = insightface.utils.face_align.norm_crop(np.array(Image.open(img_path)))
      return model.get_embedding(img)

def main():
      model = insightface.app.FaceAnalysis(name='buffalo_l', providers=['CPUExecutionProvider'])
      model.prepare(ctx_id=0)

      # Get selfie embeddings
      selfie_files = [os.path.join(SELFIE_DIR, f) for f in os.listdir(SELFIE_DIR)]
      selfie_embeddings = []
      for selfie in selfie_files:
          faces = model.get(np.array(Image.open(selfie)))
          if faces:
              selfie_embeddings.append(faces[0].embedding)

      if not selfie_embeddings:
          print("No face found in selfie(s).")
          return

      # Compare with photos
      for photo_file in os.listdir(PHOTOS_DIR):
          photo_path = os.path.join(PHOTOS_DIR, photo_file)
          faces = model.get(np.array(Image.open(photo_path)))
          for face in faces:
              for emb in selfie_embeddings:
                  sim = np.dot(face.embedding, emb) / (np.linalg.norm(face.embedding) * np.linalg.norm(emb))
                  if sim > 0.5:  # You can adjust this threshold
                      shutil.copy(photo_path, os.path.join(RESULTS_DIR, photo_file))
                      print(f"Match found: {photo_file}")
                      break

if __name__ == '__main__':
      main()