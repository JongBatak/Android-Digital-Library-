package com.example.perpustakaanoffline.admin

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    state: AdminUiState,
    onAddBook: () -> Unit,
    onDeleteBook: (Long) -> Unit,
    onRefreshStats: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    Button(onClick = onRefreshStats) {
                        Text("Refresh Stats")
                    }
                }
            )

            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { StatSection(state.stats) }
                item { BookSection(state.books, onAddBook, onDeleteBook) }
                item { AccountSection(state.accounts) }
                item { AnalyticsSection(state.analytics) }
            }
        }
    }
}

@Composable
private fun StatSection(stats: List<AdminStat>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            StatCard(stat)
        }
    }
}

@Composable
private fun RowScope.StatCard(stat: AdminStat) {
    val infiniteTransition = rememberInfiniteTransition(label = "statPulse")
    val animatedBorder by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAnim"
    )

    Card(
        modifier = Modifier
            .weight(1f)
            .height(130.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F33)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Text(stat.label, style = MaterialTheme.typography.labelMedium, color = Color(0xFFB0B3C1))
                Spacer(modifier = Modifier.height(12.dp))
                Text(stat.value.toString(), style = MaterialTheme.typography.displaySmall, color = Color.White)
            }
            Text(
                text = if (stat.deltaPercent >= 0) "+${"%.1f".format(stat.deltaPercent)}%" else "${"%.1f".format(stat.deltaPercent)}%",
                color = if (stat.deltaPercent >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    brush = Brush.linearGradient(listOf(Color(0xFFFF8E53), Color(0xFFFF6A00))),
                    startAngle = -90f,
                    sweepAngle = 270f * animatedBorder,
                    useCenter = false,
                    alpha = 0.4f,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
private fun BookSection(
    books: List<AdminBook>,
    onAddBook: () -> Unit,
    onDelete: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kelola Buku", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onAddBook) { Text("Tambah") }
            }
            Spacer(Modifier.height(12.dp))
            books.forEach { book ->
                AdminBookRow(book, onDelete)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AdminBookRow(book: AdminBook, onDelete: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(book.title, fontWeight = FontWeight.Bold)
            Text(book.author, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(book.category, color = Color(0xFF6A1B9A))
                Text(book.format, color = Color(0xFFFF6A00))
            }
        }
        Text(
            text = "Hapus",
            color = Color(0xFFE53935),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onDelete(book.id) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AccountSection(accounts: List<AdminAccount>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Kelola Akun", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            accounts.forEach { account ->
                AccountRow(account)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AccountRow(account: AdminAccount) {
    val color = if (account.gender == Gender.MALE) Color(0xFF2196F3) else Color(0xFFFF69B4)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF0F0F5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(account.name, fontWeight = FontWeight.Bold)
            Text(account.username, color = Color.Gray)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(if (account.gender == Gender.MALE) "Laki-laki" else "Perempuan", color = color)
        }
    }
}

@Composable
private fun AnalyticsSection(analytics: List<BookAnalytics>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Analitik Buku", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            analytics.forEach { item ->
                AnalyticsRow(item)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AnalyticsRow(item: BookAnalytics) {
    var animatedProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(item.title) {
        animatedProgress = 0f
        val duration = 1800
        val frameMillis = 16L
        var elapsed = 0
        while (elapsed < duration) {
            animatedProgress = (elapsed.toFloat() / duration) * item.popularity
            elapsed += frameMillis.toInt()
            delay(frameMillis)
        }
        animatedProgress = item.popularity
    }

    Column {
        Text(item.title, fontWeight = FontWeight.Bold)
        Text("${item.minutesRead} menit dibaca", color = Color.Gray)
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF8E53), Color(0xFFFF6A00))))
            )
        }
    }
}
