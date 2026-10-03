package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.InquiryDao
import com.example.data.local.dao.InventoryLogDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.VideoInspectionDao
import com.example.data.local.dao.VoiceMemoDao
import com.example.data.local.entity.InquiryEntity
import com.example.data.local.entity.InventoryLogEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.VideoInspectionEntity
import com.example.data.local.entity.VoiceMemoEntity

@Database(
    entities = [
        ProductEntity::class,
        InventoryLogEntity::class,
        VoiceMemoEntity::class,
        VideoInspectionEntity::class,
        OrderEntity::class,
        InquiryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun inventoryLogDao(): InventoryLogDao
    abstract fun voiceMemoDao(): VoiceMemoDao
    abstract fun videoInspectionDao(): VideoInspectionDao
    abstract fun orderDao(): OrderDao
    abstract fun inquiryDao(): InquiryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "supplyflow_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
