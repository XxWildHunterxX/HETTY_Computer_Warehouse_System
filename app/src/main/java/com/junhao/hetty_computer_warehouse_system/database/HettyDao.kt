package com.junhao.hetty_computer_warehouse_system.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface HettyDao {

    @Insert
    suspend fun insertUser(user:User)

    @Query("SELECT * from user_table")
    suspend fun showAll_User()




}