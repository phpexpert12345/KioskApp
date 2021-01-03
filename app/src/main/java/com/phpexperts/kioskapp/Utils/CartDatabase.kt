package com.phpexperts.kioskapp.Utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.phpexperts.kioskapp.Models.OrderCartDao
import com.phpexperts.kioskapp.Models.OrderCartItem
import com.phpexperts.kioskapp.Models.ToppingDao
import com.phpexperts.kioskapp.Models.ToppingItems

@Database(entities = arrayOf(OrderCartItem::class, ToppingItems::class), version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun OrderCartDao(): OrderCartDao?
    abstract fun ToppingDao():ToppingDao?
    companion object{
        var INSTANCE: CartDatabase? = null
        fun getDataBase(context:Context):CartDatabase?{
            if (INSTANCE == null) {
                synchronized(CartDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CartDatabase::class.java, "cart_database"
                        )
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return INSTANCE
        }
        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
//            PopulateDbAsync(INSTANCE).execute()
            }
        }
    }


}