package com.example.Bountees.AddToCart

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

data class DataClassNew(var title: String, var image: String, var price: Int)

class Cart(context: Context) {
    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase

    fun addItemToCart(item: DataClassNew) {
        val values = ContentValues().apply {
            put("title", item.title)
            put("image", item.image)
            put("price",item.price)
        }

        db.insert("cart", null, values)
    }

    @SuppressLint("Range")
    fun getCartItems(): MutableList<DataClassNew> {
        val items = mutableListOf<DataClassNew>()
        val query = "SELECT * FROM cart"

        val cursor: Cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val image = cursor.getString(cursor.getColumnIndex("image"))
                val priceString = cursor.getInt(cursor.getColumnIndex("price"))
                val price = priceString.toInt()
//                val currency = cursor.getString(cursor.getColumnIndex("currency"))
                items.add(DataClassNew(title, image, price ))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }

        return items
    }

    //added
    fun deleteCartItem(item: DataClassNew) {
//        db.delete("cart", "${DatabaseHelper.PRODUCT_ID} = ?", arrayOf(item.toString()))
//        db.execSQL("DELETE FROM ")
//        val itemName = item.image
//        val itemImage = item.image
//        val itemPrice = item.price
//        val itemID = DatabaseHelper.PRODUCT_ID

        db.execSQL("DELETE FROM cart")
    }



}