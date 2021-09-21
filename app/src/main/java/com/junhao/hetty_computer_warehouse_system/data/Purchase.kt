package com.junhao.hetty_computer_warehouse_system.data

data class Purchase(
    val purchaseID :String?="",
    val purProductName: String?="",
    val purQty: String?="" ,
    val purPrice : String?="",
    val supplierName:String?="",
    val supplierAddress:String?="",
    val supplierContact:String?="",
    val requestDate:String?="",
    val receivedDate:String?="",
    val status:String?=""
)
