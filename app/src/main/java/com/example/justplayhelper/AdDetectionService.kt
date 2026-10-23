package com.example.justplayhelper

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.widget.Toast

class AdDetectionService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString() ?: return

                if (packageName == "com.android.vending") {
                    Toast.makeText(
                        this,
                        "Google Play aberta. Podes voltar à aplicação anterior.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                val root = rootInActiveWindow ?: return
                inspectNodes(root)
            }
        }
    }

    private fun inspectNodes(node: AccessibilityNodeInfo?) {
        if (node == null) return

        val text = node.text?.toString()?.trim()?.lowercase() ?: ""
        val description =
            node.contentDescription?.toString()?.trim()?.lowercase() ?: ""

        val labels = listOf(
            text,
            description
        )

        val closeLabels = listOf(
            "close",
            "fechar",
            "skip ad",
            "ignorar anúncio",
            "ignorar anuncio"
        )

        if (labels.any { label ->
                closeLabels.any { keyword -> label.contains(keyword) }
            }) {
            // Reconhece possíveis opções de fecho.
            // Não carrega automaticamente: o texto pode pertencer
            // a outro elemento da aplicação.
        }

        for (i in 0 until node.childCount) {
            inspectNodes(node.getChild(i))
        }
    }

    override fun onInterrupt() {
        // O Android interrompeu o serviço.
    }
}
