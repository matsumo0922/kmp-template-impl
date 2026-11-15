package me.matsumo.romaflow.feature.billing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** 課金画面のルートと遷移元。 */
@Serializable
data class PaywallRoute(val source: String) : NavKey

fun EntryProviderScope<NavKey>.paywallEntry(onBack: () -> Unit) {
    entry<PaywallRoute> { route ->
        PaywallScreen(
            modifier = Modifier.fillMaxSize(),
            source = route.source,
            onBack = onBack,
        )
    }
}
