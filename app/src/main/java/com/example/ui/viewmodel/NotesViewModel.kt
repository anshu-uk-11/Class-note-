package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.model.AcademicSemester
import com.example.data.model.ClassSection
import com.example.data.model.DashboardStats
import com.example.data.model.ImportantTopic
import com.example.data.model.NoteItem
import com.example.data.model.PreviousPaper
import com.example.data.model.StudyResource
import com.example.data.model.Subject
import com.example.data.model.User
import com.example.data.model.WebsiteSettings
import com.example.data.repository.NotesRepository
import com.example.data.security.FileStorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption {
    NEWEST,
    OLDEST,
    MOST_DOWNLOADED,
    ALPHABETICAL
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val fileStorage = FileStorageService(application)
    val repository = NotesRepository(db.notesDao(), fileStorage)

    // Current logged in user
    val currentUser: StateFlow<User?> = repository.currentUser

    // Global site settings
    val websiteSettings: StateFlow<WebsiteSettings> = kotlinx.coroutines.flow.flow {
        repository.websiteSettings.collect { emit(it ?: WebsiteSettings()) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WebsiteSettings()
    )

    // Admin Dashboard stats
    val dashboardStats: StateFlow<DashboardStats> = repository.dashboardStats
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DashboardStats()
        )

    // Core lists
    val allNotes: StateFlow<List<NoteItem>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentNotes: StateFlow<List<NoteItem>> = repository.recentNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val importantNotes: StateFlow<List<NoteItem>> = repository.importantNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSemesters: StateFlow<List<AcademicSemester>> = repository.allSemesters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allClassSections: StateFlow<List<ClassSection>> = repository.allClassSections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPreviousPapers: StateFlow<List<PreviousPaper>> = repository.allPreviousPapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allImportantTopics: StateFlow<List<ImportantTopic>> = repository.allImportantTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudyResources: StateFlow<List<StudyResource>> = repository.allStudyResources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFavorites: StateFlow<List<NoteItem>> = repository.currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getBookmarkedNotesForCurrentUser()
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter & Search state for Notes Browsing
    val searchQuery = MutableStateFlow("")
    val filterSemester = MutableStateFlow<String?>(null)
    val filterClassSection = MutableStateFlow<String?>(null)
    val filterSubject = MutableStateFlow<String?>(null)
    val filterUnit = MutableStateFlow<Int?>(null)
    val filterTopic = MutableStateFlow<String?>(null)
    val filterPriority = MutableStateFlow<String?>(null)
    val filterNoteType = MutableStateFlow<String?>(null)
    val sortOption = MutableStateFlow(SortOption.NEWEST)

    // UI Feedback & Active Previews
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _previewNote = MutableStateFlow<NoteItem?>(null)
    val previewNote: StateFlow<NoteItem?> = _previewNote.asStateFlow()

    private val _previewPaper = MutableStateFlow<PreviousPaper?>(null)
    val previewPaper: StateFlow<PreviousPaper?> = _previewPaper.asStateFlow()

    private val _previewResource = MutableStateFlow<StudyResource?>(null)
    val previewResource: StateFlow<StudyResource?> = _previewResource.asStateFlow()

    init {
        // Pre-populate realistic academic curriculum data
        viewModelScope.launch {
            DatabaseInitializer.populateInitialData(db.notesDao())
        }
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun openNotePreview(note: NoteItem) {
        _previewNote.value = note
    }

    fun closeNotePreview() {
        _previewNote.value = null
    }

    fun openPaperPreview(paper: PreviousPaper) {
        _previewPaper.value = paper
    }

    fun closePaperPreview() {
        _previewPaper.value = null
    }

    fun openResourcePreview(resource: StudyResource) {
        _previewResource.value = resource
    }

    fun closeResourcePreview() {
        _previewResource.value = null
    }

    // Authentication actions
    fun login(email: String, rawPass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.login(email, rawPass).fold(
                onSuccess = {
                    showSnackbar("Welcome back, ${it.name} (${it.role})")
                    onResult(true, null)
                },
                onFailure = {
                    onResult(false, it.message ?: "Authentication failed")
                }
            )
        }
    }

    fun register(
        name: String,
        email: String,
        rawPass: String,
        department: String,
        semester: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            repository.register(name, email, rawPass, department, semester).fold(
                onSuccess = {
                    showSnackbar("Account registered! Welcome to Class Notes Hub.")
                    onResult(true, null)
                },
                onFailure = {
                    onResult(false, it.message ?: "Registration failed")
                }
            )
        }
    }

    fun logout() {
        repository.logout()
        showSnackbar("You have been signed out.")
    }

    fun updateProfile(name: String, department: String, semester: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.updateProfile(name, department, semester).fold(
                onSuccess = {
                    showSnackbar("Profile updated successfully")
                    onResult(true, null)
                },
                onFailure = {
                    onResult(false, it.message)
                }
            )
        }
    }

    // Student actions
    fun downloadNote(note: NoteItem) {
        viewModelScope.launch {
            repository.recordNoteDownload(note)
            showSnackbar("Downloaded: ${note.fileName} (${note.fileSizeFormatted})")
        }
    }

    fun downloadPaper(paper: PreviousPaper) {
        viewModelScope.launch {
            repository.recordPaperDownload(paper)
            showSnackbar("Downloaded: ${paper.fileName}")
        }
    }

    fun downloadResource(resource: StudyResource) {
        viewModelScope.launch {
            repository.recordResourceDownload(resource)
            showSnackbar("Downloaded study material: ${resource.title}")
        }
    }

    fun toggleFavorite(note: NoteItem) {
        viewModelScope.launch {
            repository.toggleBookmark(note.id).fold(
                onSuccess = { added ->
                    val msg = if (added) "Saved to My Favorites" else "Removed from Favorites"
                    showSnackbar(msg)
                },
                onFailure = {
                    showSnackbar(it.message ?: "Please log in first")
                }
            )
        }
    }

    // Admin Note Operations
    fun uploadNote(
        title: String,
        description: String,
        subject: String,
        semester: String,
        academicYear: String,
        classSection: String,
        unitNumber: Int,
        unitName: String,
        topic: String,
        tags: String,
        noteType: String,
        priority: String,
        fileName: String,
        fileContent: ByteArray? = null,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            repository.uploadNote(
                title, description, subject, semester, academicYear, classSection,
                unitNumber, unitName, topic, tags, noteType, priority, fileName, fileContent
            ).fold(
                onSuccess = {
                    showSnackbar("Note uploaded successfully and placed in $subject ($semester)")
                    onComplete(true, null)
                },
                onFailure = {
                    onComplete(false, it.message)
                }
            )
        }
    }

    fun updateNote(note: NoteItem, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.updateNote(note).fold(
                onSuccess = {
                    showSnackbar("Note updated successfully")
                    onComplete(true, null)
                },
                onFailure = {
                    onComplete(false, it.message)
                }
            )
        }
    }

    fun deleteNote(id: Long, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.deleteNote(id).fold(
                onSuccess = {
                    showSnackbar("Note deleted")
                    onComplete(true, null)
                },
                onFailure = {
                    onComplete(false, it.message)
                }
            )
        }
    }

    // Admin Curriculum Operations
    fun addSubject(code: String, name: String, semester: String, department: String, description: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.addSubject(code, name, semester, department, description).fold(
                onSuccess = {
                    showSnackbar("Subject $name added")
                    onComplete(true, null)
                },
                onFailure = { onComplete(false, it.message) }
            )
        }
    }

    fun deleteSubject(id: Long) {
        viewModelScope.launch {
            repository.deleteSubject(id).fold(
                onSuccess = { showSnackbar("Subject removed") },
                onFailure = { showSnackbar(it.message ?: "Failed to delete subject") }
            )
        }
    }

    fun addSemester(name: String, year: String, academicYear: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.addSemester(name, year, academicYear).fold(
                onSuccess = {
                    showSnackbar("Semester $name created")
                    onComplete(true, null)
                },
                onFailure = { onComplete(false, it.message) }
            )
        }
    }

    fun deleteSemester(id: Long) {
        viewModelScope.launch {
            repository.deleteSemester(id).fold(
                onSuccess = { showSnackbar("Semester removed") },
                onFailure = { showSnackbar(it.message ?: "Error") }
            )
        }
    }

    fun addClassSection(name: String, semesterName: String, department: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.addClassSection(name, semesterName, department).fold(
                onSuccess = {
                    showSnackbar("Class / Section $name created")
                    onComplete(true, null)
                },
                onFailure = { onComplete(false, it.message) }
            )
        }
    }

    fun deleteClassSection(id: Long) {
        viewModelScope.launch {
            repository.deleteClassSection(id).fold(
                onSuccess = { showSnackbar("Class section removed") },
                onFailure = { showSnackbar(it.message ?: "Error") }
            )
        }
    }

    // Admin Previous Papers
    fun addPreviousPaper(
        title: String,
        subject: String,
        semester: String,
        classSection: String,
        examType: String,
        examYear: String,
        academicYear: String,
        description: String,
        fileName: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            repository.addPreviousPaper(
                title, subject, semester, classSection, examType, examYear,
                academicYear, description, fileName
            ).fold(
                onSuccess = {
                    showSnackbar("Previous paper uploaded")
                    onComplete(true, null)
                },
                onFailure = { onComplete(false, it.message) }
            )
        }
    }

    fun deletePreviousPaper(id: Long) {
        viewModelScope.launch {
            repository.deletePreviousPaper(id).fold(
                onSuccess = { showSnackbar("Previous paper deleted") },
                onFailure = { showSnackbar(it.message ?: "Error") }
            )
        }
    }

    // Admin Important Topics
    fun addImportantTopic(
        topicName: String,
        subject: String,
        semester: String,
        classSection: String,
        unitNumber: Int,
        unitName: String,
        description: String,
        priority: String,
        relatedNoteTitle: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            repository.addImportantTopic(
                topicName, subject, semester, classSection, unitNumber,
                unitName, description, priority, relatedNoteTitle
            ).fold(
                onSuccess = {
                    showSnackbar("Important topic added")
                    onComplete(true, null)
                },
                onFailure = { onComplete(false, it.message) }
            )
        }
    }

    fun deleteImportantTopic(id: Long) {
        viewModelScope.launch {
            repository.deleteImportantTopic(id).fold(
                onSuccess = { showSnackbar("Important topic deleted") },
                onFailure = { showSnackbar(it.message ?: "Error") }
            )
        }
    }

    // Admin Study Resources
    fun addStudyResource(
        title: String,
        resourceType: String,
        subject: String,
        semester: String,
        description: String,
        urlOrPath: String,
        fileName: String,
        isExternalLink: Boolean,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            repository.addStudyResource(
                title, resourceType, subject, semester, description,
                urlOrPath, fileName, isExternalLink
            ).fold(
                onSuccess = {
                    showSnackbar("Study resource added")
                    onComplete(true, null)
                },
                onFailure = { onComplete(false, it.message) }
            )
        }
    }

    fun deleteStudyResource(id: Long) {
        viewModelScope.launch {
            repository.deleteStudyResource(id).fold(
                onSuccess = { showSnackbar("Study resource deleted") },
                onFailure = { showSnackbar(it.message ?: "Error") }
            )
        }
    }

    // Admin Users
    fun updateUserRole(userId: Long, newRole: String) {
        viewModelScope.launch {
            repository.updateUserRole(userId, newRole).fold(
                onSuccess = { showSnackbar("User role changed to $newRole") },
                onFailure = { showSnackbar(it.message ?: "Role update failed") }
            )
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            repository.deleteUser(userId).fold(
                onSuccess = { showSnackbar("User account deleted") },
                onFailure = { showSnackbar(it.message ?: "Failed to delete user") }
            )
        }
    }

    // Admin Customization
    fun saveCustomization(settings: WebsiteSettings, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.saveCustomization(settings).fold(
                onSuccess = {
                    showSnackbar("Website customization settings saved")
                    onComplete(true, null)
                },
                onFailure = { onComplete(false, it.message) }
            )
        }
    }

    fun clearFilters() {
        filterSemester.value = null
        filterClassSection.value = null
        filterSubject.value = null
        filterUnit.value = null
        filterTopic.value = null
        filterPriority.value = null
        filterNoteType.value = null
        searchQuery.value = ""
        sortOption.value = SortOption.NEWEST
    }
}
