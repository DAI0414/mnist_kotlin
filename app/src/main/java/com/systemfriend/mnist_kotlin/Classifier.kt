package com.systemfriend.mnist_kotlin

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Classifier{

    private val modelName = "mnist.tflite"

    val imgHeight = 28
    val imgWidth = 28

    private val batchSize = 1
    private val numChannel = 1
    private val numClasses = 10

    private val options = Interpreter.Options()
    private val interpreter: Interpreter
    private val imageData: ByteBuffer
    private val imagePixels = IntArray(imgHeight * imgWidth)
    private val results = Array(1){FloatArray(numClasses)}

    constructor(activity: Activity){
        interpreter = Interpreter(loadModelFile(activity), options)
        imageData = ByteBuffer.allocateDirect(4 * batchSize * imgHeight * imgWidth * numChannel)
        imageData.order(ByteOrder.nativeOrder())
    }

    private fun loadModelFile(activity: Activity): MappedByteBuffer{
        var fileDescriptor: AssetFileDescriptor = activity.assets.openFd(modelName)
        var inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        var fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(bitmap: Bitmap): Pair<Int, Float>{
        convertBitmapToByteBuffer(bitmap)
        interpreter.run(imageData, results)

        var maxIndex = -1
        var max = -1.0f
        for(index in 0.until(numClasses)){
            if(max < results[0][index]){
                maxIndex = index
                max = results[0][index]
            }
        }

        return maxIndex to max
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap){
        if (imageData == null) return

        imageData.rewind()

        bitmap.getPixels(imagePixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for(pixel in 0.until(imgWidth * imgHeight)){
            val value = imagePixels[pixel]
            imageData.putFloat(convertPixel(value))
        }
    }

    private fun convertPixel(color: Int): Float{
        return (255 - (((color shr 16) and 0xFF) * 0.299f
                + ((color shr 8) and 0xFF) * 0.597f
                + (color and 0xFF) * 0.114f)) / 255f
    }
}