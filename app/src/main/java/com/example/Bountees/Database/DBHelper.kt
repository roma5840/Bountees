package com.example.loginactivity

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(private val context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION= 2
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD= "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTableQuery)
        Log.i("test","database")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }
    fun insertUser(username: String, password: String): Long {
        return if (!userExists(username)) {
            val values = ContentValues().apply {
                put(COLUMN_USERNAME, username)
                put(COLUMN_PASSWORD, password)

            }
            val db = writableDatabase
            db.insert(TABLE_NAME, null, values)
        } else {
            -1
        }
    }
    fun readUser(username: String, password: String):Boolean{
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME= ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_NAME,null, selection,selectionArgs,null,null,null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists

    }
    private fun userExists(username: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()

        return userExists
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME)
                val passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD)

                if (idIndex != -1 && usernameIndex != -1 && passwordIndex != -1) {
                    val id = cursor.getLong(idIndex)
                    val username = cursor.getString(usernameIndex)
                    val password = cursor.getString(passwordIndex)

                    users.add(User(id, username, password))
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return users
    }

    data class User(
        val id: Long,
        val username: String,
        val password: String
    )


}