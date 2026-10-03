package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.InquiryEntity
import com.example.data.local.entity.InventoryLogEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.VideoInspectionEntity
import com.example.data.local.entity.VoiceMemoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isRecommended = 1")
    fun getRecommendedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun getProductBySku(sku: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = :newStock WHERE id = :id")
    suspend fun updateStock(id: Long, newStock: Int)
}

@Dao
interface InventoryLogDao {
    @Query("SELECT * FROM inventory_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<InventoryLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: InventoryLogEntity): Long
}

@Dao
interface VoiceMemoDao {
    @Query("SELECT * FROM voice_memos ORDER BY timestamp DESC")
    fun getAllMemos(): Flow<List<VoiceMemoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: VoiceMemoEntity): Long

    @Query("DELETE FROM voice_memos WHERE id = :id")
    suspend fun deleteMemo(id: Long)
}

@Dao
interface VideoInspectionDao {
    @Query("SELECT * FROM video_inspections ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<VideoInspectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: VideoInspectionEntity): Long
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: String)
}

@Dao
interface InquiryDao {
    @Query("SELECT * FROM inquiries ORDER BY timestamp DESC")
    fun getAllInquiries(): Flow<List<InquiryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: InquiryEntity): Long

    @Update
    suspend fun updateInquiry(inquiry: InquiryEntity)
}
