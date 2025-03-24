package com.example.Bountees.AddToCart
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 3) {
    companion object{
        private const val DATABASE_NAME = "CartDB.db"
        private const val TABLE_NAME ="cart"
        const val PRODUCT_ID = "id"

        //        private const val PRODUCT_PRICE = "price"
        const val PRODUCT_IMAGE = "image"
        const val PRODUCT_NAME = "title"
        const val PRODUCT_PRICE = "price"
        const val PRODUCT_CURRENCY = "currency"
//        private const val PRODUCT_QUANTITY = "quantity"
    }
//
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME("+
                "$PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "$PRODUCT_IMAGE INT,"+
                "$PRODUCT_NAME TEXT,"+
                "$PRODUCT_PRICE INT,"+
                "$PRODUCT_CURRENCY TEXT)")
        db?.execSQL(createTableQuery)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS cart")
        onCreate(db)
    }


}

