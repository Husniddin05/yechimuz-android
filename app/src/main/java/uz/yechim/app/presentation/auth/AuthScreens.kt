package uz.yechim.app.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.yechim.app.core.network.RetrofitClient
import uz.yechim.app.core.security.KeystoreManager
import uz.yechim.app.core.theme.*
import uz.yechim.app.data.model.LoginRequest
import uz.yechim.app.data.model.RegisterRequest

// Reusable High-Contrast Colors for text fields (Always PURE BLACK text on light background)
@Composable
fun authInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedContainerColor = Color(0xFFF3F0FA),
    unfocusedContainerColor = Color(0xFFF8F7FC),
    focusedBorderColor = YechimPurple,
    unfocusedBorderColor = Color(0xFF94A3B8),
    focusedLabelColor = YechimPurple,
    unfocusedLabelColor = Color(0xFF334155),
    focusedLeadingIconColor = YechimPurple,
    unfocusedLeadingIconColor = Color(0xFF475569),
    focusedTrailingIconColor = YechimPurple,
    unfocusedTrailingIconColor = Color(0xFF475569),
    cursorColor = Color.Black,
    focusedPlaceholderColor = Color(0xFF64748B),
    unfocusedPlaceholderColor = Color(0xFF64748B)
)

// ─────────────────── SPLASH SCREEN ───────────────────
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(1f, animationSpec = tween(600))
        }
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF8FF),
                        Color(0xFFF3EEFF),
                        Color(0xFFE9E0FE)
                    )
                )
            )
    ) {
        // Decorative background glow
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(320.dp)
                .clip(CircleShape)
                .background(YechimPurpleLight.copy(alpha = 0.5f))
        )

        // Center Brand Identity (Exact match to Mockup Screen 1)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 60.dp)
                .scale(scale.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💡",
                fontSize = 72.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Title: YechimUZ
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Yechim",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = YechimPurple,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "UZ",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = YechimPurpleDark,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle exactly from mockup
            Text(
                text = "Muammo qo'ying.\nYechim toping.\nImkoniyat yarating.",
                textAlign = TextAlign.Center,
                color = TextPrimaryLight,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp
            )
        }

        // City skyline decorative illustration at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            YechimPurpleLight.copy(alpha = 0.6f),
                            YechimPurpleLight
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🕌 🏙️ 🗼 🏢 🏛️",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 32.dp),
                color = YechimPurpleMid.copy(alpha = 0.7f)
            )

            // Slider pill bar at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .width(60.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(YechimPurple)
            )
        }
    }
}

// ─────────────────── AUTH CONTAINER (LOGIN & REGISTER) ───────────────────
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    AuthScreenCore(
        initialTab = 0,
        onSuccess = onLoginSuccess
    )
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    AuthScreenCore(
        initialTab = 1,
        onSuccess = onRegisterSuccess
    )
}

