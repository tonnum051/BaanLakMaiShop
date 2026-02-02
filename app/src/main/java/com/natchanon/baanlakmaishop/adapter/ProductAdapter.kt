package com.natchanon.baanlakmaishop.adapter

import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.natchanon.baanlakmaishop.R
import com.natchanon.baanlakmaishop.database.ProductWithId
import com.natchanon.baanlakmaishop.model.ProductRepository

class ProductAdapter(
    private var products: MutableList<ProductWithId>,
    private val onEditClick: (ProductWithId) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgItem)
        val name: TextView = view.findViewById(R.id.tvName)
        val amount: TextView = view.findViewById(R.id.tvAmount)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productWithId = products[position]
        val product = productWithId.product

        holder.name.text = product.name
        holder.amount.text = "จำนวน: ${product.amount}"
        holder.price.text = "ราคา: ${product.price}"

        if (product.imageUri != null) {
            holder.img.setImageURI(Uri.parse(product.imageUri))
        } else {
            holder.img.setImageResource(R.drawable.productimg)
        }

        // ===== ปุ่มแก้ไข =====
        holder.btnEdit.setOnClickListener {
            onEditClick(productWithId)
        }

        // ===== ปุ่มลบ + แจ้งเตือน =====
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("ยืนยันการลบ")
                .setMessage("คุณต้องการลบสินค้า \"${product.name}\" ใช่หรือไม่?")
                .setPositiveButton("ลบ") { _, _ ->
                    // ลบจาก database
                    val success = ProductRepository.deleteProduct(productWithId.id)
                    
                    if (success) {
                        products.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, products.size)
                        
                        Toast.makeText(
                            it.context,
                            "ลบสินค้าเรียบร้อย",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            it.context,
                            "เกิดข้อผิดพลาดในการลบ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("ยกเลิก", null)
                .show()
        }
    }

    override fun getItemCount(): Int = products.size

    /**
     * ฟังก์ชันสำหรับอัปเดตรายการสินค้าทั้งหมด
     */
    fun updateProducts(newProducts: MutableList<ProductWithId>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
