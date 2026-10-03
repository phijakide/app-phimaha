package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.InquiryEntity
import com.example.data.local.entity.InventoryLogEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.VideoInspectionEntity
import com.example.data.local.entity.VoiceMemoEntity
import com.example.data.remote.GeminiApiService
import com.example.data.remote.ThinkingResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SupplyChainRepository(
    private val database: AppDatabase,
    val geminiApi: GeminiApiService = GeminiApiService()
) {
    private val productDao = database.productDao()
    private val inventoryLogDao = database.inventoryLogDao()
    private val voiceMemoDao = database.voiceMemoDao()
    private val videoInspectionDao = database.videoInspectionDao()
    private val orderDao = database.orderDao()
    private val inquiryDao = database.inquiryDao()

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val recommendedProducts: Flow<List<ProductEntity>> = productDao.getRecommendedProducts()
    val inventoryLogs: Flow<List<InventoryLogEntity>> = inventoryLogDao.getAllLogs()
    val voiceMemos: Flow<List<VoiceMemoEntity>> = voiceMemoDao.getAllMemos()
    val videoInspections: Flow<List<VideoInspectionEntity>> = videoInspectionDao.getAllInspections()
    val orders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val inquiries: Flow<List<InquiryEntity>> = inquiryDao.getAllInquiries()

    suspend fun seedInitialDataIfEmpty() {
        val currentProducts = productDao.getAllProducts().first()
        if (currentProducts.isEmpty()) {
            val defaultProducts = listOf(
                ProductEntity(
                    sku = "SKU-LOG-84920",
                    name = "Industrial Sensor Controller",
                    category = "Warehouse IoT",
                    priceUsd = 349.99,
                    stockQuantity = 42,
                    reorderLevel = 15,
                    supplierName = "CyberLogix Systems",
                    rating = 4.9f,
                    description = "Ultra-rugged IP67 telemetry hub with multi-protocol CAN bus, LoRaWAN and real-time vibration sensing for automated conveyors.",
                    isRecommended = true,
                    warehouseBin = "Aisle 04, Bin B-12"
                ),
                ProductEntity(
                    sku = "SKU-COLD-55012",
                    name = "Smart Thermal Cold-Chain Logger",
                    category = "Cold-Chain",
                    priceUsd = 129.00,
                    stockQuantity = 8,
                    reorderLevel = 20,
                    supplierName = "FrostGuard Tech",
                    rating = 4.8f,
                    description = "Continuous temperature (-80°C to +60°C) and relative humidity logger with NIST calibration and Bluetooth instant audit download.",
                    isRecommended = true,
                    warehouseBin = "Cold Vault C-01"
                ),
                ProductEntity(
                    sku = "SKU-RFID-10294",
                    name = "RFID Ultra-Rugged Pallet Beacon",
                    category = "Tracking & RFID",
                    priceUsd = 79.50,
                    stockQuantity = 180,
                    reorderLevel = 50,
                    supplierName = "OmniTrack RFID",
                    rating = 4.7f,
                    description = "Gen2 passive & active hybrid pallet tracking transponder with 30m detection radius through high-density metal stacking.",
                    isRecommended = false,
                    warehouseBin = "Aisle 02, Bin D-08"
                ),
                ProductEntity(
                    sku = "SKU-ROB-99301",
                    name = "Automated AGV Optical Barcode Scanner",
                    category = "Robotics & AGV",
                    priceUsd = 1249.00,
                    stockQuantity = 14,
                    reorderLevel = 5,
                    supplierName = "AcroMotion Robotics",
                    rating = 5.0f,
                    description = "Dual-camera high-resolution omni-directional scanner designed for autonomous guided vehicles and robot picker arms.",
                    isRecommended = true,
                    warehouseBin = "Aisle 01, Bin R-03"
                ),
                ProductEntity(
                    sku = "SKU-SCN-77144",
                    name = "Handheld Wireless Laser 2D Reader",
                    category = "Field Hardware",
                    priceUsd = 215.00,
                    stockQuantity = 55,
                    reorderLevel = 15,
                    supplierName = "ScanSpeed Global",
                    rating = 4.6f,
                    description = "Ergonomic Android-compatible wireless barcode reader with haptic feedback, 24-hour battery shift, and drop resistance to 2.5m.",
                    isRecommended = false,
                    warehouseBin = "Tool Crib T-04"
                ),
                ProductEntity(
                    sku = "SKU-PKG-44211",
                    name = "ESD Anti-Static Cushion Packaging Roll",
                    category = "Packaging & Supplies",
                    priceUsd = 35.00,
                    stockQuantity = 620,
                    reorderLevel = 150,
                    supplierName = "SafePack Materials",
                    rating = 4.5f,
                    description = "Electrostatic-discharge dissipative bubble wrap with reinforced tensile polymer layers for sensitive semiconductor electronics.",
                    isRecommended = false,
                    warehouseBin = "Bay 07, Bulk Staging"
                )
            )
            productDao.insertProducts(defaultProducts)

            // Seed orders
            val defaultOrders = listOf(
                OrderEntity(
                    orderNumber = "ORD-2026-9012",
                    customerName = "Global Logistics Enterprise",
                    totalAmount = 4280.00,
                    currency = "USD",
                    status = "In Transit",
                    itemsCount = 12,
                    trackingNumber = "SF-US-89104-EXP"
                ),
                OrderEntity(
                    orderNumber = "ORD-2026-9013",
                    customerName = "Pacific Freight Terminal Hub",
                    totalAmount = 1590.00,
                    currency = "USD",
                    status = "Out for Delivery",
                    itemsCount = 4,
                    trackingNumber = "SF-EU-33921-PRI"
                ),
                OrderEntity(
                    orderNumber = "ORD-2026-9014",
                    customerName = "Nordic Supply Chain AB",
                    totalAmount = 8900.00,
                    currency = "USD",
                    status = "Processing",
                    itemsCount = 25,
                    trackingNumber = "SF-NO-49182-STD"
                ),
                OrderEntity(
                    orderNumber = "ORD-2026-9015",
                    customerName = "Apex Micro Manufacturing",
                    totalAmount = 349.99,
                    currency = "USD",
                    status = "Delivered",
                    itemsCount = 1,
                    trackingNumber = "SF-AP-11029-LOC"
                )
            )
            orderDao.insertOrders(defaultOrders)

            // Seed initial inventory log
            inventoryLogDao.insertLog(
                InventoryLogEntity(
                    sku = "SKU-LOG-84920",
                    productName = "Industrial Sensor Controller",
                    changeAmount = +20,
                    previousStock = 22,
                    newStock = 42,
                    reason = "Received Inbound",
                    personnelName = "J. Vance (Shift Supv)",
                    notes = "Passed visual inspection at Receiving Dock 3."
                )
            )

            // Seed sample voice memo
            voiceMemoDao.insertMemo(
                VoiceMemoEntity(
                    title = "Aisle 04 Audit & Reorder Check",
                    audioDurationSec = 14,
                    transcribedText = "Warehouse audit complete for Zone A Aisle 4. Counted forty-two units of Industrial Sensor Controllers. Reorder threshold is set to fifteen. Stock status healthy.",
                    modelUsed = "gemini-3.5-transcribe",
                    category = "Warehouse Audit"
                )
            )

            // Seed sample video inspection
            videoInspectionDao.insertInspection(
                VideoInspectionEntity(
                    title = "Pallet Inbound Verification #84920",
                    videoFileName = "inbound_pallet_dock3.mp4",
                    summary = "Pallet packaging intact with unbroken security tape. Minor cosmetic scratch on carton exterior.",
                    detectedIssues = "Cosmetic abrasion on exterior cardboard. Zero internal shock detection.",
                    barcodeDetected = "SKU-LOG-84920 / LOT-2026-Q4-08",
                    complianceStatus = "PASSED",
                    analysisResult = "Verified by Gemini 3.1 Pro: Compliant with ISO 9001 logistics packaging standards. Cleared for automated racking.",
                    modelUsed = "gemini-3.1-pro-preview"
                )
            )

            // Seed inquiries
            inquiryDao.insertInquiry(
                InquiryEntity(
                    customerName = "Apex Micro Manufacturing",
                    email = "logistics@apexmicro.com",
                    category = "Logistics & Customs",
                    inquiryMessage = "Requesting customs clearance manifest HS 8517.62 for shipment ORD-2026-9015 for cross-border audit.",
                    aiSuggestedResponse = "Dear Apex Micro, Manifest documents for HS 8517.62 have been compiled and attached with certificate of origin.",
                    status = "Replied"
                )
            )
        }
    }

    suspend fun adjustInventory(
        sku: String,
        amountChange: Int,
        reason: String,
        personnelName: String,
        notes: String
    ) {
        val product = productDao.getProductBySku(sku) ?: return
        val currentStock = product.stockQuantity
        val newStock = (currentStock + amountChange).coerceAtLeast(0)
        productDao.updateStock(product.id, newStock)

        inventoryLogDao.insertLog(
            InventoryLogEntity(
                sku = sku,
                productName = product.name,
                changeAmount = amountChange,
                previousStock = currentStock,
                newStock = newStock,
                reason = reason,
                personnelName = personnelName,
                notes = notes
            )
        )
    }

    suspend fun saveVoiceMemo(
        title: String,
        durationSec: Int,
        transcribedText: String,
        category: String
    ): Long {
        return voiceMemoDao.insertMemo(
            VoiceMemoEntity(
                title = title,
                audioDurationSec = durationSec,
                transcribedText = transcribedText,
                modelUsed = "gemini-3.5-transcribe",
                category = category
            )
        )
    }

    suspend fun saveVideoInspection(
        title: String,
        videoFileName: String,
        summary: String,
        detectedIssues: String,
        barcode: String,
        status: String,
        fullAnalysis: String
    ): Long {
        return videoInspectionDao.insertInspection(
            VideoInspectionEntity(
                title = title,
                videoFileName = videoFileName,
                summary = summary,
                detectedIssues = detectedIssues,
                barcodeDetected = barcode,
                complianceStatus = status,
                analysisResult = fullAnalysis,
                modelUsed = "gemini-3.1-pro-preview"
            )
        )
    }

    suspend fun createOrder(
        customerName: String,
        totalAmount: Double,
        currency: String,
        itemsCount: Int
    ): Long {
        val randomSuffix = (1000..9999).random()
        val orderNum = "ORD-2026-$randomSuffix"
        val trk = "SF-${currency.take(2)}-${(100000..999999).random()}"
        return orderDao.insertOrder(
            OrderEntity(
                orderNumber = orderNum,
                customerName = customerName,
                totalAmount = totalAmount,
                currency = currency,
                status = "Processing",
                itemsCount = itemsCount,
                trackingNumber = trk
            )
        )
    }

    suspend fun submitInquiry(
        customerName: String,
        email: String,
        category: String,
        message: String
    ): Long {
        val aiReply = geminiApi.generateInquiryReply(customerName, category, message)
        return inquiryDao.insertInquiry(
            InquiryEntity(
                customerName = customerName,
                email = email,
                category = category,
                inquiryMessage = message,
                aiSuggestedResponse = aiReply,
                status = "Replied"
            )
        )
    }

    suspend fun updateOrderStatus(id: Long, newStatus: String) {
        orderDao.updateOrderStatus(id, newStatus)
    }

    suspend fun deleteVoiceMemo(id: Long) {
        voiceMemoDao.deleteMemo(id)
    }
}
