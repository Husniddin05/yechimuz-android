package uz.yechim.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uz.yechim.app.core.network.RetrofitClient
import uz.yechim.app.core.security.KeystoreManager
import uz.yechim.app.core.theme.*
import uz.yechim.app.data.model.CategoryDto
import uz.yechim.app.data.model.ProblemDto

@Composable
fun HomeScreen(
    onNavigateToCreateProblem: () -> Unit,
    onNavigateToMarketplace: (String?) -> Unit,
    onNavigateToProblemDetail: (String) -> Unit,
    onNavigateToSolutions: () -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }
    val keystoreManager = remember { KeystoreManager(context) }

    var problems by remember { mutableStateOf<List<ProblemDto>>(emptyList()) }
    var categories by remember { mutableStateOf<List<CategoryDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val rawUserName = keystoreManager.getUserName()
    val displayName = if (!rawUserName.isNullOrBlank() && rawUserName != "authenticated_user") {
        rawUserName.substringBefore(" ").ifEmpty { rawUserName }
    } else {
        "Foydalanuvchi"
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val pRes = apiService.getProblems()
                if (pRes.isSuccessful) problems = pRes.body()?.data ?: emptyList()
                val cRes = apiService.getCategories()
                if (cRes.isSuccessful) categories = cRes.body()?.data ?: emptyList()
            } catch (_: Exception) {
            } finally { isLoading = false }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            // ── Premium Distinctive Gradient Header ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                YechimPurpleDark,
                                YechimPurple,
                                YechimIndigo
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    // Top Row: Hamburger + Brand Emblem + Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Glassmorphic Menu Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable { onOpenDrawer() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menyu",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Center: Glowing Brand Logo & Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenDrawer() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .border(1.5.dp, VibrantGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Y",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Yechim",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    "UZ",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = VibrantGold,
                                    fontSize = 22.sp,
                                    letterSpacing = (-0.5).sp
                                )
                            }
                        }

                        // Right: Notification Bell & Profile Avatar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.18f))
                                    .clickable { showNotificationsDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                BadgedBox(badge = {
                                    Badge(
                                        containerColor = ErrorRed,
                                        modifier = Modifier.offset(x = (-2).dp, y = 2.dp)
                                    ) {
                                        Text("3", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Bildirishnomalar",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Profile Avatar
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .clickable { onOpenDrawer() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    displayName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Row: Greeting Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Salom, $displayName 👋",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Text(
                                "Bugun qanday muammoga yechim topamiz?",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }

                        // Quick action search pill
                        Surface(
                            onClick = { onNavigateToMarketplace(null) },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Qidiruv", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── Hero Banner Card (Exact match to Mockup Screen 2) ──
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF6D28D9),
                                        Color(0xFF4F46E5)
                                    )
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Yangi imkoniyatlar sari birga!",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = Color.White,
                                    lineHeight = 23.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Muammo va yechimlar dunyosiga qo'shiling",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = onNavigateToCreateProblem,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = YechimPurple
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        "Bosh sahifa →",
                                        color = YechimPurple,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Text("🚀", fontSize = 52.sp)
                        }
                    }
                }

                // ── Ommabop Yo'nalishlar (Category chips) ──
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Ommabop Yo'nalishlar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(categories) { cat ->
                            Surface(
                                onClick = { onNavigateToMarketplace(cat.slug) },
                                shape = RoundedCornerShape(20.dp),
                                color = SurfaceCardLight,
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(listOf(CardBorder, CardBorder))
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (cat.slug) {
                                            "talim" -> "🎓"
                                            "texnologiya" -> "💻"
                                            "ekologiya" -> "🌿"
                                            "iqtisodiyot" -> "📈"
                                            "soglik" -> "❤️"
                                            "transport" -> "🚌"
                                            else -> "💡"
                                        },
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        cat.nameUz,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = TextPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                }

                // ── So'nggi muammolar header ──
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "So'nggi muammolar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimaryLight
                        )
                        TextButton(
                            onClick = { onNavigateToMarketplace(null) }
                        ) {
                            Text(
                                "Barchasi →",
                                color = YechimPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // ── Problem Cards (High Contrast & Clear Typography) ──
                items(problems.take(6)) { problem ->
                    HomeProblemCard(
                        problem = problem,
                        onClick = { onNavigateToProblemDetail(problem.id) }
                    )
                }

                // ── Gold Trophy Banner (Mukofotlanadi) ──
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onNavigateToSolutions() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(Color(0xFFFDE68A), Color(0xFFFDE68A)))
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏆", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Eng yaxshi yechimlar mukofotlanadi!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = VibrantGold,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = YechimPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bildirishnomalar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Notification item 1
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = YechimPurpleLight.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("💡 Yangi yechim taklif qilindi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = YechimPurpleDark)
                            Text("Azizbek Karimov sizning muammoingizga yangi yechim kiritdi.", fontSize = 11.sp, color = TextPrimaryLight)
                            Text("5 daqiqa oldin", fontSize = 10.sp, color = TextMutedLight)
                        }
                    }

                    // Notification item 2
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardLight)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("💬 2 ta yangi xabar", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = YechimPurpleDark)
                            Text("Dilshodbek Norqulov sizga xabar yubordi.", fontSize = 11.sp, color = TextPrimaryLight)
                            Text("1 soat oldin", fontSize = 10.sp, color = TextMutedLight)
                        }
                    }

                    // Notification item 3
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardLight)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("✅ Tizim faollashtirildi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SuccessGreen)
                            Text("Profil ma'lumotlaringiz muvaffaqiyatli sinxronlashtirildi.", fontSize = 11.sp, color = TextPrimaryLight)
                            Text("Bugun", fontSize = 10.sp, color = TextMutedLight)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationsDialog = false
                        onNavigateToSolutions()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                ) {
                    Text("Xabarlarni ochish", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Yopish")
                }
            }
        )
    }
}

@Composable
fun HomeProblemCard(problem: ProblemDto, onClick: () -> Unit) {
    val isEcology = problem.category?.slug == "ekologiya" || problem.title.contains("daraxt", ignoreCase = true)
    val isTech = problem.category?.slug == "texnologiya" || problem.title.contains("noutbuk", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(listOf(CardBorder.copy(alpha = 0.7f), CardBorder.copy(alpha = 0.7f)))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Themed Visual Thumbnail (matching Mockup Image)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when {
                            isEcology -> Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF047857)))
                            isTech -> Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)))
                            problem.category?.slug == "talim" -> Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)))
                            else -> Brush.verticalGradient(listOf(YechimPurple, YechimPurpleDark))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        isEcology -> "🌳"
                        isTech -> "💻"
                        problem.category?.slug == "talim" -> "🎓"
                        problem.category?.slug == "iqtisodiyot" -> "📈"
                        else -> "💡"
                    },
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = problem.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimaryLight,
                    maxLines = 2,
                    lineHeight = 19.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondaryLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isEcology) "12.5K" else if (isTech) "8.7K" else "${(5000..20000).random()}",
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Comment,
                            contentDescription = null,
                            tint = TextSecondaryLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isEcology) "124" else if (isTech) "87" else "${(30..150).random()}",
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
