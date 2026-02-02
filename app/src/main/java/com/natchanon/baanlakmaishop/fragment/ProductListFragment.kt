package com.natchanon.baanlakmaishop.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natchanon.baanlakmaishop.MainActivity
import com.natchanon.baanlakmaishop.R
import com.natchanon.baanlakmaishop.adapter.ProductAdapter
import com.natchanon.baanlakmaishop.model.ProductRepository

class ProductListFragment : Fragment(R.layout.fragment_product_list) {

    private lateinit var adapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvProducts = view.findViewById<RecyclerView>(R.id.rvProducts)


        // ดึงข้อมูลจาก SQLite database
        val productsWithId = ProductRepository.getAllProducts().toMutableList()

        adapter = ProductAdapter(productsWithId) { productWithId ->
            val fragment = EditProductFragment().apply {
                arguments = Bundle().apply {
                    putLong("productId", productWithId.id)
                }
            }

            (requireActivity() as MainActivity).navigateTo(fragment)
        }

        rvProducts.layoutManager = LinearLayoutManager(requireContext())
        rvProducts.adapter = adapter


    }

    override fun onResume() {
        super.onResume()
        // รีเฟรชข้อมูลจาก database
        val productsWithId = ProductRepository.getAllProducts().toMutableList()
        adapter.updateProducts(productsWithId)
    }
}
