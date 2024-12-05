package com.muflidevs.paradisata.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class TfLiteHelper(private val context: Context) {

    private val interpreter: Interpreter

    init {
        // Memuat model TFLite
        val tfliteModel = loadModelFile("recommendation_model.tflite")
        interpreter = Interpreter(tfliteModel)
    }

    private fun loadModelFile(modelName: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.length)
        return buffer
    }

    fun getRecommendation(inputData: FloatArray): FloatArray {
        val outputData = Array(1) { FloatArray(20) } // Misal 20 rekomendasi destinasi
        interpreter.run(inputData, outputData)
        return outputData[0]
    }
}