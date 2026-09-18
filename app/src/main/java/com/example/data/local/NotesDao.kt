package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AcademicSemester
import com.example.data.model.Bookmark
import com.example.data.model.ClassSection
import com.example.data.model.DownloadRecord
import com.example.data.model.ImportantTopic
import com.example.data.model.NoteItem
import com.example.data.model.PreviousPaper
import com.example.data.model.StudyResource
import com.example.data.model.Subject
import com.example.data.model.User
import com.example.data.model.WebsiteSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    // ----------------- USERS -----------------
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    // ----------------- NOTES -----------------
    @Query("SELECT * FROM notes ORDER BY uploadDate DESC")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes ORDER BY uploadDate DESC LIMIT :limit")
    fun getRecentNotes(limit: Int = 10): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes WHERE priority IN ('Important', 'Very Important', 'Exam Priority') ORDER BY uploadDate DESC")
    fun getImportantNotes(): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): NoteItem?

    @Query("SELECT * FROM notes WHERE subject = :subject ORDER BY unitNumber ASC, uploadDate DESC")
    fun getNotesBySubject(subject: String): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes WHERE semester = :semester ORDER BY subject ASC, uploadDate DESC")
    fun getNotesBySemester(semester: String): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes WHERE id IN (SELECT noteId FROM bookmarks WHERE userId = :userId) ORDER BY uploadDate DESC")
    fun getBookmarkedNotesForUser(userId: Long): Flow<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteItem): Long

    @Update
    suspend fun updateNote(note: NoteItem)

    @Delete
    suspend fun deleteNote(note: NoteItem)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE notes SET downloadsCount = downloadsCount + 1 WHERE id = :id")
    suspend fun incrementNoteDownloads(id: Long)

    @Query("SELECT COUNT(*) FROM notes")
    fun getNotesCount(): Flow<Int>

    // ----------------- SUBJECTS -----------------
    @Query("SELECT * FROM subjects ORDER BY code ASC, name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE semester = :semester ORDER BY name ASC")
    fun getSubjectsBySemester(semester: String): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Long)

    @Query("SELECT COUNT(*) FROM subjects")
    fun getSubjectCount(): Flow<Int>

    // ----------------- SEMESTERS & CLASSES -----------------
    @Query("SELECT * FROM semesters ORDER BY name ASC")
    fun getAllSemesters(): Flow<List<AcademicSemester>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: AcademicSemester): Long

    @Query("DELETE FROM semesters WHERE id = :id")
    suspend fun deleteSemesterById(id: Long)

    @Query("SELECT * FROM class_sections ORDER BY name ASC")
    fun getAllClassSections(): Flow<List<ClassSection>>

    @Query("SELECT * FROM class_sections WHERE semesterName = :semesterName ORDER BY name ASC")
    fun getClassSectionsBySemester(semesterName: String): Flow<List<ClassSection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSection(section: ClassSection): Long

    @Query("DELETE FROM class_sections WHERE id = :id")
    suspend fun deleteClassSectionById(id: Long)

    // ----------------- PREVIOUS PAPERS -----------------
    @Query("SELECT * FROM previous_papers ORDER BY examYear DESC, uploadDate DESC")
    fun getAllPreviousPapers(): Flow<List<PreviousPaper>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreviousPaper(paper: PreviousPaper): Long

    @Query("DELETE FROM previous_papers WHERE id = :id")
    suspend fun deletePreviousPaperById(id: Long)

    @Query("UPDATE previous_papers SET downloadsCount = downloadsCount + 1 WHERE id = :id")
    suspend fun incrementPaperDownloads(id: Long)

    @Query("SELECT COUNT(*) FROM previous_papers")
    fun getPreviousPaperCount(): Flow<Int>

    // ----------------- IMPORTANT TOPICS -----------------
    @Query("SELECT * FROM important_topics ORDER BY unitNumber ASC, topicName ASC")
    fun getAllImportantTopics(): Flow<List<ImportantTopic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportantTopic(topic: ImportantTopic): Long

    @Query("DELETE FROM important_topics WHERE id = :id")
    suspend fun deleteImportantTopicById(id: Long)

    @Query("SELECT COUNT(*) FROM important_topics")
    fun getImportantTopicCount(): Flow<Int>

    // ----------------- STUDY RESOURCES -----------------
    @Query("SELECT * FROM study_resources ORDER BY uploadDate DESC")
    fun getAllStudyResources(): Flow<List<StudyResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyResource(resource: StudyResource): Long

    @Query("DELETE FROM study_resources WHERE id = :id")
    suspend fun deleteStudyResourceById(id: Long)

    @Query("UPDATE study_resources SET downloadsCount = downloadsCount + 1 WHERE id = :id")
    suspend fun incrementResourceDownloads(id: Long)

    @Query("SELECT COUNT(*) FROM study_resources")
    fun getStudyResourceCount(): Flow<Int>

    // ----------------- BOOKMARKS -----------------
    @Query("SELECT * FROM bookmarks WHERE userId = :userId")
    fun getBookmarksByUser(userId: Long): Flow<List<Bookmark>>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE userId = :userId AND noteId = :noteId")
    suspend fun isBookmarked(userId: Long, noteId: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND noteId = :noteId")
    suspend fun deleteBookmark(userId: Long, noteId: Long)

    // ----------------- DOWNLOADS -----------------
    @Insert
    suspend fun insertDownloadRecord(record: DownloadRecord): Long

    @Query("SELECT COUNT(*) FROM downloads")
    fun getTotalDownloadsCount(): Flow<Int>

    // ----------------- WEBSITE SETTINGS -----------------
    @Query("SELECT * FROM website_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<WebsiteSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: WebsiteSettings)
}
