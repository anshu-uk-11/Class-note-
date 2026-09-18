package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String, // "STUDENT" or "ADMIN"
    val department: String = "Computer Science & Engineering",
    val semester: String = "Semester 3",
    val joinedDate: Long = System.currentTimeMillis()
)

object UserRole {
    const val STUDENT = "STUDENT"
    const val ADMIN = "ADMIN"
}

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val semester: String, // e.g. "Semester 1", "Semester 2", "Semester 3"
    val department: String = "Computer Science & Engineering",
    val description: String = "",
    val credits: Int = 4,
    val iconName: String = "code"
)

@Entity(tableName = "semesters")
data class AcademicSemester(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // "Semester 1", "Semester 2", ...
    val yearName: String, // "Year 1", "Year 2", ...
    val academicYear: String = "2025-2026"
)

@Entity(tableName = "class_sections")
data class ClassSection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "CSE-A", "CSE-B", "IT-A"
    val semesterName: String,
    val department: String = "Computer Science & Engineering"
)

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["subject"]),
        Index(value = ["semester"]),
        Index(value = ["classSection"]),
        Index(value = ["priority"])
    ]
)
data class NoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val subject: String,
    val semester: String,
    val academicYear: String = "2025-2026",
    val classSection: String,
    val unitNumber: Int,
    val unitName: String,
    val topic: String,
    val tags: String, // comma-separated
    val noteType: String, // Class Notes, Lecture Notes, Revision Notes, Assignment, Lab Manual, Question Bank, Study Material, Other
    val priority: String, // Normal, Important, Very Important, Exam Priority
    val fileName: String,
    val fileType: String, // PDF, DOCX, PPTX, etc.
    val fileSizeFormatted: String,
    val filePath: String,
    val contentPreview: String = "",
    val uploadedBy: String = "Admin Faculty",
    val uploadDate: Long = System.currentTimeMillis(),
    val downloadsCount: Int = 0
)

@Entity(
    tableName = "previous_papers",
    indices = [
        Index(value = ["subject"]),
        Index(value = ["examType"]),
        Index(value = ["examYear"])
    ]
)
data class PreviousPaper(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val semester: String,
    val classSection: String,
    val examType: String, // Mid Term, Internal, End Semester, Practical, Other
    val examYear: String, // "2024", "2023", etc.
    val academicYear: String = "2023-2024",
    val description: String = "",
    val fileName: String,
    val fileType: String = "PDF",
    val fileSizeFormatted: String = "2.1 MB",
    val filePath: String = "",
    val uploadDate: Long = System.currentTimeMillis(),
    val downloadsCount: Int = 0
)

@Entity(tableName = "important_topics")
data class ImportantTopic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicName: String,
    val subject: String,
    val semester: String,
    val classSection: String,
    val unitNumber: Int,
    val unitName: String,
    val description: String,
    val priority: String, // Important, Very Important, Exam Priority
    val relatedNoteTitle: String = ""
)

@Entity(tableName = "study_resources")
data class StudyResource(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val resourceType: String, // PDF, PPT, Assignment, Lab Manual, Question Bank, Useful Website, Video Lecture, Other
    val subject: String,
    val semester: String,
    val description: String,
    val urlOrPath: String,
    val fileName: String = "",
    val fileSizeFormatted: String = "",
    val isExternalLink: Boolean = false,
    val uploadDate: Long = System.currentTimeMillis(),
    val downloadsCount: Int = 0
)

@Entity(
    tableName = "bookmarks",
    indices = [Index(value = ["userId", "noteId"], unique = true)]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val noteId: Long,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val contentId: Long,
    val contentType: String, // "NOTE", "PAPER", "RESOURCE"
    val contentTitle: String,
    val downloadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "website_settings")
data class WebsiteSettings(
    @PrimaryKey val id: Int = 1,
    val websiteName: String = "Class Notes Hub",
    val tagline: String = "All Your Class Notes in One Place",
    val welcomeMessage: String = "Access verified lecture notes, previous question papers, unit summaries, and curated study materials prepared by university faculty.",
    val announcement: String = "Mid-semester exams start next week. Check the Exam Priority notes and Previous Question Papers!",
    val footerText: String = "© 2026 Class Notes Hub • Academic Knowledge Repository for Higher Education",
    val themeColorHex: String = "#0F2744",
    val lastUpdated: Long = System.currentTimeMillis()
)

data class DashboardStats(
    val totalUsers: Int = 0,
    val totalNotes: Int = 0,
    val totalSubjects: Int = 0,
    val totalPreviousPapers: Int = 0,
    val totalImportantTopics: Int = 0,
    val totalResources: Int = 0,
    val totalDownloads: Int = 0
)
