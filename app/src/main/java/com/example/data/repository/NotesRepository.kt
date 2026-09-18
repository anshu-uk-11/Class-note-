package com.example.data.repository

import com.example.data.local.NotesDao
import com.example.data.model.AcademicSemester
import com.example.data.model.Bookmark
import com.example.data.model.ClassSection
import com.example.data.model.DashboardStats
import com.example.data.model.DownloadRecord
import com.example.data.model.ImportantTopic
import com.example.data.model.NoteItem
import com.example.data.model.PreviousPaper
import com.example.data.model.StudyResource
import com.example.data.model.Subject
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.WebsiteSettings
import com.example.data.security.FileStorageService
import com.example.data.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class NotesRepository(
    private val dao: NotesDao,
    private val fileStorage: FileStorageService
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val allNotes: Flow<List<NoteItem>> = dao.getAllNotes()
    val recentNotes: Flow<List<NoteItem>> = dao.getRecentNotes(10)
    val importantNotes: Flow<List<NoteItem>> = dao.getImportantNotes()

    val allSubjects: Flow<List<Subject>> = dao.getAllSubjects()
    val allSemesters: Flow<List<AcademicSemester>> = dao.getAllSemesters()
    val allClassSections: Flow<List<ClassSection>> = dao.getAllClassSections()

    val allPreviousPapers: Flow<List<PreviousPaper>> = dao.getAllPreviousPapers()
    val allImportantTopics: Flow<List<ImportantTopic>> = dao.getAllImportantTopics()
    val allStudyResources: Flow<List<StudyResource>> = dao.getAllStudyResources()
    val websiteSettings: Flow<WebsiteSettings?> = dao.getSettings()
    val allUsers: Flow<List<User>> = dao.getAllUsers()

    val dashboardStats: Flow<DashboardStats> = combine(
        listOf(
            dao.getUserCount(),
            dao.getNotesCount(),
            dao.getSubjectCount(),
            dao.getPreviousPaperCount(),
            dao.getImportantTopicCount(),
            dao.getStudyResourceCount(),
            dao.getTotalDownloadsCount()
        )
    ) { values ->
        DashboardStats(
            totalUsers = values[0] as Int,
            totalNotes = values[1] as Int,
            totalSubjects = values[2] as Int,
            totalPreviousPapers = values[3] as Int,
            totalImportantTopics = values[4] as Int,
            totalResources = values[5] as Int,
            totalDownloads = values[6] as Int
        )
    }.flowOn(Dispatchers.IO)

    // ----------------- AUTHENTICATION -----------------
    suspend fun login(email: String, rawPass: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val user = dao.getUserByEmail(trimmedEmail)
            ?: return@withContext Result.failure(IllegalArgumentException("Invalid email or password"))

        if (!PasswordHasher.verifyPassword(rawPass, user.passwordHash)) {
            return@withContext Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        _currentUser.value = user
        Result.success(user)
    }

    suspend fun register(
        name: String,
        email: String,
        rawPass: String,
        department: String,
        semester: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        if (name.isBlank() || trimmedEmail.isBlank() || rawPass.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please fill in all required fields"))
        }
        if (rawPass.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        val existing = dao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return@withContext Result.failure(IllegalArgumentException("Account with this email already exists"))
        }

        val newUser = User(
            name = name.trim(),
            email = trimmedEmail,
            passwordHash = PasswordHasher.hashPassword(rawPass),
            role = UserRole.STUDENT,
            department = department.trim(),
            semester = semester.trim()
        )
        val id = dao.insertUser(newUser)
        val saved = newUser.copy(id = id)
        _currentUser.value = saved
        Result.success(saved)
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun updateProfile(name: String, department: String, semester: String): Result<User> = withContext(Dispatchers.IO) {
        val current = _currentUser.value
            ?: return@withContext Result.failure(IllegalStateException("No active user session"))
        val updated = current.copy(
            name = name.trim(),
            department = department.trim(),
            semester = semester.trim()
        )
        dao.updateUser(updated)
        _currentUser.value = updated
        Result.success(updated)
    }

    // ----------------- AUTHORIZATION GUARD -----------------
    private fun verifyAdmin(): Result<Unit> {
        val user = _currentUser.value
        return if (user != null && user.role == UserRole.ADMIN) {
            Result.success(Unit)
        } else {
            Result.failure(SecurityException("403 Forbidden: Admin privileges required for this operation."))
        }
    }

    // ----------------- NOTE MANAGEMENT (ADMIN ONLY) -----------------
    suspend fun uploadNote(
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
        fileContent: ByteArray? = null
    ): Result<Long> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }

        if (title.isBlank() || subject.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Title and Subject are required"))
        }

        val simulatedSize = (1024 * 1024 * (1.5 + Math.random() * 4.0)).toLong()
        val validation = fileStorage.validateFile(fileName.ifBlank { "$title.pdf" }, simulatedSize)
        if (!validation.isValid) {
            return@withContext Result.failure(IllegalArgumentException(validation.errorMessage))
        }

        val path = if (fileContent != null) {
            fileStorage.saveContentToFile(fileName, fileContent)
        } else {
            "materials/${fileName.ifBlank { "note_${System.currentTimeMillis()}.pdf" }}"
        }

        val note = NoteItem(
            title = title.trim(),
            description = description.trim(),
            subject = subject.trim(),
            semester = semester.trim(),
            academicYear = academicYear.trim(),
            classSection = classSection.trim(),
            unitNumber = unitNumber,
            unitName = unitName.trim(),
            topic = topic.trim(),
            tags = tags.trim(),
            noteType = noteType,
            priority = priority,
            fileName = fileName.ifBlank { "$title.pdf" },
            fileType = validation.fileExtension,
            fileSizeFormatted = validation.formattedSize,
            filePath = path,
            contentPreview = "# $title\n\n- Subject: $subject ($semester)\n- Unit $unitNumber: $unitName\n- Topic: $topic\n\n$description",
            uploadedBy = _currentUser.value?.name ?: "Faculty Admin"
        )
        val id = dao.insertNote(note)
        Result.success(id)
    }

    suspend fun updateNote(note: NoteItem): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.updateNote(note)
        Result.success(Unit)
    }

    suspend fun deleteNote(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.deleteNoteById(id)
        Result.success(Unit)
    }

    // ----------------- CURRICULUM MANAGEMENT (ADMIN ONLY) -----------------
    suspend fun addSubject(code: String, name: String, semester: String, department: String, description: String): Result<Long> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        val subject = Subject(
            code = code.trim().uppercase(),
            name = name.trim(),
            semester = semester.trim(),
            department = department.trim(),
            description = description.trim()
        )
        val id = dao.insertSubject(subject)
        Result.success(id)
    }

    suspend fun deleteSubject(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.deleteSubjectById(id)
        Result.success(Unit)
    }

    suspend fun addSemester(name: String, year: String, academicYear: String): Result<Long> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        val id = dao.insertSemester(AcademicSemester(name = name.trim(), yearName = year.trim(), academicYear = academicYear.trim()))
        Result.success(id)
    }

    suspend fun deleteSemester(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.deleteSemesterById(id)
        Result.success(Unit)
    }

    suspend fun addClassSection(name: String, semesterName: String, department: String): Result<Long> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        val id = dao.insertClassSection(ClassSection(name = name.trim(), semesterName = semesterName.trim(), department = department.trim()))
        Result.success(id)
    }

    suspend fun deleteClassSection(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.deleteClassSectionById(id)
        Result.success(Unit)
    }

    // ----------------- PREVIOUS PAPERS (ADMIN & VIEW) -----------------
    suspend fun addPreviousPaper(
        title: String,
        subject: String,
        semester: String,
        classSection: String,
        examType: String,
        examYear: String,
        academicYear: String,
        description: String,
        fileName: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        val paper = PreviousPaper(
            title = title.trim(),
            subject = subject.trim(),
            semester = semester.trim(),
            classSection = classSection.trim(),
            examType = examType.trim(),
            examYear = examYear.trim(),
            academicYear = academicYear.trim(),
            description = description.trim(),
            fileName = fileName.ifBlank { "${subject}_${examType}_$examYear.pdf" },
            fileSizeFormatted = "2.2 MB"
        )
        val id = dao.insertPreviousPaper(paper)
        Result.success(id)
    }

    suspend fun deletePreviousPaper(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.deletePreviousPaperById(id)
        Result.success(Unit)
    }

    // ----------------- IMPORTANT TOPICS (ADMIN & VIEW) -----------------
    suspend fun addImportantTopic(
        topicName: String,
        subject: String,
        semester: String,
        classSection: String,
        unitNumber: Int,
        unitName: String,
        description: String,
        priority: String,
        relatedNoteTitle: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        val topic = ImportantTopic(
            topicName = topicName.trim(),
            subject = subject.trim(),
            semester = semester.trim(),
            classSection = classSection.trim(),
            unitNumber = unitNumber,
            unitName = unitName.trim(),
            description = description.trim(),
            priority = priority.trim(),
            relatedNoteTitle = relatedNoteTitle.trim()
        )
        val id = dao.insertImportantTopic(topic)
        Result.success(id)
    }

    suspend fun deleteImportantTopic(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.deleteImportantTopicById(id)
        Result.success(Unit)
    }

    // ----------------- STUDY RESOURCES (ADMIN & VIEW) -----------------
    suspend fun addStudyResource(
        title: String,
        resourceType: String,
        subject: String,
        semester: String,
        description: String,
        urlOrPath: String,
        fileName: String,
        isExternalLink: Boolean
    ): Result<Long> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        val res = StudyResource(
            title = title.trim(),
            resourceType = resourceType.trim(),
            subject = subject.trim(),
            semester = semester.trim(),
            description = description.trim(),
            urlOrPath = urlOrPath.trim(),
            fileName = fileName.trim(),
            fileSizeFormatted = if (isExternalLink) "Web Link" else "3.5 MB",
            isExternalLink = isExternalLink
        )
        val id = dao.insertStudyResource(res)
        Result.success(id)
    }

    suspend fun deleteStudyResource(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.deleteStudyResourceById(id)
        Result.success(Unit)
    }

    // ----------------- USER ROLES & DELETION (ADMIN ONLY) -----------------
    suspend fun updateUserRole(targetUserId: Long, newRole: String): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        val user = dao.getUserById(targetUserId)
            ?: return@withContext Result.failure(IllegalArgumentException("User not found"))
        dao.updateUser(user.copy(role = newRole))
        Result.success(Unit)
    }

    suspend fun deleteUser(targetUserId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        if (_currentUser.value?.id == targetUserId) {
            return@withContext Result.failure(IllegalArgumentException("Cannot delete your own active admin account"))
        }
        dao.deleteUserById(targetUserId)
        Result.success(Unit)
    }

    // ----------------- WEBSITE CUSTOMIZATION (ADMIN ONLY) -----------------
    suspend fun saveCustomization(settings: WebsiteSettings): Result<Unit> = withContext(Dispatchers.IO) {
        verifyAdmin().onFailure { return@withContext Result.failure(it) }
        dao.saveSettings(settings.copy(lastUpdated = System.currentTimeMillis()))
        Result.success(Unit)
    }

    // ----------------- DOWNLOADS & BOOKMARKS -----------------
    suspend fun recordNoteDownload(note: NoteItem): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = _currentUser.value?.id ?: 0L
        dao.incrementNoteDownloads(note.id)
        dao.insertDownloadRecord(
            DownloadRecord(
                userId = userId,
                contentId = note.id,
                contentType = "NOTE",
                contentTitle = note.title
            )
        )
        Result.success(Unit)
    }

    suspend fun recordPaperDownload(paper: PreviousPaper): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = _currentUser.value?.id ?: 0L
        dao.incrementPaperDownloads(paper.id)
        dao.insertDownloadRecord(
            DownloadRecord(
                userId = userId,
                contentId = paper.id,
                contentType = "PAPER",
                contentTitle = paper.title
            )
        )
        Result.success(Unit)
    }

    suspend fun recordResourceDownload(resource: StudyResource): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = _currentUser.value?.id ?: 0L
        dao.incrementResourceDownloads(resource.id)
        dao.insertDownloadRecord(
            DownloadRecord(
                userId = userId,
                contentId = resource.id,
                contentType = "RESOURCE",
                contentTitle = resource.title
            )
        )
        Result.success(Unit)
    }

    suspend fun toggleBookmark(noteId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
            ?: return@withContext Result.failure(IllegalStateException("Please log in to add notes to your favorites"))
        val count = dao.isBookmarked(user.id, noteId)
        if (count > 0) {
            dao.deleteBookmark(user.id, noteId)
            Result.success(false)
        } else {
            dao.insertBookmark(Bookmark(userId = user.id, noteId = noteId))
            Result.success(true)
        }
    }

    fun getBookmarkedNotesForCurrentUser(): Flow<List<NoteItem>> {
        val uid = _currentUser.value?.id ?: 0L
        return dao.getBookmarkedNotesForUser(uid)
    }
}
