package com.natchanon.baanlakmaishop.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.natchanon.baanlakmaishop.MainActivity
import com.natchanon.baanlakmaishop.R
import com.natchanon.baanlakmaishop.model.Product
import com.natchanon.baanlakmaishop.model.ProductRepository


class EditProductFragment : Fragment(R.layout.fragment_edit_product) {

    private var imageUri: Uri? = null
    private lateinit var imgEdit: ImageView
    private var currentProductId: Long = -1

    private val pickEditImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            uri?.let {
                imageUri = it

                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                view?.findViewById<ImageView>(R.id.imgProduct)
                    ?.setImageURI(it)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // รับ productId จาก arguments
        currentProductId = requireArguments().getLong("productId", -1)
        
        if (currentProductId == -1L) {
            Toast.makeText(requireContext(), "ไม่พบข้อมูลสินค้า", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        // ดึงข้อมูลจาก database
        val productWithId = ProductRepository.getProductById(currentProductId)
        
        if (productWithId == null) {
            Toast.makeText(requireContext(), "ไม่พบข้อมูลสินค้า", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val product = productWithId.product

        val etName = view.findViewById<EditText>(R.id.etEditName)
        val etPrice = view.findViewById<EditText>(R.id.etEditPrice)
        val etAmount = view.findViewById<EditText>(R.id.etEditAmount)
        imgEdit = view.findViewById(R.id.imgEdit)

        // set ค่าเดิม
        etName.setText(product.name)
        etPrice.setText(product.price.toString())
        etAmount.setText(product.amount.toString())

        product.imageUri?.let {
            imgEdit.setImageURI(Uri.parse(it))
        }

        view.findViewById<Button>(R.id.btnPickEditImage).setOnClickListener {
            pickEditImage.launch("image/*")
        }

        view.findViewById<Button>(R.id.btnSaveEdit).setOnClickListener {

            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().toDoubleOrNull()
            val amount = etAmount.text.toString().toIntOrNull()

            if (name.isEmpty() || price == null || amount == null) {
                Toast.makeText(
                    requireContext(),
                    "กรุณากรอกข้อมูลให้ถูกต้อง",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // สร้าง Product object ใหม่
            val updatedProduct = Product(
                name = name,
                price = price,
                amount = amount,
                imageUri = imageUri?.toString() ?: product.imageUri
            )

            // อัปเดตใน database
            val success = ProductRepository.updateProduct(currentProductId, updatedProduct)

            if (success) {
                    Toast.makeText(
                        requireContext(),
                        "เพิ่มสินค้าเรียบร้อย",
                        Toast.LENGTH_SHORT
                    ).show()

                    (activity as? MainActivity)?.openProductList()
                } else {
                Toast.makeText(
                    requireContext(),
                    "เกิดข้อผิดพลาดในการแก้ไข",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
