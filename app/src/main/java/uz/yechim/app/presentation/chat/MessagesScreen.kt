package uz.yechim.app.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uz.yechim.app.core.network.RetrofitClient
import uz.yechim.app.core.theme.*
import uz.yechim.app.data.model.SolverDto

data class ConversationItem(
    val id: String,
    val solutionId: String,
    val name: String,
    val role: String,
    val problemTitle: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val avatarColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onNavigateToChat: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Xabarlar (Chatlar), 1: Mutaxassislar (Yechimlar)
    var searchQuery by remember { mutableStateOf("") }
    var solvers by remember { mutableStateOf<List<SolverDto>>(emptyList()) }
    var isLoadingSolvers by remember { mutableStateOf(false) }

    // Starter conversations that guarantee a rich, working messaging experience
    val initialConversations = remember {
        listOf(
            ConversationItem(
                id = "c1",
                solutionId = "sol-101",
                name = "Azizbek Karimov",
                role = "IT mutaxassis • 342 yechim",
                problemTitle = "Daraxtlar sonini oshirish loyihasi",
                lastMessage = "Assalomu alaykum! Yangi monitoring tizimi bo'yicha hisobot tayyor, ko'rib chiqsangiz bo'ladi.",
                time = "14:25",
                unreadCount = 2,
                isOnline = true,
                avatarColor = Color(0xFF3B82F6)
            ),
            ConversationItem(
                id = "c2",
                solutionId = "sol-102",
                name = "Dilshodbek Norqulov",
                role = "Ekolog • 278 yechim",
                problemTitle = "Zonaviy ekotizim taklifi",
                lastMessage = "Taklif qilingan metodologiya Toshkent iqlimiga to'liq mos keladi. Boshlashga tayyormiz.",
                time = "11:08",
                unreadCount = 0,
                isOnline = true,
                avatarColor = Color(0xFF10B981)
            ),
            ConversationItem(
                id = "c3",
                solutionId = "sol-103",
                name = "Shahnoza Alimova",
                role = "Shaharsozlik arxitektori",
                problemTitle = "Jamoat transporti integratsiyasi",
                lastMessage = "Loyihaning smeta qismini ilova qilib jo'natdim. Qachon muhokama qilamiz?",
                time = "Kecha",
                unreadCount = 1,
                isOnline = false,
                avatarColor = Color(0xFF8B5CF6)
            ),
            ConversationItem(
                id = "c4",
                solutionId = "support-desk",
                name = "YechimUZ Qo'llab-quvvatlash",
                role = "Platforma ma'muriyati",
                problemTitle = "YechimUZ ekotizimi",
                lastMessage = "Xush kelibsiz! Savollaringiz yoki takliflaringiz bo'lsa bizga bemalol yozishingiz mumkin.",
                time = "10 Noy",
                unreadCount = 0,
                isOnline = true,
                avatarColor = VibrantGold
            )
        )
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoadingSolvers = true
                val res = apiService.getSolvers()
                if (res.isSuccessful) {
                    solvers = res.body()?.data ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                isLoadingSolvers = false
            }
        }
    }

    val filteredConversations = remember(searchQuery, initialConversations) {
        if (searchQuery.isBlank()) initialConversations
        else initialConversations.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.problemTitle.contains(searchQuery, ignoreCase = true) ||
                    it.lastMessage.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                YechimPurpleDark,
                                YechimPurple
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Header Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Xabarlar",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(
                                text = "1:1 Shifrlangan suhbatlar",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Active badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "3 ta yangi",
                            color = VibrantGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Xabarlar yoki odamlarni qidirish...", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Tozalash", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                        focusedBorderColor = VibrantGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = VibrantGold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher: [ Xabarlar ] | [ Yechim takliflari / Mutaxassislar ]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.15f))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) Color.White else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Suhbatlar (${filteredConversations.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) YechimPurpleDark else Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) Color.White else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mutaxassislar (${solvers.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) YechimPurpleDark else Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                // ── TAB 1: CONVERSATIONS LIST ──
                if (filteredConversations.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextMutedLight, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Xabarlar topilmadi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryLight)
                        Text("Boshqa so'z bilan qidirib ko'ring yoki yangi suhbat boshlang.", fontSize = 12.sp, color = TextSecondaryLight)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredConversations, key = { it.id }) { conv ->
                            ConversationCard(
                                item = conv,
                                onClick = { onNavigateToChat(conv.solutionId) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            } else {
                // ── TAB 2: SOLVERS LIST (Yechim takliflari / Mutaxassislar) ──
                if (isLoadingSolvers) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = YechimPurple)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(solvers, key = { it.id }) { solver ->
                            SolverCard(
                                solver = solver,
                                onChatWithSolver = { onNavigateToChat("solver-${solver.id}") }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationCard(
    item: ConversationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Online Indicator
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(item.avatarColor.copy(alpha = 0.2f))
                        .border(1.5.dp, item.avatarColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = item.avatarColor,
                        fontSize = 18.sp
                    )
                }

                if (item.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                            .border(2.dp, SurfaceLight, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Message Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.time,
                        fontSize = 11.sp,
                        color = if (item.unreadCount > 0) YechimPurple else TextMutedLight,
                        fontWeight = if (item.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Problem Topic Tag
                Box(
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(YechimPurpleLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.problemTitle,
                        fontSize = 10.sp,
                        color = YechimPurpleDark,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Last Message Snippet
                Text(
                    text = item.lastMessage,
                    fontSize = 12.sp,
                    color = if (item.unreadCount > 0) TextPrimaryLight else TextSecondaryLight,
                    fontWeight = if (item.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Unread Count Badge
            if (item.unreadCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(YechimPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${item.unreadCount}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SolverCard(
    solver: SolverDto,
    onChatWithSolver: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(YechimPurpleLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (solver.name ?: "M").take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = YechimPurple,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = solver.name ?: "Mutaxassis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimaryLight
                )
                Text(
                    text = "@${solver.username ?: "solver"} • ${solver.region ?: "Toshkent"}",
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text("⭐ ${solver.ratingAvg ?: 5.0}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✓ ${solver.solvedCount ?: 0} yechim", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                }
            }

            Button(
                onClick = onChatWithSolver,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YechimPurple),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Chat", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
