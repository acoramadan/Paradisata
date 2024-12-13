# -*- coding: utf-8 -*-
"""tour_guide_recommender.ipynb

Automatically generated by Colab.

Original file is located at
    https://colab.research.google.com/drive/1leVtiHusOvEUY0V7epOIlo8fb-wlVGrF
"""

import pandas as pd
import numpy as np
from sklearn.decomposition import TruncatedSVD
from sklearn.metrics import mean_squared_error
import json
import pickle

# Step 1: Memuat data dari .json
with open('guide_ratings_reviews.json', 'r') as file:
    data = json.load(file)

# Mengonversi data JSON ke DataFrame
df = pd.DataFrame(data)

# Menampilkan beberapa data untuk memastikan struktur
print("Data awal: ")
print(df.head())

# Step 2: Membuat matriks pengguna vs panduan
# Matriks pengguna (UserID) vs panduan (GuideID) berdasarkan Rating
matrix = df.pivot(index='UserID', columns='GuideID', values='Rating').fillna(0)

# Menampilkan matriks untuk memeriksa hasil
print(f'Matrix pengguna vs panduan: \n{matrix.head()}')

# Step 3: Matrix Factorization dengan SVD
svd = TruncatedSVD(n_components=5, random_state=42)  # Tentukan komponen yang sesuai
svd.fit(matrix)  # Latih dengan matriks data

# Step 4: Menggunakan model SVD untuk membuat prediksi
predicted_matrix_full = svd.inverse_transform(svd.transform(matrix))  # Prediksi matriks penuh

# Step 5: Fungsi rekomendasi
def get_recommendations(user_id, n_recommendations=10):
    # Cek apakah user_id ada dalam matriks
    if user_id < 1 or user_id > matrix.shape[0]:
        return {"error": "UserID tidak valid"}

    # Prediksi untuk user tertentu
    user_index = user_id - 1  # Mengubah UserID menjadi index berbasis 0
    user_predicted_ratings = predicted_matrix_full[user_index]  # Mengambil seluruh prediksi untuk user tersebut

    # Mendapatkan ID guide dengan rating tertinggi
    recommended_guides = np.argsort(user_predicted_ratings)[-n_recommendations:][::-1]
    return {"recommendations": (recommended_guides + 1).tolist()}  # Mengembalikan GuideID yang sesuai (1-based index)

# Step 6: Memberikan rekomendasi tour guide berdasarkan hasil prediksi
user_id = 1  # Ganti dengan UserID yang ingin direkomendasikan
recommendations = get_recommendations(user_id)
print(f"Rekomendasi tour guide untuk UserID {user_id}: {recommendations}")

# Step 7: Menyimpan model SVD
with open('svd_model.pkl', 'wb') as f:
    pickle.dump(svd, f)
print("Model SVD telah disimpan sebagai 'svd_model.pkl'")

# Step 8: Menyimpan model TensorFlow ke dalam format H5
import tensorflow as tf
from tensorflow.keras import layers, models

# Bangun model neural network sederhana (bukan untuk SVD, hanya untuk contoh)
model = models.Sequential([
    layers.Input(shape=(predicted_matrix_full.shape[1],)),  # Input layer sesuai dengan dimensi hasil prediksi
    layers.Dense(128, activation='relu'),  # Layer tersembunyi pertama
    layers.Dense(64, activation='relu'),   # Layer tersembunyi kedua
    layers.Dense(1)  # Output layer untuk prediksi
])

model.compile(optimizer='adam', loss='mean_squared_error')

# Misalnya kita latih model (data latih bisa disesuaikan)
train_data = np.random.rand(100, 20)  # Data acak untuk contoh
train_labels = np.random.rand(100, 1)  # Label acak untuk contoh
model.fit(train_data, train_labels, epochs=5)

# Menyimpan model TensorFlow ke dalam format H5
model.save('my_svd_model.h5')
print("Model TensorFlow disimpan!")

# Langkah 9: Mengonversi model ke TensorFlow Lite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Menyimpan model TensorFlow Lite ke file
with open('svd_model.tflite', 'wb') as f:
    f.write(tflite_model)

print("Model berhasil dikonversi ke TensorFlow Lite!")

from sklearn.metrics import mean_squared_error
import numpy as np

# Step 1: Menghitung RMSE
# Misalkan kita menggunakan matriks pengguna vs panduan sebagai ground truth
actual_ratings = matrix.values.flatten()  # Flatten matriks asli (rating)
predicted_ratings = predicted_matrix_full.flatten()  # Flatten matriks prediksi

# Menghitung RMSE (Root Mean Squared Error)
rmse = np.sqrt(mean_squared_error(actual_ratings, predicted_ratings))
print(f'RMSE Model SVD: {rmse}')

# Step 2: Menghitung Akurasi (Misalnya dengan threshold prediksi)
threshold = 3  # Rating threshold (misalnya jika rating > 3 dianggap relevan)
predicted_ratings_binary = (predicted_ratings >= threshold).astype(int)
actual_ratings_binary = (actual_ratings >= threshold).astype(int)

# Akurasi dihitung sebagai proporsi prediksi yang benar
accuracy = np.mean(predicted_ratings_binary == actual_ratings_binary)
print(f'Akurasi Model: {accuracy * 100:.2f}%')

import matplotlib.pyplot as plt
import numpy as np

# Step 1: Membuat Plot perbandingan antara rating asli dan prediksi
plt.figure(figsize=(10, 6))

# Scatter plot rating asli vs rating prediksi
plt.scatter(actual_ratings, predicted_ratings, alpha=0.3)
plt.plot([min(actual_ratings), max(actual_ratings)], [min(actual_ratings), max(actual_ratings)], color='red', linestyle='--')
plt.title('Perbandingan Rating Asli dan Prediksi')
plt.xlabel('Rating Asli')
plt.ylabel('Rating Prediksi')
plt.grid(True)
plt.show()

# Step 2: Membuat Histogram untuk rating asli dan prediksi
plt.figure(figsize=(10, 6))

# Plot histogram untuk rating asli
plt.hist(actual_ratings, bins=30, alpha=0.5, label='Rating Asli', color='blue')

# Plot histogram untuk rating prediksi
plt.hist(predicted_ratings, bins=30, alpha=0.5, label='Rating Prediksi', color='orange')

plt.title('Distribusi Rating Asli dan Prediksi')
plt.xlabel('Rating')
plt.ylabel('Frekuensi')
plt.legend()
plt.grid(True)
plt.show()

