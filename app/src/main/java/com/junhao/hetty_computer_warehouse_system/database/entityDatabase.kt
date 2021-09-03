package com.junhao.hetty_computer_warehouse_system.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "warehouse_table")
data class Warehouse(
    @PrimaryKey(autoGenerate = true) val warehouseID: Int = 1001,
    @ColumnInfo(name = "warehouse_address") val warehouseAddress: String?,
    @ColumnInfo(name = "warehouse_phoneNum") val warehousePhoneNum: String?,
    @ColumnInfo(name = "warehouse_email") val warehouseEmail: String?
)

@Entity(tableName = "user_table",foreignKeys = [ForeignKey(entity = Warehouse::class,
    parentColumns = arrayOf("warehouseID"), childColumns = arrayOf("warehouse_UserID"))])
data class User(
    @PrimaryKey(autoGenerate = true) val userID:Int = 1001,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_gender") val userGender: String,
    @ColumnInfo(name = "user_dob") val userDob: String,
    @ColumnInfo(name = "user_address") val userAddress: String,
    @ColumnInfo(name = "user_phoneNum") val userPhoneNum: String,
    @ColumnInfo(name = "user_email") val userEmail: String,
    @ColumnInfo(name = "user_password") val userPassword: String,
    @ColumnInfo(name = "user_image") val userImage: Blob,
    @ColumnInfo(name = "user_joinDate") val userJoinDate: String,
    @ColumnInfo(name = "user_position") val userPosition: String,
    @ColumnInfo(name = "user_warehouseID") val warehouse_UserID:Int
)

@Entity
data class Inventory(
    @PrimaryKey val invID: Int = 1001,
    @ColumnInfo val invName: String,
    @ColumnInfo val invPrice: Double,
    @ColumnInfo val invCategory: String
)

@Entity(tableName = "warehouseInventory_table",foreignKeys = [ForeignKey(entity = Warehouse::class,
    parentColumns = arrayOf("warehouseID"), childColumns = arrayOf("warehouseInv_WarID")),
    ForeignKey(entity = Inventory::class,parentColumns=arrayOf("invID"),childColumns=arrayOf("warehouseInv_invID"))])
data class WarehouseInventory(
    @PrimaryKey(autoGenerate = true) val warehouseInvID: Int = 1001,
    @ColumnInfo(name = "warehouseInv_qty") val warehouseInvQTY: Int,
    @ColumnInfo(name = "warehouseInv_warehouseID") val warehouseInv_WarID:Int,
    @ColumnInfo(name = "warehouseInv_invID") val warehouseInv_invID:Int
)


@Entity(tableName = "purchase_table", foreignKeys = [ForeignKey(entity = WarehouseInventory::class, parentColumns = arrayOf("warehouseInvID"), childColumns = arrayOf("purchase_warehouseInvID")),
    ForeignKey(entity= User::class,parentColumns = arrayOf("userID"),childColumns = arrayOf("purchase_userID"))])
data class Purchase(
    @PrimaryKey(autoGenerate = true) val purchaseID: Int = 1001,
    @ColumnInfo(name = "purchase_qty") val purchaseQTY: Int,
    @ColumnInfo(name = "purchase_supplierCompany") val supplierCompany: String,
    @ColumnInfo(name = "purchase_phoneNum") val supplierPhoneNum: String,
    @ColumnInfo(name = "purchase_warehouseInvID") val purchase_warehouseInvID: Int,
    @ColumnInfo(name = "purchase_userID") val purchase_userID:Int
)

@Entity(tableName = "tracking_table",foreignKeys = [ForeignKey(entity = WarehouseInventory::class, parentColumns = arrayOf("warehouseInvID"), childColumns = arrayOf("track_warehouseInvID")),
    ForeignKey(entity= User::class,parentColumns = arrayOf("userID"),childColumns = arrayOf("track_userID"))])
data class Tracking(
    @PrimaryKey(autoGenerate = true) val trackID: Int = 1001,
    @ColumnInfo(name = "track_section") val trackSection: String,
    @ColumnInfo(name = "track_floor") val trackFloor: String,
    @ColumnInfo(name = "track_warehouseInvID") val track_warehouseInvID:Int,
    @ColumnInfo(name = "track_userID") val track_userID:Int
)

@Entity(tableName = "salesTransaction_table",foreignKeys = [ForeignKey(entity = User::class, parentColumns = arrayOf("userID"), childColumns = arrayOf("salesTrans_UserID"))])
data class SalesTransaction(
    @PrimaryKey(autoGenerate = true) val salesTransID: Int = 1001,
    @ColumnInfo(name = "salesTrans_date") val salesTransDate: String,
    @ColumnInfo(name = "salesTrans_userID") val salesTrans_UserID:Int
)

@Entity(tableName = "salesDetail_table",foreignKeys = [ForeignKey(entity = SalesTransaction::class, parentColumns = arrayOf("salesTransID"), childColumns = arrayOf("salesDetail_salesTransID")),
    ForeignKey(entity= WarehouseInventory::class,parentColumns = arrayOf("warehouseInvID"),childColumns = arrayOf("salesDetail_WarehouseInvID"))])
data class SalesDetail(
    @PrimaryKey(autoGenerate = true) val salesDetailID: Int = 1001,
    @ColumnInfo(name = "salesDetail_qty") val salesDetailQTY: Int,
    @ColumnInfo(name = "salesDetail_salesTransID") val salesDetail_salesTransID:Int,
    @ColumnInfo(name = "salesDetail_warInvID") val salesDetail_WarehouseInvID:Int
)
