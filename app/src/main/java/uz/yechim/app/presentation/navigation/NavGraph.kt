package uz.yechim.app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import uz.yechim.app.core.theme.*
import uz.yechim.app.presentation.ai.AIProblemScreen
import uz.yechim.app.presentation.auth.LoginScreen
import uz.yechim.app.presentation.auth.RegisterScreen
import uz.yechim.app.presentation.auth.SplashScreen
import uz.yechim.app.presentation.chat.MessagesScreen
import uz.yechim.app.presentation.chat.SecureChatScreen
import uz.yechim.app.presentation.home.HomeScreen
import uz.yechim.app.presentation.problems.ProblemDetailScreen
import uz.yechim.app.presentation.problems.ProblemListScreen
import uz.yechim.app.presentation.profile.ProfileScreen
import uz.yechim.app.presentation.solutions.ComparisonMatrixScreen

object ScreenRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val PROBLEM_DETAIL = "problem_detail/{problemId}"
    const val CHAT = "chat/{solutionId}"
}

object BottomNavItems {
    const val HOME = "home"
    const val MARKETPLACE = "marketplace"
    const val AI_CREATE = "ai_create"
    const val SOLUTIONS = "solutions"
    const val PROFILE = "profile"
}

@Composable
fun MainAppScaffold(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    var currentRoute by remember { mutableStateOf(BottomNavItems.HOME) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideNavDrawerContent(
                currentRoute = currentRoute,
                onNavigate = { targetRoute ->
                    currentRoute = targetRoute
                    bottomNavController.navigate(targetRoute) {
                        popUpTo(BottomNavItems.HOME)
                    }
                },
                onClose = {
                    coroutineScope.launch { drawerState.close() }
                },
                onLogout = {
                    coroutineScope.launch { drawerState.close() }
                    rootNavController.navigate(ScreenRoutes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = BackgroundLight,
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceLight,
                    tonalElevation = 8.dp
                ) {
                    // 1. Asosiy (Home)
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItems.HOME,
                        onClick = {
                            currentRoute = BottomNavItems.HOME
                            bottomNavController.navigate(BottomNavItems.HOME) { popUpTo(BottomNavItems.HOME) { inclusive = true } }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(22.dp)) },
                        label = { Text("Asosiy", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = YechimPurple,
                            selectedTextColor = YechimPurple,
                            indicatorColor = YechimPurpleLight
                        )
                    )
                    // 2. Kategoriyalar (Marketplace)
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItems.MARKETPLACE,
                        onClick = {
                            currentRoute = BottomNavItems.MARKETPLACE
                            bottomNavController.navigate(BottomNavItems.MARKETPLACE) { popUpTo(BottomNavItems.HOME) }
                        },
                        icon = { Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(22.dp)) },
                        label = { Text("Kategoriya", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = YechimPurple,
                            selectedTextColor = YechimPurple,
                            indicatorColor = YechimPurpleLight
                        )
                    )
                    // 3. Center Add Action Button (+)
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItems.AI_CREATE,
                        onClick = {
                            currentRoute = BottomNavItems.AI_CREATE
                            bottomNavController.navigate(BottomNavItems.AI_CREATE) { popUpTo(BottomNavItems.HOME) }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(YechimPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Muammo qo'shish", tint = Color.White, modifier = Modifier.size(26.dp))
                            }
                        },
                        label = { },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                    )
                    // 4. Yechimlar / Xabarlar
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItems.SOLUTIONS,
                        onClick = {
                            currentRoute = BottomNavItems.SOLUTIONS
                            bottomNavController.navigate(BottomNavItems.SOLUTIONS) { popUpTo(BottomNavItems.HOME) }
                        },
                        icon = {
                            BadgedBox(badge = { Badge(containerColor = ErrorRed) { Text("2", fontSize = 9.sp) } }) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(22.dp))
                            }
                        },
                        label = { Text("Xabarlar", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = YechimPurple,
                            selectedTextColor = YechimPurple,
                            indicatorColor = YechimPurpleLight
                        )
                    )
                    // 5. Profil
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItems.PROFILE,
                        onClick = {
                            currentRoute = BottomNavItems.PROFILE
                            bottomNavController.navigate(BottomNavItems.PROFILE) { popUpTo(BottomNavItems.HOME) }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(22.dp)) },
                        label = { Text("Profil", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = YechimPurple,
                            selectedTextColor = YechimPurple,
                            indicatorColor = YechimPurpleLight
                        )
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItems.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItems.HOME) {
                    HomeScreen(
                        onNavigateToCreateProblem = {
                            currentRoute = BottomNavItems.AI_CREATE
                            bottomNavController.navigate(BottomNavItems.AI_CREATE)
                        },
                        onNavigateToMarketplace = { _ ->
                            currentRoute = BottomNavItems.MARKETPLACE
                            bottomNavController.navigate(BottomNavItems.MARKETPLACE)
                        },
                        onNavigateToProblemDetail = { problemId ->
                            rootNavController.navigate("problem_detail/$problemId")
                        },
                        onNavigateToSolutions = {
                            currentRoute = BottomNavItems.SOLUTIONS
                            bottomNavController.navigate(BottomNavItems.SOLUTIONS)
                        },
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }
                composable(BottomNavItems.MARKETPLACE) {
                    ProblemListScreen(
                        onNavigateToProblemDetail = { problemId ->
                            rootNavController.navigate("problem_detail/$problemId")
                        }
                    )
                }
                composable(BottomNavItems.AI_CREATE) {
                    AIProblemScreen(onChallengePublished = {
                        currentRoute = BottomNavItems.MARKETPLACE
                        bottomNavController.navigate(BottomNavItems.MARKETPLACE)
                    })
                }
                composable(BottomNavItems.SOLUTIONS) {
                    MessagesScreen(onNavigateToChat = { solutionId ->
                        rootNavController.navigate("chat/$solutionId")
                    })
                }
                composable(BottomNavItems.PROFILE) {
                    ProfileScreen(
                        onLogout = {
                            rootNavController.navigate(ScreenRoutes.LOGIN) { popUpTo(0) }
                        },
                        onNavigateToProblems = {
                            currentRoute = BottomNavItems.MARKETPLACE
                            bottomNavController.navigate(BottomNavItems.MARKETPLACE)
                        },
                        onNavigateToSolutions = {
                            currentRoute = BottomNavItems.SOLUTIONS
                            bottomNavController.navigate(BottomNavItems.SOLUTIONS)
                        },
                        onNavigateToChats = {
                            currentRoute = BottomNavItems.SOLUTIONS
                            bottomNavController.navigate(BottomNavItems.SOLUTIONS)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = ScreenRoutes.SPLASH) {
        composable(ScreenRoutes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(startDestination) { popUpTo(ScreenRoutes.SPLASH) { inclusive = true } }
            })
        }
        composable(ScreenRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ScreenRoutes.MAIN) { popUpTo(ScreenRoutes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegister = {
                    navController.navigate(ScreenRoutes.REGISTER)
                }
            )
        }
        composable(ScreenRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(ScreenRoutes.MAIN) { popUpTo(ScreenRoutes.LOGIN) { inclusive = true } }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(ScreenRoutes.MAIN) {
            MainAppScaffold(rootNavController = navController)
        }
        composable(ScreenRoutes.PROBLEM_DETAIL) { backStackEntry ->
            val problemId = backStackEntry.arguments?.getString("problemId") ?: "default"
            ProblemDetailScreen(
                problemId = problemId,
                onBack = { navController.popBackStack() },
                onViewAllSolutions = {
                    navController.popBackStack()
                },
                onSubmitSolution = { sId ->
                    navController.navigate("chat/$sId")
                }
            )
        }
        composable(ScreenRoutes.CHAT) { backStackEntry ->
            val solutionId = backStackEntry.arguments?.getString("solutionId") ?: "default"
            SecureChatScreen(solutionId = solutionId, onBack = { navController.popBackStack() })
        }
    }
}
