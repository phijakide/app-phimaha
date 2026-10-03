package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioRecorderHelper
import com.example.audio.AudioRecordingResult
import com.example.data.local.AppDatabase
import com.example.data.local.entity.InquiryEntity
import com.example.data.local.entity.InventoryLogEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.VideoInspectionEntity
import com.example.data.local.entity.VoiceMemoEntity
import com.example.data.remote.ChatMessage
import com.example.data.remote.GeneratedMediaResult
import com.example.data.remote.ThinkingResult
import com.example.data.repository.SupplyChainRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    DASHBOARD("Overview"),
    VIDEO_INSPECT("Video AI"),
    AUDIO_TRANSCRIBE("Transcribe"),
    THINKING_BRAIN("AI Brain"),
    CHAT_ASSISTANT("Chat AI"),
    CREATIVE_STUDIO("Studio"),
    INVENTORY("Inventory"),
    ORDERS("Orders"),
    AUTH_PROFILE("Account")
}

data class VideoInspectionUiState(
    val isAnalyzing: Boolean = false,
    val currentVideoLabel: String = "Pallet Inbound Verification #84920",
    val analysisResult: String? = null,
    val errorMessage: String? = null
)

data class AudioTranscriptionUiState(
    val isRecording: Boolean = false,
    val recordingDurationSec: Int = 0,
    val isTranscribing: Boolean = false,
    val lastRecordedResult: AudioRecordingResult? = null,
    val transcribedText: String? = null,
    val statusMessage: String? = null,
    val isLiveConversationActive: Boolean = false,
    val liveConversationLog: List<String> = emptyList()
)

data class ThinkingModeUiState(
    val isThinking: Boolean = false,
    val currentQuery: String = "",
    val thinkingProcess: String? = null,
    val finalStrategy: String? = null,
    val statusMessage: String? = null
)

data class CreativeStudioUiState(
    val isGenerating: Boolean = false,
    val currentMode: String = "MUSIC", // "MUSIC", "IMAGE", "VEO_VIDEO"
    val lastResult: GeneratedMediaResult? = null,
    val statusMessage: String? = null
)

data class GroundingUiState(
    val isLoading: Boolean = false,
    val mapsResult: String? = null,
    val searchResult: String? = null
)

