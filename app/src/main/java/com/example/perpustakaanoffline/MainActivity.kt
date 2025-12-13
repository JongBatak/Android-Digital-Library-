package com.example.perpustakaanoffline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Import lifecycle compose extension
import com.example.perpustakaanoffline.admin.AdminDashboardScreen
import com.example.perpustakaanoffline.admin.AdminViewModel
import com.example.perpustakaanoffline.ui.theme.PerpustakaanOfflineTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerpustakaanOfflineTheme {
                val state = viewModel.uiState.collectAsStateWithLifecycle()
                AdminDashboardScreen(
                    state = state.value,
                    onAddBook = viewModel::addRandomBook,
                    onDeleteBook = viewModel::deleteBook,
                    onRefreshStats = viewModel::refreshStats
                )
            }
        }
    }
}
