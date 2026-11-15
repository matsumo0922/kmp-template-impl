package me.matsumo.romaflow.feature.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** 設定画面のルート。 */
@Serializable
data object SettingRoute : NavKey

fun EntryProviderScope<NavKey>.settingEntry(
    onBack: () -> Unit,
    onUpgradeClick: () -> Unit,
    onOpenSourceLicensesClick: () -> Unit,
) {
    entry<SettingRoute> {
        SettingScreen(
            modifier = Modifier.fillMaxSize(),
            onBack = onBack,
            onUpgradeClick = onUpgradeClick,
            onOpenSourceLicensesClick = onOpenSourceLicensesClick,
        )
    }
}