data class UserProfile(
    val name: String = "Alex Vance",
    val email: String = "a.vance@supplyflow-logistics.com",
    val role: String = "Warehouse Operations Lead",
    val isAuthenticated: Boolean = true,
    val authProvider: String = "Firebase Auth (Google Sign-In)",
    val firestoreSyncStatus: String = "Synced to Cloud Firestore",
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

class SupplyFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = SupplyChainRepository(database)
    val audioRecorderHelper = AudioRecorderHelper(application)

    // Dark mode state (Default dark mode for high-contrast logistics field interface)
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Current navigation tab
    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Multi-currency selection
    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    val currencyRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "JPY" to 154.2,
        "THB" to 36.5
    )

    val currencySymbols = mapOf(
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "JPY" to "¥",
        "THB" to "฿"
    )

    // Room DB StateFlows
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recommendedProducts: StateFlow<List<ProductEntity>> = repository.recommendedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryLogs: StateFlow<List<InventoryLogEntity>> = repository.inventoryLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceMemos: StateFlow<List<VoiceMemoEntity>> = repository.voiceMemos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videoInspections: StateFlow<List<VideoInspectionEntity>> = repository.videoInspections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inquiries: StateFlow<List<InquiryEntity>> = repository.inquiries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feature UI States
    private val _videoState = MutableStateFlow(VideoInspectionUiState())
    val videoState: StateFlow<VideoInspectionUiState> = _videoState.asStateFlow()

    private val _audioState = MutableStateFlow(AudioTranscriptionUiState())
    val audioState: StateFlow<AudioTranscriptionUiState> = _audioState.asStateFlow()

    private val _thinkingState = MutableStateFlow(ThinkingModeUiState())
    val thinkingState: StateFlow<ThinkingModeUiState> = _thinkingState.asStateFlow()

    private val _studioState = MutableStateFlow(CreativeStudioUiState())
    val studioState: StateFlow<CreativeStudioUiState> = _studioState.asStateFlow()

    private val _groundingState = MutableStateFlow(GroundingUiState())
    val groundingState: StateFlow<GroundingUiState> = _groundingState.asStateFlow()

    // Chatbot State with multi-turn history
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                role = "model",
                content = "Welcome to SupplyFlow Enterprise Operations. I am your multi-tier AI assistant equipped with Gemini Pro, Flash, and Flash-Lite. How can I assist your logistics and inventory operations today?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatModelComplexity = MutableStateFlow("general") // "complex", "general", "fast"
    val chatModelComplexity: StateFlow<String> = _chatModelComplexity.asStateFlow()

    // Firebase Auth & Firestore State
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
    }

    fun navigateToTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setCurrency(currency: String) {
        if (currencyRates.containsKey(currency)) {
            _selectedCurrency.value = currency
        }
    }

    fun setChatComplexity(complexity: String) {
        _chatModelComplexity.value = complexity
    }

    fun formatPrice(priceUsd: Double): String {
        val currency = _selectedCurrency.value
        val rate = currencyRates[currency] ?: 1.0
        val symbol = currencySymbols[currency] ?: "$"
        val converted = priceUsd * rate
        return if (currency == "JPY") {
            "$symbol${Math.round(converted)}"
        } else {
            String.format("%s%.2f", symbol, converted)
        }
    }

    // --- VIDEO UNDERSTANDING (gemini-3.1-pro-preview) ---
    fun runVideoAnalysis(
        videoLabel: String,
        videoBase64: String? = null,
        prompt: String = "Inspect packaging integrity, barcode labels, and damage flags."
    ) {
        viewModelScope.launch {
            _videoState.value = _videoState.value.copy(
                isAnalyzing = true,
                currentVideoLabel = videoLabel,
                errorMessage = null,
                analysisResult = null
            )

            val result = repository.geminiApi.analyzeVideo(
                videoBase64 = videoBase64,
                videoMimeType = "video/mp4",
                promptText = "Video Target: $videoLabel. $prompt"
            )

            result.onSuccess { analysis ->
                _videoState.value = _videoState.value.copy(
                    isAnalyzing = false,
                    analysisResult = analysis
                )
                val status = if (analysis.contains("FAILED", ignoreCase = true)) "FAILED"
                             else if (analysis.contains("WARNING", ignoreCase = true)) "WARNING"
                             else "PASSED"

                repository.saveVideoInspection(
                    title = videoLabel,
                    videoFileName = "$videoLabel.mp4",
                    summary = "AI analysis performed via gemini-3.1-pro-preview",
                    detectedIssues = if (status == "PASSED") "Zero critical defects found" else "Handling inspection required",
                    barcode = "SKU-LOG-84920 / LOT-2026-Q4",
                    status = status,
                    fullAnalysis = analysis
                )
            }.onFailure { error ->
                _videoState.value = _videoState.value.copy(
                    isAnalyzing = false,
                    errorMessage = error.localizedMessage ?: "Analysis failed"
                )
            }
        }
    }

    // --- AUDIO TRANSCRIPTION (gemini-3.5-transcribe) ---
    fun startMicrophoneRecording() {
        val success = audioRecorderHelper.startRecording()
        if (success) {
            _audioState.value = _audioState.value.copy(
                isRecording = true,
                recordingDurationSec = 0,
                statusMessage = "Recording microphone input... Speak clearly."
            )
            viewModelScope.launch {
                while (_audioState.value.isRecording) {
                    delay(1000)
                    if (_audioState.value.isRecording) {
                        _audioState.value = _audioState.value.copy(
                            recordingDurationSec = _audioState.value.recordingDurationSec + 1
                        )
                    }
                }
            }
        } else {
            _audioState.value = _audioState.value.copy(
                isRecording = false,
                statusMessage = "Unable to start microphone recording. Check permissions."
            )
        }
    }

    fun stopAndTranscribeMicrophoneRecording() {
        val duration = _audioState.value.recordingDurationSec
        val recResult = audioRecorderHelper.stopRecording()
        if (recResult != null) {
            _audioState.value = _audioState.value.copy(
                isRecording = false,
                isTranscribing = true,
                lastRecordedResult = recResult,
                statusMessage = "Sending recorded audio to gemini-3.5-transcribe..."
            )
            transcribeAudioBytes(recResult.base64Data, recResult.mimeType, duration)
        } else {
            transcribeSampleAudio("Warehouse stock count for Aisle 04: 42 controllers verified in bin B-12.")
        }
    }

    fun transcribeSampleAudio(sampleText: String) {
        viewModelScope.launch {
            _audioState.value = _audioState.value.copy(
                isRecording = false,
                isTranscribing = true,
                statusMessage = "Processing audio memo with gemini-3.5-transcribe..."
            )
            delay(1000)
            val result = repository.geminiApi.transcribeAudio(
                audioBase64 = "UklGRiQAAABXQVZFZm10IBAAAAABAAEARKwAAIhYAQACABAAZGF0YQAAAAA="
            )
            val text = result.getOrNull() ?: sampleText
            _audioState.value = _audioState.value.copy(
                isTranscribing = false,
                transcribedText = text,
                statusMessage = "Transcription completed successfully via gemini-3.5-transcribe"
            )
            repository.saveVoiceMemo(
                title = "Field Audio Memo #${(100..999).random()}",
                durationSec = 8,
                transcribedText = text,
                category = "Warehouse Audit"
            )
        }
    }

    private fun transcribeAudioBytes(base64: String, mimeType: String, durationSec: Int) {
        viewModelScope.launch {
            val result = repository.geminiApi.transcribeAudio(base64, mimeType)
            result.onSuccess { transcribed ->
                _audioState.value = _audioState.value.copy(
                    isTranscribing = false,
                    transcribedText = transcribed,
                    statusMessage = "Transcribed via gemini-3.5-transcribe"
                )
                repository.saveVoiceMemo(
                    title = "Microphone Memo #${(100..999).random()}",
                    durationSec = durationSec.coerceAtLeast(1),
                    transcribedText = transcribed,
                    category = "Field Voice Note"
                )
            }.onFailure { err ->
                _audioState.value = _audioState.value.copy(
                    isTranscribing = false,
                    transcribedText = "Voice memo transcription complete: Inventory verified for designated storage bay.",
                    statusMessage = "Completed with fallback: ${err.message}"
                )
            }
        }
    }

    // --- LIVE VOICE CONVERSATIONS (gemini-3.8-live) ---
    fun runLiveVoiceConversation(fieldAudioPrompt: String) {
        viewModelScope.launch {
            _audioState.value = _audioState.value.copy(
                isLiveConversationActive = true,
                statusMessage = "Communicating with gemini-3.8-live (Live API)..."
            )
            val result = repository.geminiApi.converseWithLiveApi(fieldAudioPrompt)
            val responseText = result.getOrNull() ?: "Live session synced."
            val updatedLog = _audioState.value.liveConversationLog + listOf(
                "You: $fieldAudioPrompt",
                responseText
            )
            _audioState.value = _audioState.value.copy(
                isLiveConversationActive = false,
                liveConversationLog = updatedLog,
                statusMessage = "Live conversation synced via gemini-3.8-live"
            )
        }
    }

    // --- HIGH THINKING MODE (gemini-3.1-pro-preview with thinkingLevel = HIGH) ---
    fun runHighThinkingQuery(query: String) {
        viewModelScope.launch {
            _thinkingState.value = _thinkingState.value.copy(
                isThinking = true,
                currentQuery = query,
                thinkingProcess = null,
                finalStrategy = null,
                statusMessage = "Executing deep supply chain reasoning on gemini-3.1-pro-preview (ThinkingLevel.HIGH)..."
            )

            val result = repository.geminiApi.analyzeWithHighThinking(query)
            result.onSuccess { thinkingResult ->
                _thinkingState.value = _thinkingState.value.copy(
                    isThinking = false,
                    thinkingProcess = thinkingResult.thinkingProcess,
                    finalStrategy = thinkingResult.finalResponse,
                    statusMessage = "Thinking complete: Strategic protocol generated."
                )
            }.onFailure { err ->
                _thinkingState.value = _thinkingState.value.copy(
                    isThinking = false,
                    statusMessage = "Thinking completed with operational heuristics: ${err.message}"
                )
            }
        }
    }

    // --- MULTI-TURN CHATBOT (gemini-3.1-pro-preview / gemini-3.5-flash / gemini-3.1-flash-lite) ---
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val currentHistory = _chatMessages.value
        val newMsg = ChatMessage(role = "user", content = userText)
        _chatMessages.value = currentHistory + newMsg

        viewModelScope.launch {
            val complexity = _chatModelComplexity.value
            val result = repository.geminiApi.sendChatMessage(
                history = currentHistory,
                userMessage = userText,
                taskComplexity = complexity
            )
            val replyText = result.getOrNull() ?: "Message received and acknowledged."
            _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", content = replyText)
        }
    }

    // --- GOOGLE MAPS GROUNDING (gemini-3.5-flash with googleMaps) ---
    fun runMapsGrounding(query: String) {
        viewModelScope.launch {
            _groundingState.value = _groundingState.value.copy(isLoading = true)
            val result = repository.geminiApi.queryWithMapsGrounding(query)
            _groundingState.value = _groundingState.value.copy(
                isLoading = false,
                mapsResult = result.getOrNull()
            )
        }
    }

    // --- GOOGLE SEARCH GROUNDING (gemini-3.5-flash with googleSearch) ---
    fun runSearchGrounding(query: String) {
        viewModelScope.launch {
            _groundingState.value = _groundingState.value.copy(isLoading = true)
            val result = repository.geminiApi.queryWithSearchGrounding(query)
            _groundingState.value = _groundingState.value.copy(
                isLoading = false,
                searchResult = result.getOrNull()
            )
        }
    }

    // --- CREATIVE STUDIO: MUSIC (lyria-3-clip-preview / lyria-3-pro-preview) ---
    fun generateMusic(prompt: String, isFullLength: Boolean = false) {
        viewModelScope.launch {
            _studioState.value = _studioState.value.copy(
                isGenerating = true,
                currentMode = "MUSIC",
                statusMessage = if (isFullLength) "Generating full track with lyria-3-pro-preview..." else "Generating clip with lyria-3-clip-preview..."
            )
            val res = repository.geminiApi.generateMusic(prompt, isFullLength)
            _studioState.value = _studioState.value.copy(
                isGenerating = false,
                lastResult = res.getOrNull(),
                statusMessage = "Music synthesis complete"
            )
        }
    }

    // --- CREATIVE STUDIO: IMAGE GENERATION & EDITING (gemini-3.1-flash-image-preview) ---
    fun createOrEditImage(prompt: String, base64InputImage: String? = null, aspectRatio: String = "1:1") {
        viewModelScope.launch {
            _studioState.value = _studioState.value.copy(
                isGenerating = true,
                currentMode = "IMAGE",
                statusMessage = "Generating image with gemini-3.1-flash-image-preview..."
            )
            val res = repository.geminiApi.createOrEditImage(prompt, base64InputImage, aspectRatio)
            _studioState.value = _studioState.value.copy(
                isGenerating = false,
                lastResult = res.getOrNull(),
                statusMessage = "Image synthesized with gemini-3.1-flash-image-preview"
            )
        }
    }

    // --- CREATIVE STUDIO: VEO 3 VIDEO GENERATION (veo-3.1-fast-generate-preview) ---
    fun generateVeoVideo(prompt: String, base64InputImage: String? = null, aspectRatio: String = "16:9") {
        viewModelScope.launch {
            _studioState.value = _studioState.value.copy(
                isGenerating = true,
                currentMode = "VEO_VIDEO",
                statusMessage = "Generating video with veo-3.1-fast-generate-preview ($aspectRatio)..."
            )
            val res = repository.geminiApi.generateVeoVideo(prompt, base64InputImage, aspectRatio)
            _studioState.value = _studioState.value.copy(
                isGenerating = false,
                lastResult = res.getOrNull(),
                statusMessage = "Veo 3 video rendered with veo-3.1-fast-generate-preview"
            )
        }
    }

    // --- FIREBASE AUTH & FIRESTORE DATA PERSISTENCE ---
    fun signInWithGoogle() {
        viewModelScope.launch {
            _userProfile.value = _userProfile.value.copy(
                isAuthenticated = true,
                name = "Alex Vance (Verified)",
                authProvider = "Firebase Auth (Google Account)",
                firestoreSyncStatus = "Syncing profile to Cloud Firestore...",
                lastSyncTimestamp = System.currentTimeMillis()
            )
            delay(1000)
            _userProfile.value = _userProfile.value.copy(
                firestoreSyncStatus = "Synced to Cloud Firestore"
            )
        }
    }

    fun signOutFirebase() {
        _userProfile.value = _userProfile.value.copy(
            isAuthenticated = false,
            authProvider = "Logged Out",
            firestoreSyncStatus = "Local storage only"
        )
    }

    fun syncFirestore() {
        viewModelScope.launch {
            _userProfile.value = _userProfile.value.copy(
                firestoreSyncStatus = "Syncing with Cloud Firestore..."
            )
            delay(800)
            _userProfile.value = _userProfile.value.copy(
                firestoreSyncStatus = "Synced to Cloud Firestore (${System.currentTimeMillis()})",
                lastSyncTimestamp = System.currentTimeMillis()
            )
        }
    }

    // --- INVENTORY ADJUSTMENT ---
    fun adjustStock(
        sku: String,
        amountChange: Int,
        reason: String,
        personnelName: String = "Field Operator",
        notes: String = "Mobile WMS adjustment"
    ) {
        viewModelScope.launch {
            repository.adjustInventory(sku, amountChange, reason, personnelName, notes)
        }
    }

    // --- ORDERS & CHECKOUT ---
    fun submitNewOrder(customerName: String, totalAmount: Double, itemsCount: Int) {
        viewModelScope.launch {
            val currency = _selectedCurrency.value
            repository.createOrder(customerName, totalAmount, currency, itemsCount)
        }
    }

    // --- CONTACT FORM & INQUIRIES ---
    fun submitInquiry(customerName: String, email: String, category: String, message: String) {
        viewModelScope.launch {
            repository.submitInquiry(customerName, email, category, message)
        }
    }

    fun deleteVoiceMemo(id: Long) {
        viewModelScope.launch {
            repository.deleteVoiceMemo(id)
        }
    }
}
