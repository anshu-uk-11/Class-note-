package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        User::class,
        Subject::class,
        AcademicSemester::class,
        ClassSection::class,
        NoteItem::class,
        PreviousPaper::class,
        ImportantTopic::class,
        StudyResource::class,
        Bookmark::class,
        DownloadRecord::class,
        WebsiteSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "class_notes_hub.db"
                ).fallbackToDestructiveMigration(false)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
