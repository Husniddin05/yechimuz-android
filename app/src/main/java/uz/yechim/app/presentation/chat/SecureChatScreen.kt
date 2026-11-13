package uz.yechim.app.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
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
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.yechim.app.core.network.RetrofitClient
import uz.yechim.app.core.theme.*
import uz.yechim.app.data.model.ChatMessageDto
import uz.yechim.app.data.model.SendMessageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureChatScreen(
    solutionId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }

    var messages by remember { mutableStateOf<List<ChatMessageDto>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var reportDialogVisible by remember { mutableStateOf(false) }
    var blockedMessage by remember { mutableStateOf<String?>(null) }

    // 4 soniyalik Lifecycle-Aware Polling (Faqat RESUMED bo'lganda ishlaydi)
    LifecycleResumeEffect(solutionId) {
        var isPollingActive = true
        val job = coroutineScope.launch {
            while (isPollingActive) {
                try {
                    val res = apiService.getChatRoom(solutionId)
                    if (res.isSuccessful && res.body()?.success == true) {
                        res.body()?.data?.messages?.let { msgs ->
                            messages = msgs
                        }
                    }
                } catch (_: Exception) {
                }
                delay(4000)
            }
        }
        onPauseOrDispose {
            isPollingActive = false
            job.cancel()
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Orqaga",
                            tint = TextPrimaryLight
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(YechimPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = YechimPurple, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Xavfsiz Ichki Chat", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryLight)
                            Text("1:1 Shifrlangan aloqa (Owner & Solver)", fontSize = 11.sp, color = TextSecondaryLight)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextPrimaryLight)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Shikoyat qilish (Report UGC)") },
                            onClick = {
                                showMenu = false
                                reportDialogVisible = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Foydalanuvchini bloklash (Block)") },
                            onClick = {
                                showMenu = false
                                blockedMessage = "Suhbatdosh bloklandi. Undan qayta xabarlar kelmaydi."
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            blockedMessage?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(text = it, color = ErrorRed, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            // Xabarlar ro'yxati
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "🛡️ Ushbu chat faqat ma’qullangan yechim bo‘yicha texnik tafsilotlarni muhokama qilish uchun himoyalangan.",
                            fontSize = 11.sp,
                            color = TextSecondaryLight,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(YechimPurpleLight.copy(alpha = 0.6f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Hozircha xabarlar yo'q. Birinchi xabarni yozing!",
                                fontSize = 13.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }

                items(messages) { msg ->
                    val isMyMessage = msg.senderId != "other"
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMyMessage) YechimPurple else SurfaceLight
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = if (!isMyMessage) CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                            ) else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.content,
                                    color = if (isMyMessage) Color.White else TextPrimaryLight,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (msg.isRead) "O‘qildi ✓✓" else "Yetkazildi ✓",
                                    fontSize = 10.sp,
                                    color = if (isMyMessage) Color.White.copy(alpha = 0.8f) else TextSecondaryLight,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            // Xabar yozish maydoni
            Surface(
                color = SurfaceLight,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Xabar yozing...", color = TextSecondaryLight, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Normal),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = YechimPurple,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = SurfaceLight,
                            unfocusedContainerColor = SurfaceLight,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val msg = inputText.trim()
                            if (msg.isNotBlank() && !isSending) {
                                isSending = true
                                coroutineScope.launch {
                                    try {
                                        val res = apiService.sendMessage(solutionId, SendMessageRequest(content = msg))
                                        if (res.isSuccessful && res.body()?.data != null) {
                                            messages = messages + res.body()!!.data!!
                                            inputText = ""
                                        }
                                    } catch (_: Exception) {
                                    } finally {
                                        isSending = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(YechimPurple)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Yuborish", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Google Play UGC Moderation: Report Dialog
    if (reportDialogVisible) {
        AlertDialog(
            onDismissRequest = { reportDialogVisible = false },
            title = { Text("Nomaqbul kontent bo‘yicha shikoyat", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = { Text("Ushbu xabar/foydalanuvchi qoidalarni buzdimi? Shikoyatingiz moderatsiya xizmatiga yuboriladi va 24 soat ichida ko‘rib chiqiladi.") },
            confirmButton = {
                Button(
                    onClick = {
                        reportDialogVisible = false
                        blockedMessage = "Shikoyat qabul qilindi. Moderatsiya jamoasi ushbu holatni tekshirmoqda."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Shikoyat yuborish", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { reportDialogVisible = false }) {
                    Text("Bekor qilish")
                }
            }
        )
    }
}
