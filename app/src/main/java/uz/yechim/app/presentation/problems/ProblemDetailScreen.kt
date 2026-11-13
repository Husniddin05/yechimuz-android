package uz.yechim.app.presentation.problems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uz.yechim.app.core.network.RetrofitClient
import uz.yechim.app.core.theme.*
import uz.yechim.app.data.model.ProblemDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemDetailScreen(
    problemId: String,
    onBack: () -> Unit,
    onViewAllSolutions: () -> Unit,
    onSubmitSolution: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }

    var problem by remember { mutableStateOf<ProblemDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(problemId) {
        coroutineScope.launch {
            try {
                val res = apiService.getProblemById(problemId)
                if (res.isSuccessful) {
                    problem = res.body()?.data
                }
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    val title = problem?.title ?: "Shahar hududida daraxtlar sonini oshirish uchun yechim topish"
    val categoryName = problem?.category?.nameUz ?: "Ekologiya"
    val description = problem?.rawDescription?.takeIf { it.isNotBlank() }
        ?: "Toshkent shahrida daraxtlar soni kamayib bormoqda. Bu esa ekologik muvozanatga salbiy ta'sir ko'rsatmoqda. Shaharni ko'kalamzorlashtirish, qurg'oqchilikka chidamli daraxtlarni ekish va ularni parvarishlash bo'yicha amaliy innovatsion yechimlar kerak."

    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = {
            // ── Bottom Persistent Action Bar (Saqlash & Taklif berish) ──
            Surface(
                color = SurfaceLight,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { isSaved = !isSaved },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSaved) YechimPurpleLight else SurfaceLight,
                            contentColor = if (isSaved) YechimPurple else TextPrimaryLight
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(if (isSaved) YechimPurple else CardBorder, if (isSaved) YechimPurple else CardBorder)
                            )
                        )
                    ) {
                        Icon(
                            if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isSaved) ErrorRed else TextSecondaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isSaved) "Saqlandi" else "Saqlash",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = { onSubmitSolution(problemId) },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Taklif berish", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YechimPurple)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Top Hero Visual Banner with Back & Share buttons ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF047857), // Forest green gradient for nature/city
                                    Color(0xFF065F46),
                                    Color(0xFF064E3B)
                                )
                            )
                        )
                ) {
                    // Decorative Nature Elements
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌳 🏙️ 🌿",
                            fontSize = 44.sp
                        )
                    }

                    // Navigation Icons on top of Hero
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }

                        IconButton(
                            onClick = { /* Share */ },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Ulashish", tint = Color.White)
                        }
                    }

                    // Category Pill Badge on bottom left of hero
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(YechimPurple)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            categoryName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // ── Main Content Card ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Problem Title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Meta statistics row: Views, Comments, Time
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("12.5K", fontSize = 12.sp, color = TextSecondaryLight)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Comment, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("124", fontSize = 12.sp, color = TextSecondaryLight)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("2 kun oldin", fontSize = 12.sp, color = TextSecondaryLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = CardBorder.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Muammo tavsifi ──
                    Text(
                        text = "Muammo tavsifi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryLight,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = CardBorder.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Taklif etilgan yechimlar header ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Taklif etilgan yechimlar (24)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimaryLight
                        )
                        TextButton(onClick = onViewAllSolutions) {
                            Text(
                                "Barchasi →",
                                color = YechimPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Solution preview card (From mockup) ──
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewAllSolutions() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(YechimPurpleLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("A", fontWeight = FontWeight.Bold, color = YechimPurple, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Azizbek Karimov", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryLight)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                                    }
                                    Text("IT mutaxassis", fontSize = 12.sp, color = TextSecondaryLight)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("342", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Yangi yashil hududlar yaratish va mavjud daraxtlarni asrash...",
                                fontSize = 13.sp,
                                color = TextPrimaryLight,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
