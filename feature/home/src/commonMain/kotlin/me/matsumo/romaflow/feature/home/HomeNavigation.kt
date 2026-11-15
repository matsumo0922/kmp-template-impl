package me.matsumo.romaflow.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** ホーム画面のルート。 */
@Serializable
data object HomeRootRoute : NavKey

fun EntryProviderScope<NavKey>.homeEntry(onSettingClick: () -> Unit) {
    entry<HomeRootRoute> {
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            onSettingClick = onSettingClick,
        )
    }
}
