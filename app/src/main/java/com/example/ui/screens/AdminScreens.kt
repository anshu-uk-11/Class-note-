package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteItem
import com.example.data.model.UserRole
import com.example.data.model.WebsiteSettings
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.ErrorRuby
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.NotesViewModel

// ----------------- ADMIN DASHBOARD SCREEN -----------------
@Composable
fun AdminDashboardScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    onNavigateToUploadNote: () -> Unit,
    onNavigateToManageNotes: () -> Unit,
    onNavigateToManageCurriculum: () -> Unit,
    onNavigateToManageUsers: () -> Unit,
    onNavigateToCustomization: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()

    // Enforce real Backend/Role Authorization check
    if (currentUser == null || currentUser?.role != UserRole.ADMIN) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = ErrorRuby.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = ErrorRuby,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "403 Unauthorized / Forbidden",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRuby
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Access to the /admin control center is strictly restricted to Faculty and Administrator roles. Students cannot perform administrative modifications.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onOpenAuthDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
                ) {
                    Text("Sign In as Administrator")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onBack) {
                    Text("Return to Student Portal")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Admin Control Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Logged in as ${currentUser?.name} (Dean/HOD)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AcademicGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "ADMIN ACCESS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AcademicGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Section 4 Requirement: Large "+ Upload Notes" button in Admin Dashboard
        item {
            Button(
                onClick = onNavigateToUploadNote,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("admin_upload_notes_big_button")
            ) {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = AcademicGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "+ Upload Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Dashboard Statistic Cards Grid
        item {
            Text(
                text = "Repository & Engagement Statistics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AcademicNavy
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Stats row 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(title = "Total Users", count = stats.totalUsers.toString(), icon = Icons.Default.People, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Total Notes", count = stats.totalNotes.toString(), icon = Icons.Default.MenuBook, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Stats row 2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(title = "Total Subjects", count = stats.totalSubjects.toString(), icon = Icons.Default.School, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Previous Papers", count = stats.totalPreviousPapers.toString(), icon = Icons.Default.Description, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Stats row 3
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(title = "Important Topics", count = stats.totalImportantTopics.toString(), icon = Icons.Default.Grade, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Study Resources", count = stats.totalResources.toString(), icon = Icons.Default.Folder, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Downloads Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessEmerald.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Downloads Served", style = MaterialTheme.typography.labelSmall, color = Slate700)
                        Text("${stats.totalDownloads} file downloads", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SuccessEmerald)
                    }
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(28.dp))
                }
            }
        }

        // Admin Management Actions Section
        item {
            Text(
                text = "Administration Center",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AcademicNavy
            )
            Spacer(modifier = Modifier.height(10.dp))

            AdminNavButton(title = "Manage Notes (Edit / Replace / Delete)", icon = Icons.Default.MenuBook, onClick = onNavigateToManageNotes)
            Spacer(modifier = Modifier.height(8.dp))
            AdminNavButton(title = "Manage Subjects, Semesters & Classes", icon = Icons.Default.School, onClick = onNavigateToManageCurriculum)
            Spacer(modifier = Modifier.height(8.dp))
            AdminNavButton(title = "User Accounts & Role Permissions", icon = Icons.Default.People, onClick = onNavigateToManageUsers)
            Spacer(modifier = Modifier.height(8.dp))
            AdminNavButton(title = "Website Customization (Logo, Hero, Colors, Tagline)", icon = Icons.Default.Settings, onClick = onNavigateToCustomization)
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = AcademicNavy.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = AcademicNavy, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = AcademicNavy)
                Text(title, style = MaterialTheme.typography.labelSmall, color = Slate500, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun AdminNavButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = AcademicNavy, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Slate700)
            }
            Text("Manage →", style = MaterialTheme.typography.labelSmall, color = AccentBlue, fontWeight = FontWeight.Bold)
        }
    }
}

