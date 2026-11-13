package uz.yechim.app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.yechim.app.core.theme.*

@Composable
fun SideNavDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = SurfaceLight,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            // ── Curved Top Header with Logo ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                YechimPurpleLight,
                                YechimPurpleLight.copy(alpha = 0.5f),
                                SurfaceLight
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // Close button top right
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Yopish", tint = TextPrimaryLight)
                }

                // Brand block in center
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(YechimPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Y",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Row {
                            Text(
                                "Yechim",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = YechimPurple
                            )
                            Text(
                                "UZ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = VibrantGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Birgalikda yechim topamiz!",
                        fontSize = 12.sp,
                        color = TextSecondaryLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Drawer Menu Items ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DrawerMenuItem(
                    icon = Icons.Default.Home,
                    label = "Bosh sahifa",
                    isSelected = currentRoute == BottomNavItems.HOME,
                    onClick = {
                        onNavigate(BottomNavItems.HOME)
                        onClose()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.GridView,
                    label = "Kategoriyalar",
                    isSelected = currentRoute == BottomNavItems.MARKETPLACE,
                    onClick = {
                        onNavigate(BottomNavItems.MARKETPLACE)
                        onClose()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.WorkOutline,
                    label = "Mening faolligim",
                    isSelected = false,
                    onClick = {
                        onNavigate(BottomNavItems.SOLUTIONS)
                        onClose()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Notifications,
                    label = "Bildirishnomalar",
                    isSelected = false,
                    badge = "3",
                    onClick = {
                        onNavigate(BottomNavItems.SOLUTIONS)
                        onClose()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Settings,
                    label = "Sozlamalar",
                    isSelected = false,
                    onClick = {
                        onNavigate(BottomNavItems.PROFILE)
                        onClose()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.HelpOutline,
                    label = "Yordam",
                    isSelected = false,
                    onClick = {
                        onNavigate(BottomNavItems.SOLUTIONS)
                        onClose()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Info,
                    label = "Biz haqimizda",
                    isSelected = false,
                    onClick = {
                        onClose()
                    }
                )
            }

            // ── Bottom Logout Action ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = YechimPurple)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Chiqish",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) YechimPurpleLight else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) YechimPurple else TextPrimaryLight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) YechimPurple else TextPrimaryLight,
                modifier = Modifier.weight(1f)
            )
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ErrorRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
