package com.example.justplayhelper

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : Activity() {
    private var timer: CountDownTimer? = null
    private var duration = 30_000L
    private var remaining = duration

    private lateinit var display: TextView
    private lateinit var status: TextView
    private lateinit var startButton: Button
    private lateinit var slider: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 24)
        }

        layout.addView(TextView(this).apply {
            text = "JustPlay Helper"
            textSize = 28f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        })

        display = TextView(this).apply {
            text = formatTime(remaining)
            textSize = 44f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 16)
        }
        layout.addView(display)

        val durationLabel = TextView(this).apply {
            text = "Duração: 30 segundos"
            textSize = 16f
        }
        layout.addView(durationLabel)

        slider = SeekBar(this).apply {
            max = 175
            progress = 25
        }
        layout.addView(slider)

        slider.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    bar: SeekBar?, value: Int, fromUser: Boolean
                ) {
                    if (timer == null) {
                        duration = (value + 5) * 1000L
                        remaining = duration
                        display.text = formatTime(remaining)
                    }
                    durationLabel.text =
                        "Duração: ${value + 5} segundos"
                }

                override fun onStartTrackingTouch(bar: SeekBar?) {}
                override fun onStopTrackingTouch(bar: SeekBar?) {}
            }
        )

        startButton = Button(this).apply {
            text = "Iniciar temporizador"
        }
        layout.addView(startButton)
        startButton.setOnClickListener { startTimer() }

        val stopButton = Button(this).apply {
            text = "Parar / Reiniciar"
        }
        layout.addView(stopButton)
        stopButton.setOnClickListener {
            timer?.cancel()
            timer = null
            remaining = duration
            display.text = formatTime(remaining)
            status.text = "Temporizador parado."
            startButton.isEnabled = true
        }

        status = TextView(this).apply {
            text = "Pronto para começar."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 12)
        }
        layout.addView(status)

        layout.addView(TextView(this).apply {
            text = "Usa o temporizador como lembrete. " +
                "Esta versão não controla anúncios nem garante recompensas."
            textSize = 13f
            gravity = Gravity.CENTER
        })

        setContentView(layout)
    }

    private fun startTimer() {
        timer?.cancel()
        startButton.isEnabled = false
        status.text = "Temporizador ativo."

        timer = object : CountDownTimer(remaining, 250) {
            override fun onTick(ms: Long) {
                remaining = ms
                display.text = formatTime(ms)
            }

            override fun onFinish() {
                timer = null
                remaining = 0
                display.text = "00:00"
                status.text = "Tempo terminado!"
                startButton.isEnabled = true
            }
        }.start()
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000).coerceAtLeast(0)
        return "%02d:%02d".format(seconds / 60, seconds % 60)
    }
}
