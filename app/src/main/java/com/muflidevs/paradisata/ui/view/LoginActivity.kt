package com.muflidevs.paradisata.ui.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityLoginBinding
import com.muflidevs.paradisata.ui.view.customView.CustomButton
import com.muflidevs.paradisata.ui.view.customView.CustomEmailEditText
import com.muflidevs.paradisata.ui.view.customView.CustomPasswordEditText
import com.muflidevs.paradisata.ui.view.customView.CustomUsernameEditText
import com.muflidevs.paradisata.ui.view.tourguide.TourGuideMainActivity
import com.muflidevs.paradisata.viewModel.LoginViewModel
import com.muflidevs.paradisata.viewModel.UserViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val REQ_ONE_TAP = 2
    private var showOneTapUi = true
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginbtn: CustomButton
    private lateinit var userNameEdtTxt: CustomEmailEditText
    private lateinit var passwordEdtTxt: CustomPasswordEditText
    private lateinit var tvRegister: TextView
    private lateinit var viewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = LoginViewModel(application)
        tvRegister = binding.tvRegister
        loginbtn = binding.btnLogin
        userNameEdtTxt = binding.edtTxtEmail!!
        passwordEdtTxt = binding.edtTxtPassword
        auth = Firebase.auth
        setMyButtonEnabled()
        checkUserInput()
        userViewModel = UserViewModel(application)
        tvRegister.setOnClickListener(this)
        binding.btnLoginwthGoogle.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)

    }

    private fun setMyButtonEnabled() {
        val result = userNameEdtTxt.text
        val pasResult = passwordEdtTxt.text
        val checkPassForm = pasResult != null && pasResult.toString().isNotEmpty()
        val checkUserForm = result != null && result.toString().isNotEmpty()
        loginbtn.isEnabled = checkPassForm && checkUserForm

    }

    private fun checkUserInput() {
        userNameEdtTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMyButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        passwordEdtTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_register -> {
                val intent =
                    Intent(
                        this@LoginActivity,
                        RegisterActivity::class.java
                    )
                startActivity(intent)
                finish()
            }

            R.id.btn_loginwthGoogle -> {
                signInWithGoogle()
            }

            R.id.btn_login -> {
                loginWithEmailAndPassword()
            }
        }
    }

    private fun loginWithEmailAndPassword() {
        val email = userNameEdtTxt.text.toString()
        val password = passwordEdtTxt.text.toString()

        lifecycleScope.launch {
            viewModel.signInWithEmailPassword(email, password)
            viewModel.user.observe(this@LoginActivity) { currentUser ->
                updateUI(currentUser)
            }
        }
    }

    private fun signInWithGoogle() {
        val credential = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credential.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleSignIn(result)
            } catch (e: Exception) {
                Log.e(TAG, "$e.message")
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(id: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(id, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val userId = currentUser.uid

            userViewModel.checkUserRole(userId)

            userViewModel.userRole.observe(this) { role ->
                when (role) {
                    "tourist" -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    "tourGuide" -> {
                        val intent = Intent(this@LoginActivity, TourGuideMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "None" -> {
                        Toast.makeText(this, "Akun belum terdaftar", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        } else {
            Toast.makeText(this, "Login gagal Username/Password salah!", Toast.LENGTH_SHORT).show()
        }
    }
}