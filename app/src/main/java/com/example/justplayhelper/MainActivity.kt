package com.example.justplayhelper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("adcloser_settings", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 24)
        }

        layout.addView(TextView(this).apply {
            text = "AdCloser"
            textSize = 30f
            gravity = Gravity.CENTER
        })

        layout.addView(TextView(this).apply {
            text = "Deteção de anúncios e regresso da Google Play"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 20)
        })

        val closeSwitch = Switch(this).apply {
            text = "Tentar fechar anúncios reconhecidos"
            isChecked = prefs.getBoolean("auto_close", true)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("auto_close", checked).apply()
            }
        }
        layout.addView(closeSwitch)

        val returnSwitch = Switch(this).apply {
            text = "Regressar quando abrir a Google Play"
            isChecked = prefs.getBoolean("auto_return_play", true)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("auto_return_play", checked).apply()
            }
        }
        layout.addView(returnSwitch)

        layout.addView(Button(this).apply {
            text = "Ativar serviço de Acessibilidade"
            setOnClickListener {
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )
            }
        })

        layout.addView(Button(this).apply {
            text = "Testar aviso"
            setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "AdCloser está a funcionar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        layout.addView(TextView(this).apply {
            text = "O funcionamento depende dos elementos acessíveis de cada aplicação. Nem todos os anúncios podem ser fechados automaticamente."
            textSize = 13f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        })

        setContentView(layout)
    }
}