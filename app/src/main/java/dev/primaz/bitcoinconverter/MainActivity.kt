package dev.primaz.bitcoinconverter

import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConvert.setOnClickListener {
            if (edtUserValue.text.toString().isNotEmpty()) {
                if (edtUserValue.text.toString().toFloat() > 0) {
                    getConvertedValue(currency_spinner.selectedItem.toString(), edtUserValue.text.toString().toFloat())
                } else {
                    Toast.makeText(applicationContext, getString(R.string.value_must_be_greater_then_0), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, getString(R.string.textbox_empty), Toast.LENGTH_LONG).show()
            }
        }

        val spinner: Spinner = findViewById(R.id.currency_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.currency_code_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) { }
        }
    }

    private fun getConvertedValue(currencyCode: String, value: Number) {
        val url = "https://blockchain.info/tobtc?currency=${currencyCode}&value=${value}"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(applicationContext, getString(R.string.fail_to_convert), Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call, response: Response) {
                val convertedValue = response.body()?.string()
                handler.post(Runnable {
                    kotlin.run {
                        txtResult.text = String.format(getString(R.string.result), convertedValue)
                        resultInstructionalText.visibility = View.VISIBLE
                    }
                })
            }
        })
    }
}
