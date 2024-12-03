package com.muflidevs.paradisata.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import com.muflidevs.paradisata.data.model.remote.registration.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val mAuth = FirebaseAuth.getInstance()

    private val _userEmail = MutableLiveData<String>()
    val user: LiveData<String> get() = _userEmail
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    suspend fun signInWithGoogle(account: GoogleSignInAccount) {
        withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                val result = mAuth.signInWithCredential(credential).await()

                _userEmail.postValue(result.user?.email)
            } catch (e: Exception) {
                Log.e(TAG,"${e.message}")
            }
        }
    }
    fun isUserSignedIn(): Boolean {
        return mAuth.currentUser != null
    }
}