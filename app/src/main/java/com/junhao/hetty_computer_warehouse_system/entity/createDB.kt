package com.junhao.hetty_computer_warehouse_system.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey



@Entity
data class Warehouse(
    @PrimaryKey val warehouseID: String,
    @ColumnInfo val warehouseAddress: String,
    @ColumnInfo val warehousePhoneNum: String
)

@Entity(foreignKeys = [ForeignKey(entity = Warehouse::class, parentColumns = arrayOf("warehouseID"),
    childColumns = arrayOf("warehouseUserID"),onDelete=ForeignKey.CASCADE)])
data class User(
    @PrimaryKey val userID:String,
    @ColumnInfo val userName: String,
    @ColumnInfo val userGender: String,
    @ColumnInfo val userPhoneNum: Int,
    @ColumnInfo val warehouseUserID:String
)

@Entity(foreignKeys = [ForeignKey(entity = Warehouse::class, parentColumns = arrayOf("warehouseID"),
    childColumns = arrayOf("warehouseInvWarID"),onDelete=ForeignKey.CASCADE)])
data class WarehouseInventory(
    @PrimaryKey val warehouseInvID: String,
    @ColumnInfo val warehouseInvQTY: Int,
    @ColumnInfo val warehouseInvWarID:String
)

@Entity
data class Inventory(
    @PrimaryKey val invID: String,
    @ColumnInfo val invName: String,
    @ColumnInfo val invPrice: Double,
    @ColumnInfo val invCategory: String
)

@Entity(foreignKeys = [ForeignKey(entity = WarehouseInventory::class, parentColumns = arrayOf("warehouseInvID"), childColumns = arrayOf("purchaseWarehouseInvID")),
    ForeignKey(entity= User::class,parentColumns = arrayOf("userID"),childColumns = arrayOf("purchaseUserID"))])
data class Purchase(
    @PrimaryKey val purchaseID: String,
    @ColumnInfo val purchaseQTY: Int,
    @ColumnInfo val supplierCompany: String,
    @ColumnInfo val supplierPhoneNum: Int,
    @ColumnInfo val purchaseWarehouseInvID: String,
    @ColumnInfo val purchaseUserID:String
)


@Entity
data class Tracking(
    @PrimaryKey val trackID: String,
    @ColumnInfo val trackSection: String,
    @ColumnInfo val trackFloor: String
)

@Entity
data class salesTransaction(
    @PrimaryKey val salesTransID: String,
    @ColumnInfo val salesTransDate: Int
)

@Entity
data class salesDetail(
    @PrimaryKey val salesDetailID: String,
    @ColumnInfo val salesDetailQTY: Int
)


