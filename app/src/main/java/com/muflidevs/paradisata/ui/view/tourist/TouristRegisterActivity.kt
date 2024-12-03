package com.muflidevs.paradisata.ui.view.tourist

import android.app.ComponentCaller
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.registration.User
import com.muflidevs.paradisata.databinding.ActivityTouristRegisterBinding
import com.muflidevs.paradisata.ui.view.customView.CustomButton
import com.muflidevs.paradisata.ui.view.customView.CustomEmailEditText
import com.muflidevs.paradisata.ui.view.customView.CustomNoTelephoneEditText
import com.muflidevs.paradisata.ui.view.customView.CustomPasswordEditText
import com.muflidevs.paradisata.ui.view.customView.CustomUsernameEditText
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class TouristRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTouristRegisterBinding
    private lateinit var usernameEdtTxt: CustomUsernameEditText
    private lateinit var passwordEdtTxt: CustomPasswordEditText
    private lateinit var emailEdtTxt: CustomEmailEditText
    private lateinit var noTelpEdtTxt: CustomNoTelephoneEditText
    private lateinit var submitBtn: CustomButton
    private lateinit var backBtn: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var viewModel: RegistrationViewModel
    private val RC_SIGN_IN = 9001
    private val WEB_API_KEY = "AIzaSyBxP7csoR7JWX3AlfkHyKJQAxZq1eD_F8E"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouristRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usernameEdtTxt = binding.edtTxtUsername
        passwordEdtTxt = binding.edtTxtPassword
        emailEdtTxt = binding.edtTxtEmail
        noTelpEdtTxt = binding.edtTxtNoTelp
        submitBtn = binding.btnSubmitTourist
        backBtn = binding.btnBackTourist

        //buttonOnOff
        setEnabledButton()
        checkUserInput()

        //navigation
        backBtn.setOnClickListener(this)
        submitBtn.setOnClickListener(this)
        binding.googleIcon?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_back_tourist -> {
                finish()
            }
            R.id.btn_submit_tourist -> {
                register()
            }
            R.id.google_icon -> {
                signInWithGoogle()
            }
        }
    }

    private fun setEnabledButton() {
        val checkUsernameInput = usernameEdtTxt.text != null && usernameEdtTxt.toString().isNotEmpty()
        val checkPasswordInput = passwordEdtTxt.text != null && passwordEdtTxt.toString().isNotEmpty()
        val checkEmailInput = emailEdtTxt.text != null && emailEdtTxt.toString().isNotEmpty()
        val checkNoTelpInput = noTelpEdtTxt.text != null && noTelpEdtTxt.toString().isNotEmpty()

        submitBtn.isEnabled = checkUsernameInput && checkPasswordInput && checkEmailInput && checkNoTelpInput
    }

    private fun checkUserInput() {
        usernameEdtTxt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setEnabledButton()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        passwordEdtTxt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setEnabledButton()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        emailEdtTxt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setEnabledButton()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        noTelpEdtTxt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setEnabledButton()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
    private fun register() {
        with(binding) {
            val user = User(
                email = edtTxtEmail.text.toString(),
                password = edtTxtPassword.text.toString(),
                userName = edtTxtUsername.text.toString(),
                phoneNumber = edtTxtNoTelp.text.toString()
            )
            lifecycleScope.launch {
                val viewModel = RegistrationViewModel(application)
                try {
                    viewModel.registerNewUser(user)
                    Toast.makeText(this@TouristRegisterActivity,"Berhasil registrasi",Toast.LENGTH_SHORT).show()
                }catch (e: Exception) {
                    Toast.makeText(this@TouristRegisterActivity,"registrasi gagal ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signInWithGoogle() {
       val signIn = GoogleSignIn.getClient(this,gso).signInIntent
        startActivityForResult(signIn,RC_SIGN_IN)
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)

        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel = RegistrationViewModel(application).apply {
                    registerWithGoole(account)
                }
            }catch (e: Exception) {
                Log.e(TAG,"${e.message}")
            }
        }

    }
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("428329049233-9tammq5ohj2jgi6f8vribqu7et1o0g0s.apps.googleusercontent.com")
        .requestEmail()
        .build()

}