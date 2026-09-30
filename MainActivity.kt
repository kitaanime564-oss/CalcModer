package com.calculator.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.calculator.app.ads.AdMobManager
import com.calculator.app.ui.CalculatorScreen
import com.calculator.app.ui.CalculatorViewModel
import com.calculator.app.ui.theme.ModernCalculatorTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var adMobManager: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Google Mobile Ads SDK before launching Compose UI
        MobileAds.initialize(this) { status ->
            // AdMob Initialization completed
        }

        // 2. Initialize AdMob manager to handle preloading and showing Interstitials
        adMobManager = AdMobManager(this)
        viewModel.setAdMobManager(adMobManager)

        // 3. Launch Material Design 3 Jetpack Compose UI
        setContent {
            ModernCalculatorTheme {
                CalculatorScreen(viewModel = viewModel)
            }
        }
    }
}