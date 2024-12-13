from flask import Flask, request, jsonify
import numpy as np
import tensorflow as tf
import os
from google.cloud import firestore

# Set the path to your service account key
# os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="serviceacc.json"

app = Flask(__name__)

# Load the TensorFlow model
model = tf.keras.models.load_model("my_svd_model.h5")

# Initialize Firestore client for database 'db-user'
db = firestore.Client(project="paradisata1", database="db-user")

    
@app.route('/recommend', methods=['GET'])
def recommend():
    try:
        # Fetch all guide data from Firestore
        guides_ref = db.collection('rating')
        guides_docs = guides_ref.stream()

        guide_data = {}
        for doc in guides_docs:
            doc_data = doc.to_dict()
            guide_id = doc_data.get("GuideID")
            rating = doc_data.get("Rating")
            if guide_id in guide_data:
                guide_data[guide_id].append(rating)
            else:
                guide_data[guide_id] = [rating]

        # Calculate average ratings for each guide
        guide_ids = []
        guide_ratings = []
        for guide_id, ratings in guide_data.items():
            guide_ids.append(guide_id)
            guide_ratings.append(np.mean(ratings))

        # Sort guides by average rating in descending order
        sorted_indices = np.argsort(guide_ratings)[::-1]
        top_guide_ids = [guide_ids[i] for i in sorted_indices[:10]]

        return jsonify({"recommendations": top_guide_ids})

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/load-json-to-firestore', methods=['POST'])
def load_json_to_firestore():
    try:
        # Parse JSON input
        data = request.get_json()

        # Add data to Firestore collection with document IDs prefixed by 'db-user'
        batch = db.batch()
        for idx, item in enumerate(data):
            doc_id = f"db-user-{idx+1}"  # Create ID as 'db-user-1', 'db-user-2', etc.
            doc_ref = db.collection('rating').document(doc_id)  # Ensure data is saved in 'db-user' collection
            batch.set(doc_ref, item)
        batch.commit()

        return jsonify({"message": "Data successfully loaded to Firestore with IDs prefixed by 'db-user' in 'rating' collection."})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8080))
    # Hanya gunakan app.run() saat pengembangan lokal
    app.run(host="0.0.0.0", port=port)
