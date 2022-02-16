package com.example.saveandgetmd5hashes

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val FILENAME: String = "MD5Hashes.txt"
    private var hashesMD5List = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
        val textInputEditText = textInputLayout.editText as TextInputEditText

        val saveButton: Button = findViewById(R.id.saveButton)
        val openButton: Button = findViewById(R.id.getTextButton)

        val savedText: TextView = findViewById(R.id.savedHashesTextView)
        val MD5HASHES : Pattern = Pattern.compile(
            "[A-F0-9]{32}"
        )


        fun TextInputEditText.listenChanges(block: (text: String) -> Unit) {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    block.invoke(s.toString())
                }
            })
        }

        fun AppCompatActivity.hideKeyboard(view: View) {
            val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textInputEditText.windowToken, 0)
        }

        textInputEditText.listenChanges { textInputLayout.isErrorEnabled = false }

        saveButton.setOnClickListener {
            if (MD5HASHES.matcher(textInputEditText.text.toString()).matches()){
                hideKeyboard(textInputEditText)
                saveTextToList(textInputEditText.text.toString())
                Snackbar.make(saveButton, "Hash added", Snackbar.LENGTH_SHORT).show()
            } else {
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = getString(R.string.errorInputMD5Hashes)
                hideKeyboard(textInputEditText)
                Snackbar.make(saveButton, "Hash input error", Snackbar.LENGTH_SHORT).show()
            }
        }
        openButton.setOnClickListener {
            hashesMD5List = getListFromFile(FILENAME).toMutableSet()
            savedText.text = hashesMD5List.joinToString("\n")

        }


    }

    fun saveTextToList(text: String) {
        if (hashesMD5List.add(text)) {
            saveText(FILENAME)
        }
        else {
            Log.d("checkSet", "не добавлено, это повторение")
            Log.d("checkSet", hashesMD5List.toString())
            Log.d("checkSet", text)
            Toast.makeText(applicationContext, "Hash already exists", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveText(fileName: String) {
        applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(hashesMD5List.joinToString("\n").toByteArray())
        }
        Log.d("checkSet", "значение добавилось")
        Log.d("checkSet", hashesMD5List.toString())
        Toast.makeText(applicationContext, "Hash added", Toast.LENGTH_SHORT).show()
    }

    fun getListFromFile(fileName: String): MutableList<String> =
        File(applicationContext.filesDir, fileName).bufferedReader()
            .readLines() as MutableList<String>
}