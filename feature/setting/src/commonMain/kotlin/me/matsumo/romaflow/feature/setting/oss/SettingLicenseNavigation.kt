package me.matsumo.romaflow.feature.setting.oss

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** OSS ライセンス画面のルート。 */
@Serializable
data object OpenSourceLicensesRoute : NavKey

fun EntryProviderScope<NavKey>.openSourceLicensesEntry(onBack: () -> Unit) {
    entry<OpenSourceLicensesRoute> {
        SettingLicenseRoute(
            modifier = Modifier.fillMaxSize(),
            onBack = onBack,
        )
    }
}