@Composable
private fun AuthScreenCore(
    initialTab: Int,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keystoreManager = remember { KeystoreManager(context) }
    val apiService = remember { RetrofitClient.getApiService(context) }

    var selectedTab by remember { mutableIntStateOf(initialTab) } // 0: Kirish, 1: Ro'yxatdan o'tish

    // Login Form State
    var loginInput by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Violet Brand Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(
                    Brush.verticalGradient(listOf(YechimPurpleDark, YechimPurple))
                )
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💡", fontSize = 34.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Yechim", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("UZ", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = VibrantGold)
                }
                Text(
                    text = if (selectedTab == 0) "Platformaga xush kelibsiz!" else "Yangi hisob yaratish",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        // White Form Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 190.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ── SEGMENTED TAB SWITCHER (KIRISH / RO'YXATDAN O'TISH) ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFEDE9FE))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == 0) YechimPurple else Color.Transparent)
                                .clickable {
                                    selectedTab = 0
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Kirish",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color.White else Color(0xFF5B21B6),
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == 1) YechimPurple else Color.Transparent)
                                .clickable {
                                    selectedTab = 1
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ro'yxatdan o'tish",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) Color.White else Color(0xFF5B21B6),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Error Alert
                    errorMessage?.let { err ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.12f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err,
                                    color = ErrorRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Success Alert
                    successMessage?.let { msg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.12f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = SuccessGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (selectedTab == 0) {
                        // ──────────────────────────────────────────
                        // ── TAB 0: LOGIN FORM ──
                        // ──────────────────────────────────────────
                        Text(
                            text = "Hisobingizga kiring",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "Login va parolingizni kiriting",
                            color = TextSecondaryLight,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 18.dp)
                        )

                        // 1. Email yoki Login
                        OutlinedTextField(
                            value = loginInput,
                            onValueChange = { loginInput = it },
                            label = { Text("Email yoki Username") },
                            placeholder = { Text("masalan: ali_dev yoki ali@yechim.uz") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            trailingIcon = {
                                if (loginInput.isNotEmpty()) {
                                    IconButton(onClick = { loginInput = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Tozalash", modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = authInputColors()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 2. Parol
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Parol") },
                            placeholder = { Text("Parolingizni kiriting") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (isPasswordVisible) "Yashirish" else "Ko'rsatish"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = authInputColors()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Login Button
                        Button(
                            onClick = {
                                val cleanLogin = loginInput.trim()
                                val cleanPass = loginPassword.trim()
                                if (cleanLogin.isNotBlank() && cleanPass.isNotBlank()) {
                                    isLoading = true
                                    errorMessage = null
                                    coroutineScope.launch {
                                        try {
                                            val res = apiService.login(LoginRequest(emailOrUsername = cleanLogin, password = cleanPass))
                                            if (res.isSuccessful && res.body()?.success == true) {
                                                val body = res.body()
                                                val token = body?.token
                                                val user = body?.data
                                                if (token != null) {
                                                    keystoreManager.saveToken(token)
                                                    val name = "${user?.profile?.firstName ?: ""} ${user?.profile?.lastName ?: ""}".trim().ifEmpty { cleanLogin }
                                                    keystoreManager.saveUserSession(
                                                        userId = user?.id ?: "authenticated_user",
                                                        email = user?.email ?: cleanLogin,
                                                        fullName = name
                                                    )
                                                }
                                                onSuccess()
                                            } else {
                                                val errBody = try {
                                                    res.errorBody()?.string()?.let { jsonStr ->
                                                        com.google.gson.JsonParser.parseString(jsonStr).asJsonObject.get("error")?.asString
                                                    }
                                                } catch (_: Exception) { null }
                                                errorMessage = errBody ?: res.body()?.error ?: "Email yoki parol noto'g'ri."
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Tarmoq xatosi: Serverga ulanib bo'lmadi."
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    errorMessage = "Iltimos, login va parolni kiriting."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = YechimPurple),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Kirish →", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Switch to Register link
                        TextButton(
                            onClick = {
                                selectedTab = 1
                                errorMessage = null
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Hisobingiz yo'qmi? ", color = TextSecondaryLight, fontSize = 13.sp)
                            Text("Ro'yxatdan o'tish", color = YechimPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                    } else {
                        // ──────────────────────────────────────────
                        // ── TAB 1: REGISTER FORM ──
                        // ──────────────────────────────────────────
                        Text(
                            text = "Yangi hisob yaratish",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "Ma'lumotlaringizni to'ldiring",
                            color = TextSecondaryLight,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 1. Ism & Familiya
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = { Text("Ism", fontSize = 12.sp) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = authInputColors()
                            )
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = { Text("Familiya", fontSize = 12.sp) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = authInputColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Foydalanuvchi nomi (Username)
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                            label = { Text("Username", fontSize = 12.sp) },
                            placeholder = { Text("masalan: husniddin_dev", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = authInputColors()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3. Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", fontSize = 12.sp) },
                            placeholder = { Text("masalan: husniddin@yechim.uz", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = authInputColors()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4. Parol
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Parol (kamida 6 belgi)", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                    Icon(
                                        if (isRegPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = authInputColors()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Register Button
                        Button(
                            onClick = {
                                val cleanFirst = firstName.trim()
                                val cleanLast = lastName.trim()
                                val cleanUser = username.trim()
                                val cleanEmail = email.trim()
                                val cleanPass = regPassword.trim()

                                if (cleanFirst.isNotBlank() && cleanLast.isNotBlank() && cleanUser.isNotBlank() && cleanEmail.isNotBlank() && cleanPass.length >= 6) {
                                    isLoading = true
                                    errorMessage = null
                                    coroutineScope.launch {
                                        try {
                                            val req = RegisterRequest(
                                                firstName = cleanFirst,
                                                lastName = cleanLast,
                                                username = cleanUser,
                                                email = cleanEmail,
                                                password = cleanPass,
                                                roles = listOf("USER"),
                                                region = "Toshkent"
                                            )
                                            val res = apiService.register(req)
                                            if (res.isSuccessful && res.body()?.success == true) {
                                                val body = res.body()
                                                val token = body?.token
                                                val user = body?.data
                                                if (token != null) {
                                                    keystoreManager.saveToken(token)
                                                    keystoreManager.saveUserSession(
                                                        userId = user?.id ?: "authenticated_user",
                                                        email = cleanEmail,
                                                        fullName = "$cleanFirst $cleanLast".trim()
                                                    )
                                                }
                                                successMessage = "Ro'yxatdan muvaffaqiyatli o'tdingiz!"
                                                delay(500)
                                                onSuccess()
                                            } else {
                                                val errBody = try {
                                                    res.errorBody()?.string()?.let { jsonStr ->
                                                        com.google.gson.JsonParser.parseString(jsonStr).asJsonObject.get("error")?.asString
                                                    }
                                                } catch (_: Exception) { null }
                                                errorMessage = errBody ?: res.body()?.error ?: "Ro'yxatdan o'tishda xatolik yuz berdi."
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Tarmoq xatosi: Serverga ulanib bo'lmadi."
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    errorMessage = "Iltimos, barcha maydonlarni to'ldiring (parol kamida 6 belgi)."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = YechimPurple),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Ro'yxatdan o'tish →", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Switch to Login link
                        TextButton(
                            onClick = {
                                selectedTab = 0
                                errorMessage = null
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Hisobingiz bormi? ", color = TextSecondaryLight, fontSize = 13.sp)
                            Text("Tizimga kiring", color = YechimPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
