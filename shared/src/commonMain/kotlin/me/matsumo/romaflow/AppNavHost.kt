package me.matsumo.romaflow

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import me.matsumo.romaflow.feature.billing.PaywallRoute
import me.matsumo.romaflow.feature.billing.paywallEntry
import me.matsumo.romaflow.feature.home.HomeRootRoute
import me.matsumo.romaflow.feature.home.homeEntry
import me.matsumo.romaflow.feature.setting.SettingRoute
import me.matsumo.romaflow.feature.setting.oss.OpenSourceLicensesRoute
import me.matsumo.romaflow.feature.setting.oss.openSourceLicensesEntry
import me.matsumo.romaflow.feature.setting.settingEntry

@Composable
internal fun AppNavHost(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(navigationStateConfiguration, HomeRootRoute)
    val goBack = dropUnlessResumed { backStack.removeLastOrNull() }
    val slideDistance = with(LocalDensity.current) { 30.dp.roundToPx() }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        onBack = goBack,
        transitionSpec = { sharedAxisX(SlideDirection.Start, slideDistance) },
        popTransitionSpec = { sharedAxisX(SlideDirection.End, slideDistance) },
        predictivePopTransitionSpec = { sharedAxisX(SlideDirection.End, slideDistance) },
        entryProvider = entryProvider {
            homeEntry(
                onSettingClick = dropUnlessResumed { backStack.add(SettingRoute) },
            )
            settingEntry(
                onBack = goBack,
                onUpgradeClick = dropUnlessResumed { backStack.add(PaywallRoute("setting")) },
                onOpenSourceLicensesClick = dropUnlessResumed { backStack.add(OpenSourceLicensesRoute) },
            )
            paywallEntry(onBack = goBack)
            openSourceLicensesEntry(onBack = goBack)
        },
    )
}

/**
 * 出ていく画面と入ってくる画面が横軸を共有する遷移を返す。
 *
 * 予測型戻るは進捗を時間へ写して seek するため、時間に対して位置が単調に進む spec でなければ
 * ならない。`MotionScheme` の spatial spec は damping が 1 未満で行き過ぎてから戻るため、
 * 指を進めると画面が戻る向きへ動く。navigation3 の既定の予測型戻るが damping 1.0 の spring
 * だけで組まれているのも同じ理由である。ここでは時間基準の tween を使う。
 *
 * 移動量は Material の shared axis に合わせて [slideDistance] に抑える。全幅の移動量は方向で
 * 符号が変わるため、符号を保ったまま大きさだけを丸める。
 */
private fun AnimatedContentTransitionScope<Scene<NavKey>>.sharedAxisX(
    towards: SlideDirection,
    slideDistance: Int,
): ContentTransform {
    val slideSpec = tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing)
    val fadeSpec = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
    val offset: (Int) -> Int = { it.coerceIn(-slideDistance, slideDistance) }

    return ContentTransform(
        slideIntoContainer(
            towards = towards,
            animationSpec = slideSpec,
            initialOffset = offset,
        ) + fadeIn(fadeSpec),
        slideOutOfContainer(
            towards = towards,
            animationSpec = slideSpec,
            targetOffset = offset,
        ) + fadeOut(fadeSpec),
    )
}
