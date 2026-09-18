package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.data.model.UserRole
import com.example.ui.components.HeroCoverSection
import com.example.ui.components.NoteCard
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NotesViewModel,
    onNavigateToBrowse: () -> Unit,
    onNavigateToImportantNotes: () -> Unit,
    onNavigateToPreviousPapers: () -> Unit,
    onNavigateToImportantTopics: () -> Unit,
    onNavigateToStudyResources: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.websiteSettings.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    val recentNotes by viewModel.recentNotes.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val semesters by viewModel.allSemesters.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()

    val favoriteIds = favorites.map { it.id }.toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top App Bar / Identity Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = AcademicNavy,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Logo",
                                tint = AcademicGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = settings.websiteName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Academic Notes & Study Portal",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }

                // User / Admin status button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentUser != null) {
                        if (currentUser?.role == UserRole.ADMIN) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AcademicNavy,
                                modifier = Modifier
                                    .clickable { onNavigateToAdmin() }
                                    .testTag("admin_header_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AcademicGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Admin Panel",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier.testTag("profile_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = onOpenAuthDialog,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("login_button")
                        ) {
                            Text("Login / Register", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section 1: Hero Cover Section
        item {
            HeroCoverSection(
                settings = settings,
                stats = stats,
                onExploreClick = onNavigateToBrowse,
                onPreviousPapersClick = onNavigateToPreviousPapers,
                onImportantNotesClick = onNavigateToImportantNotes
            )
        }

        // Quick Navigation Section Cards
        item {
            Column {
                Text(
                    text = "Academic Hub Sections",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AcademicNavy
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubShortcutCard(
                        title = "Class Notes",
                        icon = Icons.Default.MenuBook,
                        badge = "${stats.totalNotes} Notes",
                        accentColor = AccentBlue,
                        onClick = onNavigateToBrowse,
                        modifier = Modifier.weight(1f)
                    )
                    HubShortcutCard(
                        title = "Exam Priority",
                        icon = Icons.Default.Grade,
                        badge = "High Yield",
                        accentColor = AcademicGold,
                        onClick = onNavigateToImportantNotes,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubShortcutCard(
                        title = "Previous Papers",
                        icon = Icons.Default.Description,
                        badge = "${stats.totalPreviousPapers} Papers",
                        accentColor = AcademicNavy,
                        onClick = onNavigateToPreviousPapers,
                        modifier = Modifier.weight(1f)
                    )
                    HubShortcutCard(
                        title = "Resources",
                        icon = Icons.Default.Folder,
                        badge = "Manuals & PPTs",
                        accentColor = Color(0xFF059669),
                        onClick = onNavigateToStudyResources,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Recently Uploaded Notes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Recently Uploaded Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy
                    )
                    Text(
                        text = "Fresh lecture transcripts, unit notes and revision guides",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }
                TextButton(onClick = onNavigateToBrowse) {
                    Text("View All", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Display recent notes
        items(recentNotes.take(4)) { note ->
            NoteCard(
                note = note,
                isBookmarked = favoriteIds.contains(note.id),
                onViewClick = { viewModel.openNotePreview(note) },
                onDownloadClick = { viewModel.downloadNote(note) },
                onToggleBookmark = { viewModel.toggleFavorite(note) }
            )
        }

        // Section: Browse by Subject
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Browse by Subject",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Core engineering curriculum and lecture topics",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjects) { subject ->
                        SubjectChipCard(
                            subject = subject,
                            onClick = {
                                viewModel.filterSubject.value = subject.name
                                viewModel.filterSemester.value = subject.semester
                                onNavigateToBrowse()
                            }
                        )
                    }
                }
            }
        }

        // Section: Browse by Semester
        item {
            Column {
                Text(
                    text = "Browse by Semester",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AcademicNavy
                )
                Text(
                    text = "Structured year-wise curriculum breakdown",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    semesters.take(4).forEach { semester ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.filterSemester.value = semester.name
                                    onNavigateToBrowse()
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = semester.yearName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate500,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = semester.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademicNavy
                                )
                            }
                        }
                    }
                }
            }
        }

        // Important Topics Shortcut Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToImportantTopics() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AcademicGold.copy(alpha = 0.2f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = AcademicGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Important Exam Topics Guide",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Master key university topics and high-probability questions",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate700
                        )
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = AcademicNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Customizable Footer Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Slate200)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = settings.websiteName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AcademicNavy
                )
                Text(
                    text = settings.tagline,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = settings.footerText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun HubShortcutCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
        }
    }
}

@Composable
fun SubjectChipCard(
    subject: Subject,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = AcademicNavy.copy(alpha = 0.1f)
            ) {
                Text(
                    text = subject.code,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AcademicNavy,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subject.semester,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
        }
    }
}
