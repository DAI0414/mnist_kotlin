package com.systemfriend.mnist_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val logTag = MainActivity::class.java.simpleName
    private var classifier: Classifier? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        classifier = Classifier(this)

        detectButton.setOnClickListener{
            if(classifier == null){
                Log.e(logTag, "onDetectClick(): Classifier is not initialized")
            } else if(fingerPaint.isEmpty){
                Toast.makeText(this, "Please write a digit in the box above.", Toast.LENGTH_LONG).show()
            } else {
                var image = fingerPaint.exportToBitmap(classifier!!.imgWidth, classifier!!.imgHeight)
                var result = classifier!!.classify(image)

                indexText.text = result.first.toString()
                resultText.text = result.second.toString()
            }
        }

        clearButton.setOnClickListener {
            fingerPaint.clear()
            indexText.text = ""
            resultText.text = ""
        }
    }
}
