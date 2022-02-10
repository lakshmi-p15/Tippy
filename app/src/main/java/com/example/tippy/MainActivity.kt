package com.example.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat


private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENTAGE = 15
class MainActivity : AppCompatActivity() {
    private lateinit var seekBarTip: SeekBar
    private lateinit var etBaseAmount: EditText
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBarTip = findViewById(R.id.seekBarTip)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        //initialize
        seekBarTip.progress = INITIAL_TIP_PERCENTAGE
        tvTipPercent.text = "$INITIAL_TIP_PERCENTAGE%"
        updateTipDescription(INITIAL_TIP_PERCENTAGE)

        //Listeners for seekbar and base amount
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgress Changed $p1")
                tvTipPercent.text = "$p1%"
                computeTipAndTotal()
                updateTipDescription(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
            }
        })

    }

    private fun updateTipDescription(tipPercent: Int) {
        //change tip description based on tip percentile
        val tipDescription = when(tipPercent){
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription

        // Update description text color based on tip percentile
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this,R.color.color_worst_tip),
            ContextCompat.getColor(this,R.color.color_best_tip)
        )as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {

        //Do nothing when base amount is empty
        if (etBaseAmount.text.isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        //Exception handling for entering valid Amount(handles unwanted characters being entered)
        try {

            //read Base Amount and Tip entered by the user
            val baseAmount = etBaseAmount.text.toString().toDouble()
            val tipPercent = seekBarTip.progress

            //Calculate tip and total amount based on tip percentage
            val tipAmount = baseAmount * tipPercent / 100
            val totalAmount = baseAmount + tipAmount

            // Update user with two decimal point amounts
            tvTipAmount.text = "%.2f".format(tipAmount)
            tvTotalAmount.text = "%.2f".format(totalAmount)

        }catch (e: NumberFormatException){
            //Let the user know that they have entered wrong characters/number
            Toast.makeText(this, "Enter valid Amount", Toast.LENGTH_SHORT).show()
            etBaseAmount.text.clear()
            return
        }
    }
}