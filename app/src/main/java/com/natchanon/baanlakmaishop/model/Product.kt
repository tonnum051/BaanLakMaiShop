package com.natchanon.baanlakmaishop.model

data class Product(
    var name: String,
    var price: Double,
    var amount: Int,
    var imageUri: String? = null
)
