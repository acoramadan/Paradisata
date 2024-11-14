package com.muflidevs.paradisata.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityLoginBinding
import com.muflidevs.paradisata.ui.view.customView.CustomButton
import com.muflidevs.paradisata.ui.view.customView.CustomPasswordEditText
import com.muflidevs.paradisata.ui.view.customView.CustomUsernameEditText

class LoginActivity : AppCompatActivity(),View.OnClickListener{
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginbtn: CustomButton
    private lateinit var userNameEdtTxt: CustomUsernameEditText
    private lateinit var passwordEdtTxt: CustomPasswordEditText
    private lateinit var tvRegister: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvRegister = binding.tvRegister
        loginbtn = binding.btnLogin
        userNameEdtTxt = binding.edtTxtUsername
        passwordEdtTxt = binding.edtTxtPassword
        setMyButtonEnabled()
        checkUserInput()

        tvRegister.setOnClickListener(this)

    }

    private fun setMyButtonEnabled() {
        val result = userNameEdtTxt.text
        val pasResult = passwordEdtTxt.text
        val checkPassForm = pasResult != null && pasResult.toString().isNotEmpty()
        val checkUserForm = result != null && result.toString().isNotEmpty()
        loginbtn.isEnabled = checkPassForm && checkUserForm

    }
    private fun checkUserInput() {
        userNameEdtTxt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMyButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        passwordEdtTxt.addTextChangedListener(object: TextWatcher {
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
        when(view?.id) {
            R.id.tv_register -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}