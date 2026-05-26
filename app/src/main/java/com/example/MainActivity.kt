package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ui.KimiViewModel
import com.example.ui.KimiViewModelFactory
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = KimiViewModelFactory(applicationContext)
        val viewModel = ViewModelProvider(this, factory)[KimiViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkNavyBackground
                ) {
                    KimiRealApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun KimiRealApp(viewModel: KimiViewModel) {
    val prijavljeniKorisnik by viewModel.prijavljeniKorisnik.collectAsState()

    AnimatedContent(
        targetState = prijavljeniKorisnik != null,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "AppAuthenticationTransition"
    ) { isLoggedIn ->
        if (isLoggedIn) {
            MainAppScreen(viewModel = viewModel)
        } else {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {}
            )
        }
    }
}
