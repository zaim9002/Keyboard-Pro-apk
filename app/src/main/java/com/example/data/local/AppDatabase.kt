package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ClipboardDao
import com.example.data.local.dao.ShortcutDao
import com.example.data.local.dao.UserWordDao
import com.example.data.local.entity.ClipboardEntity
import com.example.data.local.entity.ShortcutEntity
import com.example.data.local.entity.UserWordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClipboardEntity::class,
        ShortcutEntity::class,
        UserWordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clipboardDao(): ClipboardDao
    abstract fun shortcutDao(): ShortcutDao
    abstract fun userWordDao(): UserWordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keyboard_pro_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            try {
                                populateDefaults(db)
                            } catch (e: Exception) {
                                android.util.Log.e("AppDatabase", "Error seeding defaults", e)
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun populateDefaults(db: SupportSQLiteDatabase) {
            val now = System.currentTimeMillis()
            // Direct SQLite execution avoids deadlocking Room's builder during onCreate
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('سلام', 'السلام عليكم ورحمة الله وبركاته', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('شكرا', 'شكراً جزيلاً لك، بارك الله فيك', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('brb', 'Be right back!', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('omw', 'On my way!', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('صباح', 'صباح الخير والمسرات ☀️', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('مساء', 'مساء الخير والعافية 🌙', $now)")

            db.execSQL("INSERT OR IGNORE INTO clipboard_items (text, timestamp, isPinned, folder) VALUES ('مرحباً بك في Keyboard Pro! يمكنك تثبيت النصوص المهمة هنا لتبقى دائماً.', $now, 1, 'مهم')")
            db.execSQL("INSERT OR IGNORE INTO clipboard_items (text, timestamp, isPinned, folder) VALUES ('سبحان الله والحمد لله ولا إله إلا الله والله أكبر', $now, 1, 'أذكار')")
        }
    }
}
