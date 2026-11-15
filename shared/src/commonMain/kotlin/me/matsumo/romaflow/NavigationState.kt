package me.matsumo.romaflow

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import me.matsumo.romaflow.feature.billing.PaywallRoute
import me.matsumo.romaflow.feature.home.HomeRootRoute
import me.matsumo.romaflow.feature.setting.SettingRoute
import me.matsumo.romaflow.feature.setting.oss.OpenSourceLicensesRoute

/** Android と iOS でバックスタックを復元するための、feature ごとのルート登録。 */
internal val navigationStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeRootRoute::class, HomeRootRoute.serializer())
            subclass(SettingRoute::class, SettingRoute.serializer())
            subclass(PaywallRoute::class, PaywallRoute.serializer())
            subclass(OpenSourceLicensesRoute::class, OpenSourceLicensesRoute.serializer())
        }
    }
}
