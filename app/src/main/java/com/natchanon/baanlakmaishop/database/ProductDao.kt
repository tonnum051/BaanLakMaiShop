package com.natchanon.baanlakmaishop.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.natchanon.baanlakmaishop.model.Product

class ProductDao(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    // เปิด database สำหรับเขียน
    private fun getWritableDB(): SQLiteDatabase {
        return dbHelper.writableDatabase
    }

    // เปิด database สำหรับอ่าน
    private fun getReadableDB(): SQLiteDatabase {
        return dbHelper.readableDatabase
    }

    /**
     * เพิ่มสินค้าใหม่
     */
    fun insertProduct(product: Product): Long {
        val db = getWritableDB()
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, product.name)
            put(DatabaseHelper.COLUMN_PRICE, product.price)
            put(DatabaseHelper.COLUMN_AMOUNT, product.amount)
            put(DatabaseHelper.COLUMN_IMAGE_URI, product.imageUri)
        }

        val id = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values)
        db.close()
        return id
    }

    /**
     * อัปเดตสินค้า
     */
    fun updateProduct(id: Long, product: Product): Int {
        val db = getWritableDB()
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, product.name)
            put(DatabaseHelper.COLUMN_PRICE, product.price)
            put(DatabaseHelper.COLUMN_AMOUNT, product.amount)
            put(DatabaseHelper.COLUMN_IMAGE_URI, product.imageUri)
        }

        val rowsAffected = db.update(
            DatabaseHelper.TABLE_PRODUCTS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsAffected
    }

    /**
     * ลบสินค้า
     */
    fun deleteProduct(id: Long): Int {
        val db = getWritableDB()
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_PRODUCTS,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsDeleted
    }

    /**
     * ดึงสินค้าทั้งหมด
     */
    fun getAllProducts(): List<ProductWithId> {
        val products = mutableListOf<ProductWithId>()
        val db = getReadableDB()
        
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTS,
            null, // ดึงทุก column
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_ID} DESC" // เรียงจากใหม่ไปเก่า
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE))
                val amount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URI))

                val product = Product(
                    name = name,
                    price = price,
                    amount = amount,
                    imageUri = imageUri
                )
                
                products.add(ProductWithId(id, product))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return products
    }

    /**
     * ดึงสินค้าตาม ID
     */
    fun getProductById(id: Long): ProductWithId? {
        val db = getReadableDB()
        var productWithId: ProductWithId? = null

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTS,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE))
            val amount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URI))

            val product = Product(
                name = name,
                price = price,
                amount = amount,
                imageUri = imageUri
            )
            
            productWithId = ProductWithId(id, product)
        }

        cursor.close()
        db.close()
        return productWithId
    }

    /**
     * ลบสินค้าทั้งหมด
     */
    fun deleteAllProducts(): Int {
        val db = getWritableDB()
        val rowsDeleted = db.delete(DatabaseHelper.TABLE_PRODUCTS, null, null)
        db.close()
        return rowsDeleted
    }

    /**
     * นับจำนวนสินค้าทั้งหมด
     */
    fun getProductCount(): Int {
        val db = getReadableDB()
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_PRODUCTS}", null)
        var count = 0
        
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        
        cursor.close()
        db.close()
        return count
    }

    /**
     * ปิด database
     */
    fun close() {
        dbHelper.close()
    }
}

/**
 * Data class สำหรับเก็บ Product พร้อม ID จาก database
 */
data class ProductWithId(
    val id: Long,
    val product: Product
)
