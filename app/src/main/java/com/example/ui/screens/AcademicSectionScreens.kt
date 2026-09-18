package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ImportantTopicCard
import com.example.ui.components.NoteCard
import com.example.ui.components.PaperCard
import com.example.ui.components.StudyResourceCard
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.ErrorRuby
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.viewmodel.NotesViewModel

// ----------------- IMPORTANT NOTES SCREEN -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportantNotesScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val importantNotes by viewModel.importantNotes.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()
    val favoriteIds = favorites.map { it.id }.toSet()

    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }

    val filtered = remember(importantNotes, selectedSubject, selectedPriority) {
        importantNotes.filter { note ->
            (selectedSubject == null || note.subject == selectedSubject) &&
            (selectedPriority == null || note.priority == selectedPriority)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("important_notes_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Important & Exam Priority Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Curated high-weightage topics marked by faculty",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Priority Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All Priorities", "Exam Priority", "Very Important", "Important").forEach { p ->
                        val isSelected = (p == "All Priorities" && selectedPriority == null) || (selectedPriority == p)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPriority = if (p == "All Priorities") null else p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Subject Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedSubject == null,
                        onClick = { selectedSubject = null },
                        label = { Text("All Subjects", fontSize = 11.sp) }
                    )
                    subjects.forEach { s ->
                        FilterChip(
                            selected = selectedSubject == s.name,
                            onClick = { selectedSubject = s.name },
                            label = { Text(s.name, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notes found for this filter combination", color = Slate500)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filtered) { note ->
                    NoteCard(
                        note = note,
                        isBookmarked = favoriteIds.contains(note.id),
                        onViewClick = { viewModel.openNotePreview(note) },
                        onDownloadClick = { viewModel.downloadNote(note) },
                        onToggleBookmark = { viewModel.toggleFavorite(note) }
                    )
                }
            }
        }
    }
}

// ----------------- PREVIOUS PAPERS SCREEN -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousPapersScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val papers by viewModel.allPreviousPapers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedExamType by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<String?>(null) }

    val filtered = remember(papers, searchQuery, selectedExamType, selectedYear) {
        papers.filter { paper ->
            val matchesQuery = searchQuery.isBlank() ||
                paper.title.contains(searchQuery, ignoreCase = true) ||
                paper.subject.contains(searchQuery, ignoreCase = true)

            val matchesType = selectedExamType == null || paper.examType == selectedExamType
            val matchesYear = selectedYear == null || paper.examYear == selectedYear

            matchesQuery && matchesType && matchesYear
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("previous_papers_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Previous Examination Papers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Official University End Sem, Mid Term & Internal question sets",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search question papers by subject, year...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate500) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AcademicNavy,
                        unfocusedBorderColor = Slate200
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All Types", "End Semester", "Mid Term", "Internal", "Practical").forEach { t ->
                        val isSelected = (t == "All Types" && selectedExamType == null) || (selectedExamType == t)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedExamType = if (t == "All Types") null else t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No previous question papers match your query.", color = Slate500)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { paper ->
                    PaperCard(
                        paper = paper,
                        onViewClick = { viewModel.openPaperPreview(paper) },
                        onDownloadClick = { viewModel.downloadPaper(paper) }
                    )
                }
            }
        }
    }
}

// ----------------- IMPORTANT TOPICS SCREEN -----------------
@Composable
fun ImportantTopicsScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topics by viewModel.allImportantTopics.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    var selectedSubject by remember { mutableStateOf<String?>(null) }

    val filtered = remember(topics, selectedSubject) {
        if (selectedSubject == null) topics else topics.filter { it.subject == selectedSubject }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("important_topics_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Important Topics & Formulas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Core exam blueprints, recurring numericals and theory concepts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedSubject == null,
                        onClick = { selectedSubject = null },
                        label = { Text("All Subjects", fontSize = 11.sp) }
                    )
                    subjects.forEach { s ->
                        FilterChip(
                            selected = selectedSubject == s.name,
                            onClick = { selectedSubject = s.name },
                            label = { Text(s.name, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { topic ->
                ImportantTopicCard(topic = topic)
            }
        }
    }
}

// ----------------- STUDY RESOURCES SCREEN -----------------
@Composable
fun StudyResourcesScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources by viewModel.allStudyResources.collectAsState()
    var selectedType by remember { mutableStateOf<String?>(null) }

    val filtered = remember(resources, selectedType) {
        if (selectedType == null) resources else resources.filter { it.resourceType == selectedType }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("study_resources_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Study Resources & Lab Manuals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Lab manuals, question banks, slide decks and external learning portals",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All Resources", "Lab Manual", "Question Bank", "PPT", "Useful Website", "Video Lecture").forEach { type ->
                        val isSelected = (type == "All Resources" && selectedType == null) || (selectedType == type)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = if (type == "All Resources") null else type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { resource ->
                StudyResourceCard(
                    resource = resource,
                    onDownloadOrOpen = {
                        viewModel.downloadResource(resource)
                    }
                )
            }
        }
    }
}
