package com.junhao.hetty_computer_warehouse_system.data

data class Purchase(
    val purchaseID :String?="",
    val purProductName: String?="",
    val purQty: String?="",
    val costPerUnit : String?="",
    val totalCost:String?="",
    val supplierName:String?="",
    val supplierAddress:String?="",
    val supplierContact:String?="",
    val requestDate:String?="",
    val acceptDate:String?="",
    val rejectDate:String?="",
    val deliverDate:String?="",
    var receivedDate:String?="",
    var status:String?=""
)
