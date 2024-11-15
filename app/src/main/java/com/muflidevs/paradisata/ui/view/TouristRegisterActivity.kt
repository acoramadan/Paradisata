package com.muflidevs.paradisata.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityTouristRegisterBinding
import com.muflidevs.paradisata.ui.view.customView.CustomButton
import com.muflidevs.paradisata.ui.view.customView.CustomEmailEditText
import com.muflidevs.paradisata.ui.view.customView.CustomNoTelephoneEditText
import com.muflidevs.paradisata.ui.view.customView.CustomPasswordEditText
import com.muflidevs.paradisata.ui.view.customView.CustomUsernameEditText

class TouristRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTouristRegisterBinding
    private lateinit var usernameEdtTxt: CustomUsernameEditText
    private lateinit var passwordEdtTxt: CustomPasswordEditText
    private lateinit var emailEdtTxt: CustomEmailEditText
    private lateinit var noTelpEdtTxt: CustomNoTelephoneEditText
    private lateinit var submitBtn: CustomButton
    private lateinit var backBtn: Button

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
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_back_tourist -> {
                finish()
            }
            R.id.btn_submit_tourist -> {

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
}