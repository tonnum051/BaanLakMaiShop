package com.natchanon.baanlakmaishop.fragment

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
import android.content.Intent

class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private var imageUri: Uri? = null

    private val pickImage =
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

        val etName = view.findViewById<EditText>(R.id.etName)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val btnPickImage = view.findViewById<Button>(R.id.btnPickImage)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnPickImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnSave.setOnClickListener {

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
            if (imageUri == null) {
                Toast.makeText(
                    requireContext(),
                    "กรุณาเลือกรูปภาพสินค้า",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val product = Product(
                name = name,
                price = price,
                amount = amount,
                imageUri = imageUri?.toString()
            )

            // บันทึกลง SQLite database
            val productId = ProductRepository.addProduct(product)

            if (productId > 0) {
                Toast.makeText(
                    requireContext(),
                    "เพิ่มสินค้าเรียบร้อย",
                    Toast.LENGTH_SHORT
                ).show()

                (activity as? MainActivity)?.openProductList()
            } else {
                Toast.makeText(
                    requireContext(),
                    "เกิดข้อผิดพลาดในการเพิ่มสินค้า",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
}
