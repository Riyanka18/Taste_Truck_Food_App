package com.example.spicezilla.model

import org.json.JSONArray

data class Orders(
    val orderId: Int,
    val resName: String,
    val orderDate: String,
    val foodItem: JSONArray
)