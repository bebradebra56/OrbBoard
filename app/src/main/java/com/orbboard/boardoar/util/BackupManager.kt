package com.orbboard.boardoar.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.orbboard.boardoar.data.local.database.OrbDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(private val context: Context) {

    suspend fun exportDatabase(): Uri? = withContext(Dispatchers.IO) {
        return@withContext try {
            val dbFile = context.getDatabasePath(OrbDatabase.DATABASE_NAME)
            if (!dbFile.exists()) return@withContext null
            val exportDir = File(context.getExternalFilesDir(null), "backups")
            exportDir.mkdirs()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
            val exportFile = File(exportDir, "orb_board_backup_$timestamp.db")
            dbFile.copyTo(exportFile, overwrite = true)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                exportFile
            )
        } catch (e: Exception) {
            null
        }
    }

    fun createShareIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
