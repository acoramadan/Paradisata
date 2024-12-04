package com.muflidevs.paradisata.ui.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityOtpBinding
import com.muflidevs.paradisata.ui.view.tourist.TouristIdentityAuthActivity
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityOtpBinding
    private lateinit var userNumber: String
    private lateinit var viewModel: RegistrationViewModel
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = RegistrationViewModel(application)
        mAuth = FirebaseAuth.getInstance()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Berhasil verifikasi, langsung sign in dengan credential
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Verifikasi gagal
                Log.w(TAG, "onVerificationFailed", e)
                Log.e("OtpActivity","${e.message}")
                Toast.makeText(this@OtpActivity, "Verification Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent:`$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        userNumber = "+62${intent.getStringExtra("numberPhone")}"
        Log.d("OtpActivity", "User number: $userNumber")

        binding.resendOtp.setOnClickListener {
            resendVerificationCode(userNumber,resendToken)
        }

        binding.btnSubmitTourist.setOnClickListener {
            val enteredCode = getNumber()
            if (storedVerificationId != null) {
                verifyPhoneNumberWithCode(storedVerificationId!!, enteredCode)
            } else {
                Toast.makeText(this, "Verification ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        userNumber = "+62${intent.getStringExtra("numberPhone")}"

        startPhoneNumberVerification(userNumber)
        Log.e("onStartOTPACTIVITY",userNumber)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)       // Nomor telepon untuk verifikasi
            .setTimeout(60L, TimeUnit.SECONDS)  // Waktu tunggu verifikasi
            .setActivity(this)                 // Activity untuk callback reCAPTCHA
            .setCallbacks(callbacks)
            .build()

// Verifikasi nomor telepon
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    // Method to resend the verification code (if needed)
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)  // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)  // Timeout duration for verification
            .setActivity(this)  // Activity context for reCAPTCHA verification
            .setCallbacks(callbacks)  // Callback to handle verification states

        if (token != null) {
            optionsBuilder.setForceResendingToken(token)  // Use the token to resend verification
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    // Method to handle signing in with the received phone auth credential
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in successful, handle the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    // Navigate to next screen or show user info
                } else {
                    // Sign in failed, display a failure message
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // Invalid verification code entered
                    }
                }
            }
    }

    private fun getNumber(): String {
        with(binding) {
            val number =
                "${otpDigit1.text}${otpDigit2.text}${otpDigit3.text}${otpDigit4.text}${otpDigit5.text}${otpDigit6.text}"
            return number
        }
    }
}