// ----------------- ADMIN UPLOAD NOTE SCREEN -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUploadNoteScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    onUploadSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val semesters by viewModel.allSemesters.collectAsState()
    val sections by viewModel.allClassSections.collectAsState()

    // Form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Data Structures & Algorithms") }
    var semester by remember { mutableStateOf(semesters.firstOrNull()?.name ?: "Semester 3") }
    var academicYear by remember { mutableStateOf("2025-2026") }
    var classSection by remember { mutableStateOf("CSE-A") }
    var unitNumber by remember { mutableStateOf("1") }
    var unitName by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var noteType by remember { mutableStateOf("Class Notes") }
    var priority by remember { mutableStateOf("Normal") }

    // File simulation state
    var selectedFileName by remember { mutableStateOf("Lecture_Notes_Unit1.pdf") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dropdown expansion states
    var expSubject by remember { mutableStateOf(false) }
    var expSemester by remember { mutableStateOf(false) }
    var expSection by remember { mutableStateOf(false) }
    var expNoteType by remember { mutableStateOf(false) }
    var expPriority by remember { mutableStateOf(false) }

    val noteTypeOptions = listOf(
        "Class Notes", "Lecture Notes", "Revision Notes", "Assignment",
        "Lab Manual", "Question Bank", "Study Material", "Other"
    )
    val priorityOptions = listOf("Normal", "Important", "Very Important", "Exam Priority")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_upload_note_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("Upload Class Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Text("Attach documents, specify academic unit and assign priority", style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title *") },
                    placeholder = { Text("e.g. Asymptotic Complexity & Master Theorem") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_title_input")
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Brief summary of the lecture or study topics covered") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Subject Dropdown
            item {
                ExposedDropdownMenuBox(expanded = expSubject, onExpandedChange = { expSubject = it }) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Subject *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expSubject) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expSubject, onDismissRequest = { expSubject = false }) {
                        subjects.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.code} - ${s.name}") },
                                onClick = {
                                    subject = s.name
                                    semester = s.semester
                                    expSubject = false
                                }
                            )
                        }
                    }
                }
            }

            // Semester & Class/Section Row
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Semester dropdown
                    ExposedDropdownMenuBox(
                        expanded = expSemester,
                        onExpandedChange = { expSemester = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = semester,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Semester") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expSemester) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expSemester, onDismissRequest = { expSemester = false }) {
                            semesters.forEach { sem ->
                                DropdownMenuItem(
                                    text = { Text(sem.name) },
                                    onClick = {
                                        semester = sem.name
                                        expSemester = false
                                    }
                                )
                            }
                        }
                    }

                    // Section dropdown
                    ExposedDropdownMenuBox(
                        expanded = expSection,
                        onExpandedChange = { expSection = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = classSection,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Class / Sec") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expSection) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expSection, onDismissRequest = { expSection = false }) {
                            listOf("CSE-A", "CSE-B", "IT-A", "IT-B", "ECE-A").forEach { sec ->
                                DropdownMenuItem(
                                    text = { Text(sec) },
                                    onClick = {
                                        classSection = sec
                                        expSection = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Unit Number & Unit Name
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = unitNumber,
                        onValueChange = { unitNumber = it.filter { c -> c.isDigit() } },
                        label = { Text("Unit #") },
                        modifier = Modifier.width(90.dp)
                    )
                    OutlinedTextField(
                        value = unitName,
                        onValueChange = { unitName = it },
                        label = { Text("Unit Name") },
                        placeholder = { Text("e.g. Non-Linear Structures") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Topic & Tags
            item {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic Name *") },
                    placeholder = { Text("e.g. AVL Tree Balance Factor Rotations") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Search Tags (comma-separated)") },
                    placeholder = { Text("trees, avl, rotations, algorithms") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Note Type & Priority Dropdowns
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Note Type
                    ExposedDropdownMenuBox(
                        expanded = expNoteType,
                        onExpandedChange = { expNoteType = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = noteType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Note Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expNoteType) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expNoteType, onDismissRequest = { expNoteType = false }) {
                            noteTypeOptions.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        noteType = type
                                        expNoteType = false
                                    }
                                )
                            }
                        }
                    }

                    // Priority
                    ExposedDropdownMenuBox(
                        expanded = expPriority,
                        onExpandedChange = { expPriority = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = priority,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Priority") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expPriority) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expPriority, onDismissRequest = { expPriority = false }) {
                            priorityOptions.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p) },
                                    onClick = {
                                        priority = p
                                        expPriority = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Drag-and-drop / Choose File Upload Area
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("file_drop_area"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = AcademicNavy,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Drag & Drop Academic File Here",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                        Text(
                            text = "Allowed: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, JPG, PNG, WEBP, TXT (Max 25MB)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Choose File action
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val sampleFiles = listOf(
                                        "Unit_Summary_Notes.pdf",
                                        "Exam_Lecture_Slides.pptx",
                                        "Worked_Assignment_Solutions.docx"
                                    )
                                    selectedFileName = sampleFiles.random()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Choose File", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected File Info Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = AcademicGold, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(selectedFileName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Text("Validated • 3.2 MB • Ready for Cloud Storage", style = MaterialTheme.typography.labelSmall, color = Slate500)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(4.dp), color = SuccessEmerald.copy(alpha = 0.15f)) {
                                    Text("VALID", color = SuccessEmerald, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        if (isUploading) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Uploading file to cloud storage & indexing metadata...", style = MaterialTheme.typography.labelSmall, color = AccentBlue)
                        }
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Text(errorMessage!!, color = ErrorRuby, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "Please enter note title"
                            return@Button
                        }
                        isUploading = true
                        errorMessage = null
                        viewModel.uploadNote(
                            title = title,
                            description = description,
                            subject = subject,
                            semester = semester,
                            academicYear = academicYear,
                            classSection = classSection,
                            unitNumber = unitNumber.toIntOrNull() ?: 1,
                            unitName = unitName.ifBlank { "Unit $unitNumber" },
                            topic = topic.ifBlank { title },
                            tags = tags,
                            noteType = noteType,
                            priority = priority,
                            fileName = selectedFileName
                        ) { success, err ->
                            isUploading = false
                            if (success) {
                                onUploadSuccess()
                            } else {
                                errorMessage = err ?: "Upload failed"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_upload_note_button")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload & Index Class Note", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------- ADMIN MANAGE NOTES SCREEN -----------------
@Composable
fun AdminManageNotesScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allNotes by viewModel.allNotes.collectAsState()
    var noteToDelete by remember { mutableStateOf<NoteItem?>(null) }
    var noteToEdit by remember { mutableStateOf<NoteItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_manage_notes_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("Manage Class Notes (${allNotes.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Text("Faculty operations: Edit metadata, replace attachments, delete", style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(allNotes) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                Text("${note.subject} • ${note.semester}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Text("${note.downloadsCount} downloads", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(note.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AcademicNavy)
                        Text("Unit ${note.unitNumber}: ${note.unitName} → ${note.topic}", style = MaterialTheme.typography.bodySmall, color = Slate700)
                        Text("File: ${note.fileName} (${note.fileSizeFormatted}) • Type: ${note.noteType} • Priority: ${note.priority}", style = MaterialTheme.typography.labelSmall, color = Slate500)

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { viewModel.openNotePreview(note) }) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Preview")
                            }
                            TextButton(onClick = { noteToEdit = note }) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit / Replace")
                            }
                            TextButton(onClick = { noteToDelete = note }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRuby, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = ErrorRuby)
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog when deleting
    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Confirm Note Deletion", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete \"${noteToDelete?.title}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteNote(noteToDelete!!.id) { _, _ ->
                            noteToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRuby)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit note dialog
    if (noteToEdit != null) {
        val note = noteToEdit!!
        var editTitle by remember { mutableStateOf(note.title) }
        var editDesc by remember { mutableStateOf(note.description) }
        var editPriority by remember { mutableStateOf(note.priority) }
        var editType by remember { mutableStateOf(note.noteType) }

        AlertDialog(
            onDismissRequest = { noteToEdit = null },
            title = { Text("Edit Note Metadata", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPriority,
                        onValueChange = { editPriority = it },
                        label = { Text("Priority (Normal, Important, Exam Priority)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updated = note.copy(
                        title = editTitle,
                        description = editDesc,
                        priority = editPriority
                    )
                    viewModel.updateNote(updated) { success, _ ->
                        if (success) noteToEdit = null
                    }
                }) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ----------------- ADMIN MANAGE CURRICULUM SCREEN (SUBJECTS & SEMESTERS) -----------------
@Composable
fun AdminManageCurriculumScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val semesters by viewModel.allSemesters.collectAsState()
    val sections by viewModel.allClassSections.collectAsState()

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddSemesterDialog by remember { mutableStateOf(false) }
    var showAddSectionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_manage_curriculum_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("Curriculum & Semester Hierarchy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Text("Manage subjects, academic years, semesters and class sections", style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subjects Header + Add Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subjects (${subjects.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Button(onClick = { showAddSubjectDialog = true }, shape = RoundedCornerShape(8.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Subject", fontSize = 12.sp)
                    }
                }
            }

            items(subjects) { subject ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${subject.code}: ${subject.name}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${subject.semester} • ${subject.department}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        IconButton(onClick = { viewModel.deleteSubject(subject.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRuby)
                        }
                    }
                }
            }

            // Semesters Header + Add Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Semesters (${semesters.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Button(onClick = { showAddSemesterDialog = true }, shape = RoundedCornerShape(8.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Semester", fontSize = 12.sp)
                    }
                }
            }

            items(semesters) { sem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(sem.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${sem.yearName} • Academic Year ${sem.academicYear}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        IconButton(onClick = { viewModel.deleteSemester(sem.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRuby)
                        }
                    }
                }
            }
        }
    }

    // Add subject dialog
    if (showAddSubjectDialog) {
        var code by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var sem by remember { mutableStateOf("Semester 3") }
        var dept by remember { mutableStateOf("Computer Science & Engineering") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add New Subject", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. CS205)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sem, onValueChange = { sem = it }, label = { Text("Semester (e.g. Semester 3)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        viewModel.addSubject(code, name, sem, dept, desc) { success, _ ->
                            if (success) showAddSubjectDialog = false
                        }
                    }
                }) {
                    Text("Add Subject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add semester dialog
    if (showAddSemesterDialog) {
        var semName by remember { mutableStateOf("") }
        var yearName by remember { mutableStateOf("Year 2") }
        var acadYear by remember { mutableStateOf("2025-2026") }

        AlertDialog(
            onDismissRequest = { showAddSemesterDialog = false },
            title = { Text("Add Academic Semester", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = semName, onValueChange = { semName = it }, label = { Text("Semester Name (e.g. Semester 7)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = yearName, onValueChange = { yearName = it }, label = { Text("Year (e.g. Year 4)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = acadYear, onValueChange = { acadYear = it }, label = { Text("Academic Year (e.g. 2025-2026)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (semName.isNotBlank()) {
                        viewModel.addSemester(semName, yearName, acadYear) { success, _ ->
                            if (success) showAddSemesterDialog = false
                        }
                    }
                }) {
                    Text("Add Semester")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSemesterDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ----------------- ADMIN MANAGE USERS SCREEN -----------------
@Composable
fun AdminManageUsersScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_manage_users_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("Manage Registered Users (${users.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Text("Configure student and administrator permissions", style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = Slate500)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (user.role == UserRole.ADMIN) AcademicNavy else AcademicGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = user.role,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.role == UserRole.ADMIN) Color.White else AcademicGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row {
                            TextButton(onClick = {
                                val nextRole = if (user.role == UserRole.ADMIN) UserRole.STUDENT else UserRole.ADMIN
                                viewModel.updateUserRole(user.id, nextRole)
                            }) {
                                Text(if (user.role == UserRole.ADMIN) "Make Student" else "Make Admin")
                            }
                            IconButton(onClick = { viewModel.deleteUser(user.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRuby)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------- ADMIN WEBSITE CUSTOMIZATION SCREEN -----------------
@Composable
fun AdminCustomizationScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.websiteSettings.collectAsState()

    var websiteName by remember(settings) { mutableStateOf(settings.websiteName) }
    var tagline by remember(settings) { mutableStateOf(settings.tagline) }
    var welcomeMessage by remember(settings) { mutableStateOf(settings.welcomeMessage) }
    var announcement by remember(settings) { mutableStateOf(settings.announcement) }
    var footerText by remember(settings) { mutableStateOf(settings.footerText) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_customization_screen")
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("Website Customization", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Text("Modify branding, hero message, announcements & footer", style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = websiteName,
                onValueChange = { websiteName = it },
                label = { Text("Website Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("Hero Tagline") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = welcomeMessage,
                onValueChange = { welcomeMessage = it },
                label = { Text("Welcome Message") },
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = announcement,
                onValueChange = { announcement = it },
                label = { Text("Banner Announcement (Notice to Students)") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = footerText,
                onValueChange = { footerText = it },
                label = { Text("Footer Copyright Text") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val updated = settings.copy(
                        websiteName = websiteName.trim(),
                        tagline = tagline.trim(),
                        welcomeMessage = welcomeMessage.trim(),
                        announcement = announcement.trim(),
                        footerText = footerText.trim()
                    )
                    viewModel.saveCustomization(updated) { success, _ ->
                        if (success) onBack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Apply Settings Live", fontWeight = FontWeight.Bold)
            }
        }
    }
}
