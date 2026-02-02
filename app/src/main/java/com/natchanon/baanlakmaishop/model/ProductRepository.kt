package com.natchanon.baanlakmaishop.model

import android.content.Context
import com.natchanon.baanlakmaishop.database.ProductDao
import com.natchanon.baanlakmaishop.database.ProductWithId

object ProductRepository {
    private var productDao: ProductDao? = null

    /**
     * เริ่มต้น Repository ด้วย Context
     */
    fun initialize(context: Context) {
        if (productDao == null) {
            productDao = ProductDao(context.applicationContext)
        }
    }

    /**
     * เพิ่มสินค้าใหม่
     */
    fun addProduct(product: Product): Long {
        return productDao?.insertProduct(product) ?: -1
    }

    /**
     * อัปเดตสินค้า
     */
    fun updateProduct(id: Long, product: Product): Boolean {
        val rowsAffected = productDao?.updateProduct(id, product) ?: 0
        return rowsAffected > 0
    }

    /**
     * ลบสินค้า
     */
    fun deleteProduct(id: Long): Boolean {
        val rowsDeleted = productDao?.deleteProduct(id) ?: 0
        return rowsDeleted > 0
    }

    /**
     * ดึงสินค้าทั้งหมด
     */
    fun getAllProducts(): List<ProductWithId> {
        return productDao?.getAllProducts() ?: emptyList()
    }

    /**
     * ดึงสินค้าตาม ID
     */
    fun getProductById(id: Long): ProductWithId? {
        return productDao?.getProductById(id)
    }

    /**
     * ลบสินค้าทั้งหมด
     */
    fun deleteAllProducts(): Boolean {
        val rowsDeleted = productDao?.deleteAllProducts() ?: 0
        return rowsDeleted > 0
    }

    /**
     * นับจำนวนสินค้า
     */
    fun getProductCount(): Int {
        return productDao?.getProductCount() ?: 0
    }

    /**
     * ปิด database (ควรเรียกเมื่อแอปปิด)
     */
    fun close() {
        productDao?.close()
    }
}
