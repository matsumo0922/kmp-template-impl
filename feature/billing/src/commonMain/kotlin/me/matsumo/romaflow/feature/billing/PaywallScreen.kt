package me.matsumo.romaflow.feature.billing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import me.matsumo.romaflow.core.billing.model.SubscriptionPlan
import me.matsumo.romaflow.core.repository.ProductInfo
import me.matsumo.romaflow.core.resource.Res
import me.matsumo.romaflow.core.resource.common_close
import me.matsumo.romaflow.core.resource.paywall_error_no_subscription_to_restore
import me.matsumo.romaflow.core.resource.paywall_error_purchase_failed
import me.matsumo.romaflow.core.ui.screen.AsyncLoadContents
import me.matsumo.romaflow.feature.billing.components.PaywallFeatureList
import me.matsumo.romaflow.feature.billing.components.PaywallFooter
import me.matsumo.romaflow.feature.billing.components.PaywallHeader
import me.matsumo.romaflow.feature.billing.components.PaywallPlanSelector
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Suppress("UnusedParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PaywallScreen(
    source: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaywallViewModel = koinViewModel(),
) {
    val uriHandler = LocalUriHandler.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val purchaseState by viewModel.purchaseState.collectAsStateWithLifecycle()
    val selectedPlan by viewModel.selectedPlan.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnBack by rememberUpdatedState(onBack)

    val purchaseFailedMessage = stringResource(Res.string.paywall_error_purchase_failed)
    val noSubscriptionMessage = stringResource(Res.string.paywall_error_no_subscription_to_restore)

    LaunchedEffect(purchaseState) {
        when (purchaseState) {
            is PurchaseUiState.Success -> {
                currentOnBack()
            }

            is PurchaseUiState.PurchaseFailed -> {
                snackbarHostState.showSnackbar(purchaseFailedMessage)
            }

            is PurchaseUiState.NoSubscriptionToRestore -> {
                snackbarHostState.showSnackbar(noSubscriptionMessage)
            }

            is PurchaseUiState.Error -> {
                snackbarHostState.showSnackbar((purchaseState as PurchaseUiState.Error).message)
            }

            else -> {}
        }
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = viewModel::fetch,
    ) { state ->
        PaywallContent(
            modifier = Modifier.fillMaxSize(),
            products = state.products,
            selectedPlan = selectedPlan,
            purchaseState = purchaseState,
            snackbarHostState = snackbarHostState,
            onPlanSelect = viewModel::selectPlan,
            onPurchaseClick = viewModel::purchase,
            onRestoreClick = viewModel::restore,
            onBackClick = onBack,
            onTermsClick = { uriHandler.openUri("https://www.matsumo.me/application/all/team_of_service") },
            onPrivacyClick = { uriHandler.openUri("https://www.matsumo.me/application/all/privacy_policy") },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaywallContent(
    products: ImmutableList<ProductInfo>,
    selectedPlan: SubscriptionPlan,
    purchaseState: PurchaseUiState,
    snackbarHostState: SnackbarHostState,
    onPlanSelect: (SubscriptionPlan) -> Unit,
    onPurchaseClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onBackClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading = purchaseState is PurchaseUiState.Loading

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_close),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            PaywallHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )

            PaywallFeatureList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            PaywallPlanSelector(
                modifier = Modifier.fillMaxWidth(),
                products = products,
                selectedPlan = selectedPlan,
                onPlanSelect = onPlanSelect,
            )

            PaywallFooter(
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                onPurchaseClick = onPurchaseClick,
                onRestoreClick = onRestoreClick,
                onTermsClick = onTermsClick,
                onPrivacyClick = onPrivacyClick,
            )
        }
    }
}
