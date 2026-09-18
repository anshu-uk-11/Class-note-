package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteItem
import com.example.ui.components.NoteCard
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.viewmodel.NotesViewModel
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesBrowseScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allNotes by viewModel.allNotes.collectAsState()
    val semesters by viewModel.allSemesters.collectAsState()
    val sections by viewModel.allClassSections.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSemester by viewModel.filterSemester.collectAsState()
    val selectedSection by viewModel.filterClassSection.collectAsState()
    val selectedSubject by viewModel.filterSubject.collectAsState()
    val selectedPriority by viewModel.filterPriority.collectAsState()
    val selectedNoteType by viewModel.filterNoteType.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    val favoriteIds = favorites.map { it.id }.toSet()

    var showHierarchyDrillDown by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter and sort the notes
    val filteredNotes = remember(
        allNotes, searchQuery, selectedSemester, selectedSection,
        selectedSubject, selectedPriority, selectedNoteType, sortOption
    ) {
        allNotes.filter { note ->
            val matchesQuery = searchQuery.isBlank() ||
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.subject.contains(searchQuery, ignoreCase = true) ||
                note.topic.contains(searchQuery, ignoreCase = true) ||
                note.unitName.contains(searchQuery, ignoreCase = true) ||
                note.tags.contains(searchQuery, ignoreCase = true)

            val matchesSemester = selectedSemester == null || note.semester == selectedSemester
            val matchesSection = selectedSection == null || note.classSection == selectedSection
            val matchesSubject = selectedSubject == null || note.subject == selectedSubject
            val matchesPriority = selectedPriority == null || note.priority == selectedPriority
            val matchesType = selectedNoteType == null || note.noteType == selectedNoteType

            matchesQuery && matchesSemester && matchesSection && matchesSubject && matchesPriority && matchesType
        }.let { list ->
            when (sortOption) {
                SortOption.NEWEST -> list.sortedByDescending { it.uploadDate }
                SortOption.OLDEST -> list.sortedBy { it.uploadDate }
                SortOption.MOST_DOWNLOADED -> list.sortedByDescending { it.downloadsCount }
                SortOption.ALPHABETICAL -> list.sortedBy { it.title.lowercase() }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("notes_browse_screen")
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Browse Class Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "${filteredNotes.size} notes available",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }

                    // Sort menu button
                    Box {
                        OutlinedButton(
                            onClick = { showSortMenu = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (sortOption) {
                                    SortOption.NEWEST -> "Newest"
                                    SortOption.OLDEST -> "Oldest"
                                    SortOption.MOST_DOWNLOADED -> "Downloads"
                                    SortOption.ALPHABETICAL -> "A-Z"
                                },
                                fontSize = 12.sp
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Newest Uploads") },
                                onClick = {
                                    viewModel.sortOption.value = SortOption.NEWEST
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Most Downloaded") },
                                onClick = {
                                    viewModel.sortOption.value = SortOption.MOST_DOWNLOADED
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Alphabetical (A-Z)") },
                                onClick = {
                                    viewModel.sortOption.value = SortOption.ALPHABETICAL
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Oldest Uploads") },
                                onClick = {
                                    viewModel.sortOption.value = SortOption.OLDEST
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Global Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search by title, subject, unit, topic, tags...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Slate500)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Drill-Down Toggle & Active Filter indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showHierarchyDrillDown = !showHierarchyDrillDown }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (showHierarchyDrillDown) "Hide Academic Hierarchy" else "Browse: Semester → Class → Subject",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (selectedSemester != null || selectedSection != null || selectedSubject != null || selectedPriority != null || selectedNoteType != null || searchQuery.isNotBlank()) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text("Clear All Filters", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    }
                }

                // Hierarchical Selectors (Semester -> Class -> Subject)
                AnimatedVisibility(visible = showHierarchyDrillDown) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "1. Select Semester:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AcademicNavy
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = selectedSemester == null,
                                    onClick = { viewModel.filterSemester.value = null },
                                    label = { Text("All Semesters", fontSize = 11.sp) }
                                )
                                semesters.forEach { sem ->
                                    FilterChip(
                                        selected = selectedSemester == sem.name,
                                        onClick = { viewModel.filterSemester.value = sem.name },
                                        label = { Text(sem.name, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "2. Select Class / Section:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AcademicNavy
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = selectedSection == null,
                                    onClick = { viewModel.filterClassSection.value = null },
                                    label = { Text("All Sections", fontSize = 11.sp) }
                                )
                                val relevantSections = if (selectedSemester != null) {
                                    sections.filter { it.semesterName == selectedSemester }
                                } else sections
                                relevantSections.forEach { sec ->
                                    FilterChip(
                                        selected = selectedSection == sec.name,
                                        onClick = { viewModel.filterClassSection.value = sec.name },
                                        label = { Text(sec.name, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "3. Select Subject:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AcademicNavy
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = selectedSubject == null,
                                    onClick = { viewModel.filterSubject.value = null },
                                    label = { Text("All Subjects", fontSize = 11.sp) }
                                )
                                val relevantSubjects = if (selectedSemester != null) {
                                    subjects.filter { it.semester == selectedSemester }
                                } else subjects
                                relevantSubjects.forEach { sub ->
                                    FilterChip(
                                        selected = selectedSubject == sub.name,
                                        onClick = { viewModel.filterSubject.value = sub.name },
                                        label = { Text(sub.name, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Priority Filter Pills
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val priorities = listOf("Exam Priority", "Very Important", "Important", "Normal")
                    priorities.forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = {
                                viewModel.filterPriority.value = if (selectedPriority == priority) null else priority
                            },
                            label = { Text(priority, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // List of filtered notes
        if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No notes matched your filters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your search query, semester, or priority filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { viewModel.clearFilters() }) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredNotes) { note ->
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
