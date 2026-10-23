package com.example.justplayhelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Criação de um botão diretamente por código para simplificar e evitar dependências de XML de layout
        val button = Button(this).apply {
            text = "Ativar JustPlay Helper"
            textSize = 18f
            setPadding(40, 40, 40, 40)
        }

        setContentView(button)

        button.setOnClickListener {
            if (!isAccessibilityServiceEnabled(this, AdDetectionService::class.java)) {
                // Abre o ecrã de definições de acessibilidade do sistema Android
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                Toast.makeText(this, "Por favor, ative o 'JustPlay Helper' na lista", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "O serviço já está ativo e a funcionar sozinho!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Função auxiliar para verificar se o serviço de acessibilidade já foi ativado pelo utilizador.
     */
    private fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
        val expectedComponentName = "${context.packageName}/${service.name}"
        val settingValue = Settings.Secure.getString(
            context.content_contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(settingValue)
        while (splitter.hasNext()) {
            val componentName = splitter.next()
            if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}
