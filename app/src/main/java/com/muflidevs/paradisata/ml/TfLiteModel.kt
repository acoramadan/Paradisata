package com.muflidevs.paradisata.ml

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TfLiteModel(
    private val modelName: String = "recommendation_model.tflite",
    val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
) {
    private var isGPUSupported: Boolean = false

    private var interpreter: InterpreterApi? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
                isGPUSupported = true
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            loadLocalModel()
        }.addOnFailureListener {
            onError("Error model belum dimasukan")
        }
    }
    private fun loadLocalModel() {
        try {
            val buffer: ByteBuffer = loadModelFile(context.assets, modelName)
            initializeInterpreter(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }
    private fun initializeInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
            if (isGPUSupported){
                options.addDelegateFactory(GpuDelegateFactory())
            }
            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            }
        } catch (e: Exception) {
            onError(e.message.toString())
            Log.e(TAG, e.message.toString())
        }
    }

    fun predict(inputData: List<Float>) {
        val inputArray = FloatArray(inputData.size) { index -> inputData[index] }

        val outputArray = Array(1) { FloatArray(1) }  // Output array sesuai dengan model (misal hanya satu destinasi)

        try {
            interpreter?.run(inputArray, outputArray)

            val predictedClass = outputArray[0].withIndex().maxByOrNull { it.value }?.index
            onResult("Prediksi destinasi wisata: ${destinationList[predictedClass!!]}") // Menampilkan nama destinasi
        } catch (e: Exception) {
            onError("Error saat menjalankan model: ${e.message}")
        }
    }

    fun close() {
        interpreter?.close()
    }

    companion object {
        private const val TAG = "PredictionHelper"
        private  val destinationList = arrayOf(
            "Gedung Sate", "Candi Cangkuang", "Rumah Sejarah Kalijati", "Benteng Pendem",
            "Kampung Cina", "Candi Jiwa", "Museum Geologi", "Benteng Karawang",
            "Pantai Ujung Genteng", "Kebun Teh Purwakarta", "Pantai Cikao", "Kota Tua Bogor",
            "Cikao Park", "Keraton Kasepuhan", "Benteng Cagar Alam Pangandaran",
            "Istana Bogor", "Curug Cimahi", "Tugu Perjuangan Karawang", "Situ Gunung", "Kawah Cibuni"
        )
    }
}