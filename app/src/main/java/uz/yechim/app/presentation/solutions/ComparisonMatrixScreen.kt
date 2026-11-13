package uz.yechim.app.presentation.solutions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uz.yechim.app.core.network.RetrofitClient
import uz.yechim.app.core.theme.*
import uz.yechim.app.data.model.SolverDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonMatrixScreen(
    onSolutionSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }

    var solvers by remember { mutableStateOf<List<SolverDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Barcha", "Eng yaxshi", "Mashhur")

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val res = apiService.getSolvers()
                if (res.isSuccessful) solvers = res.body()?.data ?: emptyList()
            } catch (_: Exception) {
            } finally { isLoading = false }
        }
    }

    val displaySolvers = remember(solvers, selectedTab) {
        when (selectedTab) {
            1 -> solvers.sortedBy { it.rank }
            2 -> solvers.sortedByDescending { it.solvedCount ?: 0 }
            else -> solvers
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Yechimlar", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimaryLight) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceLight,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = { onSolutionSelected("general") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yechim taklif qilish", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Filter tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, label ->
                    FilterChip(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(label, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = YechimPurple,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceLight,
                            labelColor = TextSecondaryLight
                        )
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = YechimPurple)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displaySolvers) { solver ->
                        SolverCard(
                            solver = solver,
                            onClick = { onSolutionSelected(solver.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SolverCard(
    solver: SolverDto,
    onClick: () -> Unit
) {
    val sampleTitles = listOf(
        "Yangi yashil hududlar yaratish va mavjud daraxtlarni asrash",
        "Zamonaviy ekotizim loyihasi",
        "Smart monitoring tizimi orqali daraxtlarni nazorat qilish"
    )
    val sampleTexts = listOf(
        "Shahar bo'ylab yangi yashil zona ekish va mavjud daraxtlarni parvarishlash...",
        "Smart monitoring tizimi orqali daraxtlarni nazorat qilish va parvarish...",
        "AI asosida muhit monitoring va ekologik muvozanatni ta'minlash..."
    )
    val titleIdx = (solver.rank - 1).coerceIn(0, sampleTitles.lastIndex)
    val tagColors = listOf(
        Color(0xFF10B981) to Color(0xFFECFDF5),
        Color(0xFF6366F1) to Color(0xFFEEF2FF),
        Color(0xFFF97316) to Color(0xFFFFF7ED)
    )
    val tag = listOf("Yaxshi yechim", "Top yechim", "Innovatsion")[titleIdx]
    val (tagFg, tagBg) = tagColors[titleIdx]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder.copy(alpha = 0.6f))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: avatar + name + likes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(YechimPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(solver.name?.take(1)?.uppercase() ?: "S", fontWeight = FontWeight.Bold, color = YechimPurple, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(solver.name ?: "Solver #${solver.rank}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryLight)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(15.dp))
                    }
                    Text(solver.username ?: "IT mutaxassis", fontSize = 12.sp, color = TextSecondaryLight)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${(100..400).random()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(sampleTitles[titleIdx], fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryLight, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(sampleTexts[titleIdx], fontSize = 12.sp, color = TextSecondaryLight, maxLines = 2, lineHeight = 18.sp)

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: views + comments + tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextSecondaryLight)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${(10000..30000).random()}", fontSize = 11.sp, color = TextSecondaryLight)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Comment, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextSecondaryLight)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${(80..200).random()}", fontSize = 11.sp, color = TextSecondaryLight)
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(tagBg).padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(tag, fontSize = 11.sp, color = tagFg, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
