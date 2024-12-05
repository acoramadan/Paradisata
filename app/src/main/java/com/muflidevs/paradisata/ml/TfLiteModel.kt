package com.muflidevs.paradisata.ml

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import com.muflidevs.paradisata.viewModel.PlaceViewModel
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

    fun predict(inputData: List<Float>, destinationList: List<String>, topN: Int = 10): List<String> {
        // Membuat input array dengan tipe FloatArray sesuai inputData
        val inputArray = FloatArray(inputData.size) { index -> inputData[index] }

        // Membuat output array sesuai dengan jumlah destinasi (misalnya 10 output untuk 10 destinasi)
        val outputArray = FloatArray(destinationList.size) // Mengasumsikan output berjumlah sebanyak destinasi

        try {
            Log.d("Input", inputArray.joinToString(", "))

            // Menjalankan model untuk mendapatkan hasil output
            interpreter?.run(inputArray, outputArray)

            Log.d("Output", outputArray.joinToString(", "))

            // Memproses output menjadi probabilitas atau skor
            // Output array sekarang berisi skor untuk setiap destinasi
            val topIndices = outputArray
                .withIndex()
                .sortedByDescending { it.value }  // Mengurutkan berdasarkan nilai tertinggi
                .take(topN)                       // Mengambil top N rekomendasi
                .map { it.index }                  // Mendapatkan indeks dari hasil sorting

            // Mengambil destinasi berdasarkan topIndices dan mengembalikannya dalam bentuk list
            val recommendations = topIndices
                .map { destinationList[it] }  // Mengambil nama destinasi berdasarkan indeks

            Log.d("TFLITEMODEL", "Top $topN rekomendasi destinasi wisata: ${recommendations.joinToString(", ")}")
            Log.d("TFLITEMODEL", "Top $topN outputProbabilities: ${outputArray.joinToString { ", " }}")
            Log.d("TFLITEMODEL", "Top $topN topIndices: $topIndices")

            onResult("Rekomendasi destinasi wisata teratas: ${recommendations.joinToString(", ")}")

            return recommendations  // Kembalikan list rekomendasi destinasi
        } catch (e: Exception) {
            onError("Error saat menjalankan model: ${e.message}")
            return emptyList()  // Mengembalikan list kosong jika terjadi error
        }
    }




    fun close() {
        interpreter?.close()
    }


    companion object {
        private const val TAG = "PredictionHelper"
    }


}