package uz.yechim.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.yechim.app.core.security.KeystoreManager
import uz.yechim.app.core.theme.*

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToProblems: () -> Unit = {},
    onNavigateToSolutions: () -> Unit = {},
    onNavigateToChats: () -> Unit = {}
) {
    val context = LocalContext.current
    val keystoreManager = remember { KeystoreManager(context) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showVipDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var showProfileDetailDialog by remember { mutableStateOf(false) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("O'zbekcha") }

    val storedName = keystoreManager.getUserName()
    val userName = if (!storedName.isNullOrBlank() && storedName != "authenticated_user") storedName else "Husniddinbek"
    val storedEmail = keystoreManager.getUserEmail()
    val username = if (!storedEmail.isNullOrBlank()) "@${storedEmail.substringBefore("@")}" else "@husniddinuz"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar with Settings gear ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mening Profilim",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimaryLight
            )

            IconButton(
                onClick = { showSettingsDialog = true }
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Sozlamalar",
                    tint = TextPrimaryLight,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ── User Avatar & Identity (Exact match to Mockup Screen 7) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile photo circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(YechimPurpleLight)
                    .border(2.dp, YechimPurple, CircleShape)
                    .clickable { showProfileDetailDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = YechimPurple
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name with blue verified badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showProfileDetailDialog = true }
            ) {
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.Default.Verified,
                    contentDescription = "Tasdiqlangan",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Username handle
            Text(
                text = username,
                fontSize = 13.sp,
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Premium pill badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(YechimPurpleLight)
                    .clickable { showVipDialog = true }
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "👑 Premium A'zo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = YechimPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Stats Row (24 Yechimlar | 12K Obunachilar | 156 Faollik) ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showStatsDialog = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem("24", "Yechimlar")
                Box(modifier = Modifier.width(1.dp).height(36.dp).background(CardBorder))
                StatItem("12K", "Obunachilar")
                Box(modifier = Modifier.width(1.dp).height(36.dp).background(CardBorder))
                StatItem("156", "Faollik")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Purple Banner: YechimUZ jamoasiga qo'shilganingiz uchun rahmat! ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showVipDialog = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = YechimPurple)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👑", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "YechimUZ jamoasiga\nqo'shilganingiz uchun rahmat!",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Menu List (Exact items from Mockup Screen 7) ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                // 1. Mening muammolarim
                ProfileMenuItem(
                    icon = Icons.Default.Assignment,
                    label = "Mening muammolarim",
                    onClick = onNavigateToProblems
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = CardBorder.copy(alpha = 0.5f))

                // 2. Mening yechimlarim
                ProfileMenuItem(
                    icon = Icons.Default.Lightbulb,
                    label = "Mening yechimlarim",
                    onClick = onNavigateToSolutions
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = CardBorder.copy(alpha = 0.5f))

                // 3. Xabarlar
                ProfileMenuItem(
                    icon = Icons.Default.Chat,
                    label = "Xabarlar",
                    onClick = onNavigateToChats
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = CardBorder.copy(alpha = 0.5f))

                // 4. Sozlamalar
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    label = "Sozlamalar",
                    onClick = { showSettingsDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = CardBorder.copy(alpha = 0.5f))

                // 5. Chiqish
                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    label = "Chiqish",
                    onClick = {
                        keystoreManager.clearSession()
                        onLogout()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Account Deletion (Google Play Compliance) ──
        TextButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Hisobni o'chirish (Delete Account)", color = ErrorRed, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // ── 1. SETTINGS DIALOG ──
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = YechimPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ilova Sozlamalari", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Bildirishnomalar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Yangi xabar va yechimlar haqida", fontSize = 11.sp, color = TextSecondaryLight)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = YechimPurple, checkedTrackColor = YechimPurpleLight)
                        )
                    }

                    HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))

                    // Language
                    Column {
                        Text("Tilni tanlang", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("O'zbekcha", "Русский", "English").forEach { lang ->
                                FilterChip(
                                    selected = selectedLanguage == lang,
                                    onClick = { selectedLanguage = lang },
                                    label = { Text(lang, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = YechimPurple,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))

                    // Security info
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Qurilma xavfsizligi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Text("Android Keystore (AES-256-GCM) faol va himoyalangan.", fontSize = 11.sp, color = TextSecondaryLight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Versiya: YechimUZ v1.2.2 Pro (2026)", fontSize = 11.sp, color = TextMutedLight)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                ) {
                    Text("Saqlash", color = Color.White)
                }
            }
        )
    }

    // ── 2. VIP MEMBERSHIP DIALOG ──
    if (showVipDialog) {
        AlertDialog(
            onDismissRequest = { showVipDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("YechimUZ Premium A'zolik", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Siz YechimUZ ekotizimining faol a'zosisiz! Siz uchun barcha eksklyuziv imkoniyatlar yoqilgan:",
                        fontSize = 13.sp,
                        color = TextPrimaryLight
                    )
                    Text("✓ Cheksiz muammolarni e'lon qilish", fontSize = 12.sp, color = TextSecondaryLight)
                    Text("✓ AI Yordamchi orqali professional chaqiriq yaratish", fontSize = 12.sp, color = TextSecondaryLight)
                    Text("✓ Mutaxassislar bilan to'g'ridan-to'g'ri 1:1 shifrlangan chat", fontSize = 12.sp, color = TextSecondaryLight)
                    Text("✓ 24/7 ustuvor texnik qo'llab-quvvatlash", fontSize = 12.sp, color = TextSecondaryLight)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showVipDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                ) {
                    Text("Tushunarli", color = Color.White)
                }
            }
        )
    }

    // ── 3. STATS DIALOG ──
    if (showStatsDialog) {
        AlertDialog(
            onDismissRequest = { showStatsDialog = false },
            title = { Text("Faollik Statistikasi", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• 24 ta muammo va yechimlar ko'rib chiqilgan", fontSize = 13.sp)
                    Text("• 12,000+ jamiyat a'zolari obuna bo'lgan", fontSize = 13.sp)
                    Text("• 156 ta faol taklif va monitoring harakatlari", fontSize = 13.sp)
                    Text("• Reyting darajasi: Yuqori (Top 5%)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStatsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                ) {
                    Text("Yopish", color = Color.White)
                }
            }
        )
    }

    // ── 4. PROFILE DETAIL DIALOG ──
    if (showProfileDetailDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDetailDialog = false },
            title = { Text("Foydalanuvchi Ma'lumotlari", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Ism-familiya: $userName", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Username: $username", fontSize = 13.sp, color = TextSecondaryLight)
                    Text("Email: ${storedEmail ?: "Kiritilmagan"}", fontSize = 13.sp, color = TextSecondaryLight)
                    Text("Hudud: O'zbekiston, Toshkent", fontSize = 13.sp, color = TextSecondaryLight)
                    Text("Holati: Tasdiqlangan hisob", fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileDetailDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    // ── 5. DELETE ACCOUNT DIALOG ──
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hisobni o'chirish", fontWeight = FontWeight.Bold) },
            text = { Text("Hisobingiz va unga tegishli barcha ma'lumotlar butunlay o'chiriladi. Ushbu amalni ortga qaytarib bo'lmaydi.") },
            confirmButton = {
                Button(
                    onClick = {
                        keystoreManager.clearSession()
                        showDeleteDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("O'chirish", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Bekor qilish")
                }
            }
        )
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = TextPrimaryLight
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondaryLight
        )
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(YechimPurpleLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = YechimPurple, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimaryLight,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondaryLight,
            modifier = Modifier.size(18.dp)
        )
    }
}
