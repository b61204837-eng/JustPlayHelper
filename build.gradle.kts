// Bloco de plugins raiz corrigido e estruturado para compatibilidade
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    
    // CORREÇÃO: Adicionado obrigatoriamente se o projeto usar Jetpack Compose no Kotlin 2.0+
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}
