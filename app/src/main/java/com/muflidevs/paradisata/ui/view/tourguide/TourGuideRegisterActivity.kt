package com.muflidevs.paradisata.ui.view.tourguide

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.registration.User
import com.muflidevs.paradisata.databinding.ActivityTourGuideRegisterBinding
import com.muflidevs.paradisata.ui.view.customView.CustomButton
import com.muflidevs.paradisata.ui.view.customView.CustomEmailEditText
import com.muflidevs.paradisata.ui.view.customView.CustomNoTelephoneEditText
import com.muflidevs.paradisata.ui.view.customView.CustomUsernameEditText
import com.muflidevs.paradisata.ui.view.customView.CustomPasswordEditText
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import kotlinx.coroutines.launch
import java.util.UUID

class TourGuideRegisterActivity : AppCompatActivity(),View.OnClickListener{
    private lateinit var binding: ActivityTourGuideRegisterBinding
    private lateinit var usernameEdtTxt: CustomUsernameEditText
    private lateinit var passwordEdtTxt: CustomPasswordEditText
    private lateinit var emailEdtTxt: CustomEmailEditText
    private lateinit var noTelpEdtTxt: CustomNoTelephoneEditText
    private lateinit var submitBtn: CustomButton
    private lateinit var backBtn: Button
    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourGuideRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usernameEdtTxt = binding.edtTxtUsername
        passwordEdtTxt = binding.edtTxtPassword
        emailEdtTxt = binding.edtTxtEmail
        noTelpEdtTxt = binding.edtTxtNoTelp
        submitBtn = binding.btnSubmit
        backBtn = binding.btnBack

        setEnabledButton()
        checkUserInput()

        backBtn.setOnClickListener(this)
        submitBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_back -> finish()
            R.id.btn_submit -> {
                register()
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
                id = UUID.randomUUID().toString(),
                email = edtTxtEmail.text.toString(),
                password = edtTxtPassword.text.toString(),
                userName = edtTxtUsername.text.toString(),
                phoneNumber = edtTxtNoTelp.text.toString()
            )
            lifecycleScope.launch {
                viewModel = RegistrationViewModel(application)
                try {
                    viewModel.registerNewUser(user)
                    Toast.makeText(
                        this@TourGuideRegisterActivity,
                        "Berhasil registrasi",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(
                        Intent(
                            this@TourGuideRegisterActivity,
                            TourGuideIdentityAuthActivity::class.java
                        ).apply {
                            putExtra("extra_uuid", user.id)
                            Log.d("TourGuideRegister","Data UUID yang dikirim ${user.id}")
                        }
                    )
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@TourGuideRegisterActivity,
                        "registrasi gagal ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("TourGuideRegister", "error : ${e.message}")
                }
            }
        }
    }
}