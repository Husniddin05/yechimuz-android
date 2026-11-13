package uz.yechim.app.presentation.problems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import uz.yechim.app.data.model.ProblemDto

private val REGIONS = listOf(
    "Barchasi", "Toshkent sh.", "Toshkent vil.", "Samarqand",
    "Farg'ona", "Andijon", "Namangan", "Qashqadaryo",
    "Surxondaryo", "Buxoro", "Navoiy", "Xorazm",
    "Jizzax", "Sirdaryo", "Qoraqalpog'iston"
)

data class CategoryItemUI(
    val title: String,
    val emoji: String,
    val iconColor: Color,
    val cardBg: Color,
    val slug: String
)

private val MOCKUP_CATEGORIES = listOf(
    CategoryItemUI("Ta'lim", "🎓", Color(0xFF2563EB), Color(0xFFEFF6FF), "talim"),
    CategoryItemUI("Texnologiya", "💻", Color(0xFF4F46E5), Color(0xFFEEF2FF), "texnologiya"),
    CategoryItemUI("Ekologiya", "🌿", Color(0xFF059669), Color(0xFFECFDF5), "ekologiya"),
    CategoryItemUI("Iqtisodiyot", "📈", Color(0xFFD97706), Color(0xFFFFFBEB), "iqtisodiyot"),
    CategoryItemUI("Sog'liqni\nsaqlash", "❤️", Color(0xFFDC2626), Color(0xFFFEF2F2), "soglik"),
    CategoryItemUI("Jamiyat", "👥", Color(0xFF7C3AED), Color(0xFFF5F3FF), "jamiyat"),
    CategoryItemUI("Transport", "🚌", Color(0xFF0284C7), Color(0xFFF0F9FF), "transport"),
    CategoryItemUI("Shahar\ninfratuzilmasi", "🏙️", Color(0xFF475569), Color(0xFFF8FAFC), "shahar"),
    CategoryItemUI("Boshqa", "•••", Color(0xFF64748B), Color(0xFFF1F5F9), "boshqa")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemListScreen(
    onNavigateToProblemDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getApiService(context) }

    var problems by remember { mutableStateOf<List<ProblemDto>>(emptyList()) }
    var categories by remember { mutableStateOf<List<CategoryDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Barchasi") }
    var selectedCategorySlug by remember { mutableStateOf<String?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }

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
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.GridView,
                            contentDescription = null,
                            tint = TextPrimaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Kategoriyalar",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Qidirish",
                            tint = TextPrimaryLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Search field if active ──
            if (isSearchActive) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Muammolar bo'yicha qidiring...", fontSize = 14.sp, color = TextSecondaryLight) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = YechimPurple) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                }
            }

            // ── Category Grid (Exact 2-column layout matching Mockup Screen 4) ──
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val chunked = MOCKUP_CATEGORIES.chunked(2)
                    chunked.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { cat ->
                                val isSelected = selectedCategorySlug == cat.slug
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(115.dp)
                                        .clickable {
                                            selectedCategorySlug = if (isSelected) null else cat.slug
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) YechimPurpleLight else cat.cardBg
                                    ),
                                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(YechimPurple)
                                    ) else null,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(cat.emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = cat.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) YechimPurple else cat.iconColor,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ── Region filter chips ──
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Hudud bo'yicha saralash",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimaryLight
                    )
                    if (selectedCategorySlug != null || selectedRegion != "Barchasi") {
                        TextButton(
                            onClick = {
                                selectedCategorySlug = null
                                selectedRegion = "Barchasi"
                                searchQuery = ""
                            }
                        ) {
                            Text("Filtrni tozalash", fontSize = 12.sp, color = YechimPurple)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(REGIONS) { region ->
                        val selected = selectedRegion == region
                        FilterChip(
                            selected = selected,
                            onClick = { selectedRegion = region },
                            label = { Text(region, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
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
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── Filtered Problems List ──
            val filtered = problems.filter { p ->
                val matchesRegion = selectedRegion == "Barchasi" || p.region?.contains(selectedRegion.take(5), ignoreCase = true) == true
                val matchesCategory = selectedCategorySlug == null || p.category?.slug == selectedCategorySlug
                val matchesSearch = searchQuery.isEmpty() || p.title.contains(searchQuery, ignoreCase = true)
                matchesRegion && matchesCategory && matchesSearch
            }

            if (filtered.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tanlangan parametrlar bo'yicha muammolar topilmadi.",
                            fontSize = 13.sp,
                            color = TextSecondaryLight,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filtered) { problem ->
                    MarketplaceProblemCard(
                        problem = problem,
                        onClick = { onNavigateToProblemDetail(problem.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun MarketplaceProblemCard(
    problem: ProblemDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(YechimPurpleLight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        problem.category?.nameUz ?: "Umumiy",
                        fontSize = 11.sp,
                        color = YechimPurple,
                        fontWeight = FontWeight.Bold
                    )
                }

                problem.region?.let { r ->
                    Text(
                        r,
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                problem.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimaryLight,
                lineHeight = 20.sp
            )

            if (!problem.rawDescription.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    problem.rawDescription,
                    fontSize = 12.sp,
                    color = TextSecondaryLight,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${(5000..25000).random()}", fontSize = 11.sp, color = TextSecondaryLight)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${(50..200).random()}", fontSize = 11.sp, color = TextSecondaryLight)
                }
            }
        }
    }
}
