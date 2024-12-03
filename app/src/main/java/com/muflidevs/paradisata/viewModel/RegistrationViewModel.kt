package com.muflidevs.paradisata.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muflidevs.paradisata.data.model.remote.registration.TourGuide
import com.muflidevs.paradisata.data.model.remote.registration.Tourist
import com.muflidevs.paradisata.data.model.remote.registration.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance("db-user")

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user
    private val _tourist = MutableLiveData<Tourist>()
    val tourist: LiveData<Tourist> get() = _tourist

    private val _tourGuide = MutableLiveData<TourGuide>()
    val tourGuide: LiveData<TourGuide> get() = _tourGuide

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun registerWithGoole(account: GoogleSignInAccount) {
        account.let {
            val name = it.displayName ?: " "
            val email = it.email ?: " "
            val photoUrl = it.photoUrl ?: " "
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "null"

            val userMap = hashMapOf(
                "name" to name,
                "email" to email,
                "photoUrl" to photoUrl,
                "id" to uid
            )

            viewModelScope.launch {
                saveToFireStore(
                    collection = "users",
                    documentId = uid,
                    data = userMap
                )
            }
        }
    }

    suspend fun registerNewUser(user: User) {
        _isLoading.value = true
        try {
            val authResult = mAuth.createUserWithEmailAndPassword(user.email, user.password).await()

            val userMap = hashMapOf(
                "userName" to user.userName,
                "email" to user.email,
                "password" to user.password,
                "phoneNumber" to user.phoneNumber
            )
            saveToFireStore(
                collection = "user",
                documentId = authResult.user?.uid ?: "",
                data = userMap
            )
            _user.value = User(
                email = user.email,
                password = user.password,
                userName = user.userName,
                phoneNumber = user.phoneNumber
            )
        } catch (e: Exception) {
            Log.e("RegistrationViewModel", "${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun registerTourist(tourist: Tourist) {
        _isLoading.value = true
        try {
            val data = hashMapOf(
                "fullName" to tourist.fullName,
                "address" to tourist.address,
                "gender" to tourist.gender,
                "touristFrom" to tourist.touristFrom,
                "photo" to tourist.photo
            )

            saveToFireStore("user", "tourist", data = data)
            _tourist.value = tourist

        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan data ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun saveToFireStore(
        collection: String,
        subCollection: String? = null,
        documentId: String? = null,
        data: Map<String, Any>
    ) {
        _isLoading.value = true
        try {
            if (subCollection == null) {
                if (documentId != null) {
                    db.collection(collection).document(documentId)
                        .set(data)
                        .await()
                }
                Log.d("RegistrationViewModel", "DocumentSnapshot successfully written!")
            } else {
                if (documentId != null) {
                    db.collection(collection).document(documentId)
                        .collection(subCollection)
                        .document()
                        .set(data)
                        .await()
                }
            }
        } catch (e: Exception) {
            Log.e("RegistrationViewModel", "Error saving document: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri!!
    }

}