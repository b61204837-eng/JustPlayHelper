package com.example.justplayhelper

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AdDetectionService : AccessibilityService() {

    companion object {
        private const val TAG = "JustPlayHelper_Service"
        private const val PLAY_STORE_PACKAGE = "com.android.vending"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return

        // 1. INTERCEÇÃO DA GOOGLE PLAY STORE
        val currentPackage = event.packageName?.toString()
        if (currentPackage == PLAY_STORE_PACKAGE) {
            Log.d(TAG, "Redirecionamento para a Play Store detetado! Forçando retorno...")
            // Simula o clique no botão físico de 'Voltar' do sistema Android
            performGlobalAction(GLOBAL_ACTION_BACK)
            rootNode.recycle()
            return
        }

        // 2. PROCESSAMENTO E FECHO DE ANÚNCIOS ATIVOS
        analisarEFecharAnuncios(rootNode)
        rootNode.recycle()
    }

    private fun analisarEFecharAnuncios(rootNode: AccessibilityNodeInfo) {
        // Mapeamento extensivo de termos globais em vários idiomas para botões de fecho
        val dicionarioFechar = listOf(
            "fechar", "close", "skip", "pular", "dismiss", "ocultar", "hide",
            "✕", "x", "×", "●", "关闭", "itálico", "continuar para o app", "prosseguir"
        )

        // Estrutura de IDs de recursos (nomes técnicos dados pelos SDKs de anúncios ao botão X)
        val fragmentosDeIdsAnuncios = listOf(
            "close", "skip", "cancel", "dismiss", "btn_close", "close_button", 
            "interstitial", "tt_video_ad_close", "anythink", "applovin", "admob"
        )

        // Executa varredura profunda e interativa na hierarquia visível do ecrã
        varrerArvoreComponentes(rootNode, dicionarioFechar, fragmentosDeIdsAnuncios)
    }

    private fun varrerArvoreComponentes(
        node: AccessibilityNodeInfo?, 
        termosTexto: List<String>, 
        fragmentosId: List<String>
    ) {
        if (node == null) return

        // Validação A: Detetar correspondência por Texto Visível ou Conteúdo Descritivo
        val textoDoElemento = node.text?.toString()?.lowercase() ?: ""
        val descricaoAcessivel = node.contentDescription?.toString()?.lowercase() ?: ""

        val correspondeTexto = termosTexto.any { termo -> 
            textoDoElemento == termo || descricaoAcessivel.contains(termo) 
        }

        // Validação B: Detetar correspondência pelo ID do Recurso Técnico (nos bastidores)
        val idDoRecurso = node.viewIdResourceName?.lowercase() ?: ""
        val correspondeId = fragmentosId.any { fragmento -> 
            idDoRecurso.contains(fragmento) 
        }

        // AÇÃO: Se houver correspondência e o componente permitir cliques, executa a ação de toque
        if ((correspondeTexto || correspondeId) && node.isClickable) {
            Log.d(TAG, "Gatilho de fecho encontrado! Texto: '$textoDoElemento' | ID: '$idDoRecurso'")
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return // Para o ciclo nesta ramificação para evitar múltiplos cliques em simultâneo
        }

        // Algoritmo de busca em profundidade recursiva pelos nós filhos
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            varrerArvoreComponentes(child, termosTexto, fragmentosId)
            child?.recycle() // Importante libertar memória do ponteiro nativo do Android após a leitura
        }
    }

    override fun onInterrupt() {
        Log.e(TAG, "O serviço de acessibilidade do JustPlayHelper foi interrompido.")
    }
}
