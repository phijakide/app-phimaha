package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sku: String,
    val name: String,
    val category: String,
    val priceUsd: Double,
    val stockQuantity: Int,
    val reorderLevel: Int,
    val supplierName: String,
    val rating: Float,
    val description: String,
    val isRecommended: Boolean = false,
    val warehouseBin: String = "A-12"
)

@Entity(tableName = "inventory_logs")
data class InventoryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sku: String,
    val productName: String,
    val changeAmount: Int,
    val previousStock: Int,
    val newStock: Int,
    val reason: String, // "Received", "Dispatched", "Audit Correction", "Damaged"
    val personnelName: String,
    val notes: String = ""
)

@Entity(tableName = "voice_memos")
data class VoiceMemoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val audioDurationSec: Int,
    val transcribedText: String,
    val modelUsed: String = "gemini-3.5-transcribe",
    val category: String, // "Warehouse Audit", "Damage Note", "Dispatch Audio", "Field Inspection"
    val isSynced: Boolean = true
)

@Entity(tableName = "video_inspections")
data class VideoInspectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val videoFileName: String,
    val summary: String,
    val detectedIssues: String,
    val barcodeDetected: String,
    val complianceStatus: String, // "PASSED", "WARNING", "FAILED"
    val analysisResult: String,
    val modelUsed: String = "gemini-3.1-pro-preview"
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val totalAmount: Double,
    val currency: String = "USD",
    val status: String, // "Processing", "Packed", "Shipped", "In Transit", "Delivered"
    val itemsCount: Int,
    val trackingNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "inquiries")
data class InquiryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val customerName: String,
    val email: String,
    val category: String, // "Order Inquiry", "Inventory Stock", "Logistics & Customs", "Wholesale"
    val inquiryMessage: String,
    val aiSuggestedResponse: String = "",
    val status: String = "Open" // "Open", "Replied", "Resolved"
)
