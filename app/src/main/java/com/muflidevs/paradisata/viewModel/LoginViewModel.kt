package com.muflidevs.paradisata.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.muflidevs.paradisata.data.model.remote.registration.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mAuth = FirebaseAuth.getInstance()

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val sharedPrefs =
        application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun checkIfUserIsLoggedIn(): Boolean {
        val token = sharedPrefs.getString("user_token", null)
        return token != null
    }

    private fun saveUserToken(token: String) {
        val editor = sharedPrefs.edit()
        editor.putString("user_token", token)
        editor.apply()
    }

    private fun clearUserToken() {
        val editor = sharedPrefs.edit()
        editor.remove("user_token")
        editor.apply()
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount) {
        withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = mAuth.signInWithCredential(credential).await()

                _userEmail.postValue(result.user?.email)
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }
    }

    suspend fun signInWithEmailPassword(
        email: String,
        password: String
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user =
                    signInWithEmailAndPasswordAsync(email, password)
                _user.value = user
                user?.uid?.let { saveUserToken(it) }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
                Log.e("LoginViewModel", errorMessage.value!!)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun signInWithEmailAndPasswordAsync(
        email: String,
        password: String
    ): FirebaseUser? {
        return suspendCoroutine { continuation ->
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(mAuth.currentUser)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Login failed")
                        )
                    }
                }
        }
    }

    fun logout() {
        mAuth.signOut()
        clearUserToken()
    }
}