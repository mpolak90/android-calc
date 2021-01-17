package pl.bajty.simplecalculator

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val state = CurrentState(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initButtons()
    }

    private fun getDigitButtons() : List<Button> {
        return listOf(
                findViewById(R.id._0),
                findViewById(R.id._1),
                findViewById(R.id._2),
                findViewById(R.id._3),
                findViewById(R.id._4),
                findViewById(R.id._5),
                findViewById(R.id._6),
                findViewById(R.id._7),
                findViewById(R.id._8),
                findViewById(R.id._9)
        )
    }

    fun showToast(string: String) {
        Toast.makeText(applicationContext, string, Toast.LENGTH_SHORT).show()
    }

    private fun showResult() {
        findViewById<TextView>(R.id.bottomLine).text = state.getState()
        val lastResult = "Ans = " + state.getLastResult()
        findViewById<TextView>(R.id.topLine).text = lastResult
    }

    private fun initButtons() {
        for (button in getDigitButtons()) {
            button.setOnClickListener {
                state.continueDigit(button.text.toString())
                showResult()
            }
        }
        for (button in getElementarySymbols()) {
                button.setOnClickListener {
                    if (button.text.toString() != "NUM") {
                        state.continueSymbol(button.text.toString())
                    } else {
                        state.continueSymbol("^")
                    }
                showResult()
            }
        }

        findViewById<Button>(R.id.comma).setOnClickListener {
            state.addComma()
            showResult()
        }

        findViewById<Button>(R.id.backspace).setOnClickListener {
            state.clearLast()
            showResult()
        }

        findViewById<Button>(R.id.equals).setOnClickListener {
            state.equality()
            showResult()
        }

        findViewById<Button>(R.id.openBracket).setOnClickListener {
            state.addOpenBracket()
            showResult()
        }

        findViewById<Button>(R.id.closeBracket).setOnClickListener {
            state.addCloseBracket()
            showResult()
        }

        findViewById<Button>(R.id.c).setOnClickListener {
            state.end()
            showResult()
        }

        findViewById<Button>(R.id.percent).setOnClickListener {
            state.continuePercent()
            showResult()
        }

        findViewById<Button>(R.id.xsquare).setOnClickListener {
            findViewById<Button>(R.id.num).performClick()
            findViewById<Button>(R.id._2).performClick()
        }

        findViewById<Button>(R.id.ans).setOnClickListener {
            state.addAns()
            showResult()
        }

        findViewById<Button>(R.id.bin).setOnClickListener {
            val holder = "BIN: " + state.getBin(state.getLastResult())
            findViewById<TextView>(R.id.topLine).text = holder
        }

        findViewById<Button>(R.id.hex).setOnClickListener {
            val holder = "HEX: " + state.getHex(state.getLastResult())
            findViewById<TextView>(R.id.topLine).text = holder
        }

        findViewById<Button>(R.id.dec).setOnClickListener {
            val holder = "Ans = " + state.getLastResult()
            findViewById<TextView>(R.id.topLine).text = holder
        }

        findViewById<Button>(R.id.sto).setOnClickListener {
            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle(R.string.storage)
            var message = ""
            if (state.getStorage().size == 0) {
                message = R.string.empty.toString()
            } else {
                for (i in 0 until state.getStorage().size) {
                    message += (i + 1).toString() + ": ${state.getStorage()[i]}\n\n"
                }
            }
            alertDialog.setMessage(message)
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, _ -> dialog.dismiss() }
            alertDialog.show()
        }
    }

    private fun getElementarySymbols() : List<Button> {
        return listOf(
                findViewById(R.id.divide),
                findViewById(R.id.multiply),
                findViewById(R.id.minus),
                findViewById(R.id.plus),
                findViewById(R.id.num)
        )
    }

}