package com.muflidevs.paradisata.ui.view

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.SnapTransactionDetail
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityDestinationsOrderBinding
import com.muflidevs.paradisata.ui.view.payments.PaymentsMidtransActivity
import com.muflidevs.paradisata.viewModel.TouristViewModel
import com.muflidevs.paradisata.viewModel.UserViewModel
import java.util.UUID

class DestinationsOrderActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val touristViewModel: TouristViewModel by viewModels()
    private lateinit var binding: ActivityDestinationsOrderBinding
    private var count = 0
    private var dateRange = 0
    private val placesSelected = mutableListOf<String>()
    private var totalPrices = 0
    private var userName: String = ""
    private var email: String = ""
    private var phoneNumber: String = ""
    private var address: String = ""

    private var customerDetails = com.midtrans.sdk.uikit.api.model.CustomerDetails(
        firstName = "user fullname",
        customerIdentifier = "mail@mail.com",
        email = "mail@mail.com",
        phone = "085310102020"
    )
    private var itemDetails = listOf(ItemDetails("test-01", 36500.0, 1, "test01"))

    private fun initTransactionDetails(): SnapTransactionDetail {
        return SnapTransactionDetail(
            orderId = UUID.randomUUID().toString(),
            grossAmount = 36500.0
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDestinationsOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dateRange = intent.getIntExtra(OrderActivity.EXTRA_DATE, 0)
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-xQa1PTlE7tnvF8pG")
            .setContext(applicationContext)
            .setTransactionFinishedCallback(TransactionFinishedCallback { results ->

                if (results.status == "success") {
                    Toast.makeText(this, "Pembayaran Berhasil!", Toast.LENGTH_LONG).show()
                }
            })
            .setMerchantBaseUrl("https://5285-36-93-21-250.ngrok-free.app/bangkit/charge/index.php/")
            .enableLog(true)
            .setLanguage("id")
            .buildSDK()

        binding.btnAdd.setOnClickListener {
            if (count < dateRange) {
                val intent =
                    Intent(this@DestinationsOrderActivity, ListDestinationActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                binding.btnAdd.visibility = View.GONE
            }
        }
        userViewModel.getUser(userViewModel.getUserToken())
        userViewModel.user.observe(this@DestinationsOrderActivity) { user ->
            userName = user?.userName!!
            email = user.email
            phoneNumber = user.phoneNumber
        }
        touristViewModel.getTourist(userViewModel.getUserToken())
        touristViewModel.tourist.observe(this@DestinationsOrderActivity) { tourist ->
            address = tourist?.address!!
        }
        binding.btnSubmit.setOnClickListener {
            val transaction = initTransactionDetails()
            Log.d(
                "Payments : ",
                "Data : $itemDetails, $customerDetails, ${transaction.orderId}, ${transaction.grossAmount}"
            )
            val transactionRequest = TransactionRequest(
                "paradisata" + System.currentTimeMillis().toString() + "",
                totalPrices.toDouble()
            )
            val detail = ItemDetails(
                UUID.randomUUID().toString(),
                totalPrices.toDouble(),
                placesSelected.size,
                binding.listOrder.text.toString()
            )
            Log.d("DestinationOrderActivity", "Price : ${detail.price}\n Name : ${detail.name},${detail.quantity}")
            val itemDetails = ArrayList<ItemDetails>()
            itemDetails.add(detail)
            uiKitDetails(
                transactionRequest = transactionRequest,
                userName = userName,
                email = email,
                address = address,
                phoneNumber = phoneNumber
            )
            transactionRequest.itemDetails = itemDetails
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)

        }

    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedPlace = data?.getStringExtra(ListDestinationActivity.EXTRA_SELECTED_PLACE)
            val pricePlace = data?.getIntExtra(ListDestinationActivity.EXTRA_PRICE, 0)
            Log.d("SelectedPlace", "$selectedPlace")

            pricePlace.let {
                totalPrices += it ?: 0
                binding.price.text = "Rp.$totalPrices K"
            }
            selectedPlace?.let {
                placesSelected.add(it)
                count++

                binding.listOrder.text = "${placesSelected.joinToString("• ")}\n"
                if (count >= dateRange) {
                    binding.btnAdd.visibility = View.GONE
                }
            }
        }
    }

    private fun uiKitDetails(
        transactionRequest: TransactionRequest,
        userName: String,
        phoneNumber: String,
        email: String,
        address: String,
    ) {
        val customerDetails = CustomerDetails()
        val shippingAddress = ShippingAddress()
        val billingAddress = BillingAddress()

        Log.d(
            "DestinationOrderActivity",
            "Data yang dikirim email : $email \nUsername: $userName\nPhoneNumber $phoneNumber\naddress : $address"
        )
        customerDetails.firstName = userName
        customerDetails.lastName = userName
        customerDetails.customerIdentifier = userName
        customerDetails.phone = phoneNumber
        customerDetails.email = email
        shippingAddress.address = address
        shippingAddress.city = "Makassar"
        shippingAddress.postalCode = "90557"

        customerDetails.shippingAddress = shippingAddress
        billingAddress.address = address
        billingAddress.city = "Makassar"
        billingAddress.postalCode = "90557"
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }

    companion object {
        const val REQUEST_CODE = 1001
        const val EXTRA_TOTAL_PRICE = "extra_total_price"
        const val EXTRA_TOTAL_PLACE = "extra_total_place"
    }
}
