package me.matsumo.romaflow

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import kotlinx.serialization.json.Json
import me.matsumo.romaflow.feature.billing.PaywallRoute
import me.matsumo.romaflow.feature.home.HomeRootRoute
import me.matsumo.romaflow.feature.setting.SettingRoute
import me.matsumo.romaflow.feature.setting.oss.OpenSourceLicensesRoute
import kotlin.test.Test
import kotlin.test.assertEquals

/** feature ごとのルートと引数が保存後も復元できることを検証する。 */
class NavigationStateTest {
    @Test
    fun restoresBackStackOrderAndRouteArguments() {
        val backStack = NavBackStack<NavKey>(
            HomeRootRoute,
            SettingRoute,
            OpenSourceLicensesRoute,
            PaywallRoute("setting"),
        )
        val serializer = NavBackStackSerializer<NavKey>()
        val json = Json {
            serializersModule = navigationStateConfiguration.serializersModule
        }

        val savedState = json.encodeToString(serializer, backStack)
        val restored = json.decodeFromString(serializer, savedState)

        assertEquals(backStack.toList(), restored.toList())
    }
}
