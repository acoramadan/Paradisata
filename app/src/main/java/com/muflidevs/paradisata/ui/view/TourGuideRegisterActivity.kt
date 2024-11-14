package com.muflidevs.paradisata.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityTourGuideRegisterBinding
import com.muflidevs.paradisata.ui.view.customView.CustomButton
import com.muflidevs.paradisata.ui.view.customView.CustomEmailEditText
import com.muflidevs.paradisata.ui.view.customView.CustomNoTelephoneEditText
import com.muflidevs.paradisata.ui.view.customView.CustomUsernameEditText
import com.muflidevs.paradisata.ui.view.customView.CustomPasswordEditText

class TourGuideRegisterActivity : AppCompatActivity(),View.OnClickListener{
    private lateinit var binding: ActivityTourGuideRegisterBinding
    private lateinit var usernameEdtTxt: CustomUsernameEditText
    private lateinit var passwordEdtTxt: CustomPasswordEditText
    private lateinit var emailEdtTxt: CustomEmailEditText
    private lateinit var noTelpEdtTxt: CustomNoTelephoneEditText
    private lateinit var submitBtn: CustomButton
    private lateinit var backBtn: Button
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

        //navigation
        backBtn.setOnClickListener(this)
        submitBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_back -> finish()
            R.id.btn_submit -> {

            }
        }
    }
}