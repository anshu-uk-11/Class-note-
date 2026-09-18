package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.NotePreviewDialog
import com.example.ui.components.PaperPreviewDialog
import com.example.ui.components.ResourcePreviewDialog
import com.example.ui.screens.AdminCustomizationScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminManageCurriculumScreen
import com.example.ui.screens.AdminManageNotesScreen
import com.example.ui.screens.AdminManageUsersScreen
import com.example.ui.screens.AdminUploadNoteScreen
import com.example.ui.screens.AuthDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImportantNotesScreen
import com.example.ui.screens.ImportantTopicsScreen
import com.example.ui.screens.NotesBrowseScreen
import com.example.ui.screens.PreviousPapersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StudyResourcesScreen
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate500
import com.example.ui.viewmodel.NotesViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object BrowseNotes : Screen("browse_notes", "Notes", Icons.Default.MenuBook)
    object ImportantNotes : Screen("important_notes", "Important", Icons.Default.Grade)
    object PreviousPapers : Screen("previous_papers", "Papers", Icons.Default.Description)
    object Resources : Screen("study_resources", "Resources", Icons.Default.Folder)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Secondary routes
    object ImportantTopics : Screen("important_topics", "Topics", Icons.Default.Grade)
    object AdminDashboard : Screen("admin_dashboard", "Admin", Icons.Default.Home)
    object AdminUploadNote : Screen("admin_upload_note", "Upload Note", Icons.Default.MenuBook)
    object AdminManageNotes : Screen("admin_manage_notes", "Manage Notes", Icons.Default.MenuBook)
    object AdminManageCurriculum : Screen("admin_manage_curriculum", "Curriculum", Icons.Default.MenuBook)
    object AdminManageUsers : Screen("admin_manage_users", "Users", Icons.Default.Person)
    object AdminCustomization : Screen("admin_customization", "Customization", Icons.Default.Home)
}

class MainActivity : ComponentActivity() {

    private val viewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ClassNotesHubApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ClassNotesHubApp(viewModel: NotesViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    var showAuthDialog by remember { mutableStateOf(false) }

    val previewNote by viewModel.previewNote.collectAsState()
    val previewPaper by viewModel.previewPaper.collectAsState()
    val previewResource by viewModel.previewResource.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()
    val favoriteIds = favorites.map { it.id }.toSet()

    // Handle snackbar notifications
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.BrowseNotes,
        Screen.ImportantNotes,
        Screen.PreviousPapers,
        Screen.Resources,
        Screen.Profile
    )

    // Hide bottom navigation on dedicated admin detail forms
    val showBottomBar = currentRoute in bottomNavItems.map { it.route } || currentRoute == Screen.ImportantTopics.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(screen.title, fontSize = 11.sp)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AcademicNavy,
                                selectedTextColor = AcademicNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.25f),
                                unselectedIconColor = Slate500,
                                unselectedTextColor = Slate500
                            )
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
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToBrowse = { navController.navigate(Screen.BrowseNotes.route) },
                        onNavigateToImportantNotes = { navController.navigate(Screen.ImportantNotes.route) },
                        onNavigateToPreviousPapers = { navController.navigate(Screen.PreviousPapers.route) },
                        onNavigateToImportantTopics = { navController.navigate(Screen.ImportantTopics.route) },
                        onNavigateToStudyResources = { navController.navigate(Screen.Resources.route) },
                        onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onOpenAuthDialog = { showAuthDialog = true }
                    )
                }

                composable(Screen.BrowseNotes.route) {
                    NotesBrowseScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.ImportantNotes.route) {
                    ImportantNotesScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.PreviousPapers.route) {
                    PreviousPapersScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.ImportantTopics.route) {
                    ImportantTopicsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Resources.route) {
                    StudyResourcesScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenAuthDialog = { showAuthDialog = true },
                        onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) }
                    )
                }

                // ----------------- ADMIN ROUTES -----------------
                composable(Screen.AdminDashboard.route) {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToUploadNote = { navController.navigate(Screen.AdminUploadNote.route) },
                        onNavigateToManageNotes = { navController.navigate(Screen.AdminManageNotes.route) },
                        onNavigateToManageCurriculum = { navController.navigate(Screen.AdminManageCurriculum.route) },
                        onNavigateToManageUsers = { navController.navigate(Screen.AdminManageUsers.route) },
                        onNavigateToCustomization = { navController.navigate(Screen.AdminCustomization.route) },
                        onOpenAuthDialog = { showAuthDialog = true }
                    )
                }

                composable(Screen.AdminUploadNote.route) {
                    AdminUploadNoteScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onUploadSuccess = {
                            navController.navigate(Screen.AdminManageNotes.route) {
                                popUpTo(Screen.AdminDashboard.route)
                            }
                        }
                    )
                }

                composable(Screen.AdminManageNotes.route) {
                    AdminManageNotesScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AdminManageCurriculum.route) {
                    AdminManageCurriculumScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AdminManageUsers.route) {
                    AdminManageUsersScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AdminCustomization.route) {
                    AdminCustomizationScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Global Preview & Auth Dialogs
            if (previewNote != null) {
                NotePreviewDialog(
                    note = previewNote!!,
                    isBookmarked = favoriteIds.contains(previewNote!!.id),
                    onDismiss = { viewModel.closeNotePreview() },
                    onDownload = { viewModel.downloadNote(previewNote!!) },
                    onToggleBookmark = { viewModel.toggleFavorite(previewNote!!) }
                )
            }

            if (previewPaper != null) {
                PaperPreviewDialog(
                    paper = previewPaper!!,
                    onDismiss = { viewModel.closePaperPreview() },
                    onDownload = { viewModel.downloadPaper(previewPaper!!) }
                )
            }

            if (previewResource != null) {
                ResourcePreviewDialog(
                    resource = previewResource!!,
                    onDismiss = { viewModel.closeResourcePreview() },
                    onDownloadOrOpen = { viewModel.downloadResource(previewResource!!) }
                )
            }

            if (showAuthDialog) {
                AuthDialog(
                    viewModel = viewModel,
                    onDismiss = { showAuthDialog = false }
                )
            }
        }
    }
}
