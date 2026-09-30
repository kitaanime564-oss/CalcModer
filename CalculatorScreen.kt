package com.calculator.app.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calculator.app.ads.AdaptiveBannerAd
import com.calculator.app.ads.AdMobManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val activity = LocalContext.current as? Activity

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Production AdMob Adaptive Banner Ad continuously docked at the bottom
            AdaptiveBannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                adUnitId = AdMobManager.BANNER_AD_UNIT_ID
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLandscape) {
                LandscapeCalculatorLayout(
                    uiState = uiState,
                    viewModel = viewModel,
                    activity = activity
                )
            } else {
                PortraitCalculatorLayout(
                    uiState = uiState,
                    viewModel = viewModel,
                    activity = activity
                )
            }

            // Calculation History Modal Bottom Sheet
            if (uiState.isHistoryOpen) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.toggleHistory(false) },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    HistoryBottomSheetContent(
                        history = uiState.history,
                        onItemClick = { item -> viewModel.loadHistoryItem(item) },
                        onClearAll = { viewModel.clearHistory(activity) }
                    )
                }
            }
        }
    }
}

// ... Additional Compose layouts (Portrait, Landscape, Display, Keypad grids)
