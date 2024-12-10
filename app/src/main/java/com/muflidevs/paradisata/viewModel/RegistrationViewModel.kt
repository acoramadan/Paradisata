package com.muflidevs.paradisata.viewModel

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.muflidevs.paradisata.data.model.remote.registration.PackageName
import com.muflidevs.paradisata.data.model.remote.registration.TourGuide
import com.muflidevs.paradisata.data.model.remote.registration.Tourist
import com.muflidevs.paradisata.data.model.remote.registration.User
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit


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

    private val _packageName = MutableLiveData<PackageName>()
    val packageName: LiveData<PackageName> get() = _packageName
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String> get() = _verificationId

    private val _verificationState = MutableLiveData<String>()
    val verificationState: LiveData<String> get() = _verificationState

    private val _isValid = MutableLiveData<Boolean>()
    val isValid: LiveData<Boolean> get() = _isValid


    suspend fun registerNewUser(user: User) {
        _isLoading.value = true
        try {
            val authResult = mAuth.createUserWithEmailAndPassword(user.email, user.password).await()
            val userMap = hashMapOf(
                "id" to user.id,
                "userName" to user.userName,
                "email" to user.email,
                "password" to user.password,
                "phoneNumber" to user.phoneNumber
            )
            saveToFireStore(
                collection = "user",
                documentId = user.id,
                data = userMap
            )
            _user.value = User(
                id = user.id,
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

    suspend fun registerTourist(tourist: Tourist, documentId: String) {
        _isLoading.value = true
        try {
            val data = hashMapOf(
                "id" to tourist.id,
                "fullName" to tourist.fullName,
                "address" to tourist.address,
                "gender" to tourist.gender,
                "touristFrom" to tourist.touristFrom,
                "photo" to tourist.photo
            )
            saveToFireStore(
                collection = "user",
                subCollection = "tourist",  // Nama subkoleksi
                documentId = documentId, // ID dokumen user yang valid
                data = data
            )
            Log.d("authresulfirebase", "${documentId}")
            _tourist.value = tourist

        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan data ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun registerTourguide(
        tourGuide: TourGuide,
        documentId: String,
        secondDocumentId: String?
    ) {
        _isLoading.value = true
        try {
            val data = hashMapOf(
                "id" to tourGuide.id,
                "fullName" to tourGuide.fullName,
                "address" to tourGuide.address,
                "gender" to tourGuide.gender,
                "dateOfBirth" to tourGuide.dateOfBirth,
                "photoKtp" to tourGuide.photo
            )
            saveToFireStore(
                collection = "user",
                subCollection = "tourGuide",
                documentId = documentId,
                secondDocumentId = secondDocumentId,
                data = data
            )
            Log.d("authresulfirebase", documentId)
            _tourGuide.value = tourGuide

        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan data ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun registerPackageTourguide(
        packageName: PackageName,
        documentId: String,
        secondDocumentId: String?,
        thirdDocumentId: String?
    ) {
        _isLoading.value = true
        try {
            val data = hashMapOf(
                "id" to packageName.id,
                "package_name" to packageName.name,
                "address" to packageName.address,
                "facilities" to packageName.facilities,
                "homestayPhoto" to packageName.homeStayPhoto,
                "totalGuest" to packageName.totalGuest,
                "TransportationPicture" to packageName.transportationPhoto,
                "TransportationType" to packageName.transportationType
            )
            saveToFireStore(
                collection = "user",
                subCollection = "tourGuide",
                thirdCollection = "package",
                documentId = documentId,
                secondDocumentId = secondDocumentId,
                thirdDocumentId = thirdDocumentId,
                data = data
            )
            Log.d("authresulfirebase", documentId)
            _packageName.value = packageName

        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan data ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }


    private suspend fun saveToFireStore(
        collection: String,
        subCollection: String? = null,
        thirdCollection: String? = null,
        documentId: String? = null,
        secondDocumentId: String? = null,
        thirdDocumentId: String? = null,
        data: Map<String, Any>
    ) {
        _isLoading.value = true
        try {
            if (subCollection == null) {
                if (documentId != null) {
                    db.collection(collection).document(documentId)
                        .set(data)
                        .await()
                    Log.d("RegistrationViewModel", "DocumentSnapshot successfully written!")
                }
            } else {
                if (documentId != null) {
                    if (secondDocumentId != null && thirdDocumentId != null) {
                        db.collection(collection).document(documentId)
                            .collection(subCollection).document(secondDocumentId)
                            .collection(thirdCollection ?: "package").document(thirdDocumentId)
                            .set(data)
                            .await()
                        Log.d(
                            "RegistrationViewModel",
                            "Document added to Thirdcollection successfully!"
                        )
                    } else if (secondDocumentId != null) {
                        db.collection(collection).document(documentId)
                            .collection(subCollection)
                            .document(secondDocumentId)
                            .set(data)
                            .await()
                    }
                    Log.d("RegistrationViewModel", "Document added to subcollection successfully!")
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

    fun sendVerificationCode(number: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, forceResendingToken)
                _verificationId.value = verificationId
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                code?.let {
                    _verificationState.value = "Code received: $it"
                    Log.e(TAG, it)
                    verifyCode(it)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _verificationState.value = "Verification failed: ${e.message}"
                Log.e(TAG, "${e.message}")
            }
        }

    fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value ?: "", code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _verificationState.value = "Sign in successful"
                    _isValid.value = true
                } else {
                    _verificationState.value = "Sign in failed: ${task.exception?.message}"
                    _isValid.value = false
                    throw Exception("Sign in failed: ${task.exception?.message}")
                }
            }
    }

}
