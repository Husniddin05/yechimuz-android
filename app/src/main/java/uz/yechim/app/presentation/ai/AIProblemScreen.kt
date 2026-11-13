package uz.yechim.app.presentation.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uz.yechim.app.core.network.RetrofitClient
import uz.yechim.app.core.theme.*
import uz.yechim.app.data.model.CategoryDto
import uz.yechim.app.data.model.GenerateChallengeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIProblemScreen(onChallengePublished: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }

    var step by remember { mutableIntStateOf(1) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf<List<CategoryDto>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<CategoryDto?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val res = apiService.getCategories()
            if (res.isSuccessful) categories = res.body()?.data ?: emptyList()
        } catch (_: Exception) {}
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Muammo joylash", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimaryLight) },
                navigationIcon = {
                    if (step > 1) {
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimaryLight)
                        }
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ── Illustration ──
            Text("❓", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Muammongizni yozing", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextPrimaryLight)
            Text(
                "Muammingiz haqida batafsil ma'lumot bering",
                fontSize = 13.sp, color = TextSecondaryLight, textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            errorMessage?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(it, color = ErrorRed, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Sarlavha kiriting...", color = TextSecondaryLight) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
            Spacer(modifier = Modifier.height(14.dp))

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Muammo tavsifi...", color = TextSecondaryLight) },
                minLines = 4,
                maxLines = 6,
                textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
            Spacer(modifier = Modifier.height(16.dp))

            // Category dropdown
            Text("Kategoriya tanlang", fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                color = TextPrimaryLight, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedCategory?.nameUz ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Kategoriya tanlang", color = TextSecondaryLight) },
                    leadingIcon = { Icon(Icons.Default.GridView, contentDescription = null, tint = YechimPurple) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Medium),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = YechimPurple,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight
                    )
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nameUz) },
                            onClick = { selectedCategory = cat; expanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Submit button
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            try {
                                val res = apiService.generateChallenge(
                                    GenerateChallengeRequest(
                                        title = title,
                                        rawDescription = description,
                                        categoryId = selectedCategory?.id,
                                        region = null
                                    )
                                )
                                if (res.isSuccessful && res.body()?.success == true) {
                                    onChallengePublished()
                                } else {
                                    errorMessage = res.body()?.error ?: "Muammo joylashda xatolik."
                                }
                            } catch (e: Exception) {
                                errorMessage = "Tarmoq xatosi."
                            } finally { isLoading = false }
                        }
                    } else {
                        errorMessage = "Sarlavha va tavsif majburiy."
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YechimPurple),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Keyingi qadam →", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rasm qo'shish shart emas, faqat matn yetarli!", fontSize = 12.sp, color = Color(0xFF1D4ED8))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
