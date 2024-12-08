package com.muflidevs.paradisata.ui.view.payments

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityDestinationsOrderBinding
import com.muflidevs.paradisata.ui.view.DestinationsOrderActivity

class PaymentsMidtransActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinationsOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinationsOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prices = intent.getIntExtra(DestinationsOrderActivity.EXTRA_TOTAL_PRICE,0)
        val quantity = intent.getIntExtra(DestinationsOrderActivity.EXTRA_TOTAL_PLACE,0)

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-xQa1PTlE7tnvF8pG")
            .setContext(applicationContext)
            .setTransactionFinishedCallback(TransactionFinishedCallback { results ->

                if (results.status == "success") {
                    Toast.makeText(this, "Pembayaran Berhasil!", Toast.LENGTH_LONG).show()
                }
            })
            .setMerchantBaseUrl("http://localhost/bangkit/charge/index.php")
            .enableLog(true)
            .setLanguage("en")
            .buildSDK()

        binding.btnSubmit.setOnClickListener {
            val harga = prices
            val total = quantity
            val transactionRequest = TransactionRequest("paradisata"+System.currentTimeMillis().toShort()+ "",harga.toDouble())
            val detail = ItemDetails("NamaItemId", quantity.toDouble(), harga,"Package Order")
            val itemDetails = ArrayList<ItemDetails>()

            itemDetails.add(detail)
            uiKitDetails(transactionRequest)
            transactionRequest.itemDetails = itemDetails

            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)
        }
    }

    private fun uiKitDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails()
        val shippingAddress = ShippingAddress()
        val billingAddress = BillingAddress()
        customerDetails.customerIdentifier = "Ahmad Mufli Ramadhan"
        customerDetails.phone = "089698100654"
        customerDetails.firstName = "Ahmad Mufli"
        customerDetails.lastName = "Ramadhan"
        customerDetails.email = "a272b4ky0215@bangkit.academy"
        
        shippingAddress.address = "Rappocini, Tamalate"
        shippingAddress.city = "Makassar"
        shippingAddress.postalCode = "90225"

        customerDetails.shippingAddress = shippingAddress
        billingAddress.address = "Rappocini, Tamalate"
        billingAddress.city = "Makassar"
        billingAddress.postalCode = "90225"

        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails


    }
}