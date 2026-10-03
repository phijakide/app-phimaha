package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SupplyFlowViewModel
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.TertiaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreativeStudioScreen(
    viewModel: SupplyFlowViewModel,
    modifier: Modifier = Modifier
) {
    val studioState by viewModel.studioState.collectAsStateWithLifecycle()

    var selectedStudioMode by remember { mutableStateOf(0) } // 0: Lyria Music, 1: Gemini Image, 2: Veo Video

    // Music inputs
    var musicPrompt by remember { mutableStateOf("Upbeat modern industrial electronic track with ambient warehouse pulses and rhythmic cyber synths") }
    var isFullLengthTrack by remember { mutableStateOf(false) }

    // Image inputs
    var imagePrompt by remember { mutableStateOf("Futuristic automated forklift picking high-bay warehouse pallet under neon cyan warehouse lighting, photorealistic 3D render") }
    var selectedImageRatio by remember { mutableStateOf("1:1") }

    // Veo Video inputs
    var veoPrompt by remember { mutableStateOf("Cinematic drone fly-through over an automated fulfillment center with high-speed robotic conveyor sorting packages into delivery trucks") }
    var selectedVeoRatio by remember { mutableStateOf("16:9") } // "16:9" or "9:16"
    var uploadedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uploadedImageUri = uri
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("creative_studio_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Switcher Tabs
        item {
            TabRow(selectedTabIndex = selectedStudioMode) {
                Tab(
                    selected = selectedStudioMode == 0,
                    onClick = { selectedStudioMode = 0 },
                    text = { Text("Music (Lyria)") },
                    icon = { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedStudioMode == 1,
                    onClick = { selectedStudioMode = 1 },
                    text = { Text("Image (3.1 Flash)") },
                    icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedStudioMode == 2,
                    onClick = { selectedStudioMode = 2 },
                    text = { Text("Video (Veo 3)") },
                    icon = { Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        when (selectedStudioMode) {
            0 -> {
                // LYRIA MUSIC GENERATION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = if (isFullLengthTrack) "lyria-3-pro-preview" else "lyria-3-clip-preview",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = if (isFullLengthTrack) "Full Track" else "Clip (up to 30s)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lyria AI Music Generation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Synthesize warehouse ambient focus tracks, dispatch notification chimes, and operational soundscapes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Track Length Option",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = !isFullLengthTrack,
                                    onClick = { isFullLengthTrack = false },
                                    label = { Text("Short Clip (lyria-3-clip-preview)") }
                                )
                                FilterChip(
                                    selected = isFullLengthTrack,
                                    onClick = { isFullLengthTrack = true },
                                    label = { Text("Full Track (lyria-3-pro-preview)") }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = musicPrompt,
                                onValueChange = { musicPrompt = it },
                                label = { Text("Music Generation Prompt") },
                                minLines = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("music_prompt_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.generateMusic(musicPrompt, isFullLengthTrack) },
                                enabled = !studioState.isGenerating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_music_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (studioState.isGenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Synthesizing Music...")
                                } else {
                                    Icon(Icons.Default.MusicNote, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Music with Lyria")
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // GEMINI 3.1 FLASH IMAGE PREVIEW (CREATE & EDIT IMAGES)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SecondaryLight
                            ) {
                                Text(
                                    text = "gemini-3.1-flash-image-preview",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create & Edit Images with AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Generate new product marketing imagery, edit existing packaging designs, and render warehouse concepts.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Aspect Ratio",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("1:1", "16:9", "4:3", "9:16").forEach { ratio ->
                                    FilterChip(
                                        selected = selectedImageRatio == ratio,
                                        onClick = { selectedImageRatio = ratio },
                                        label = { Text(ratio) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = imagePrompt,
                                onValueChange = { imagePrompt = it },
                                label = { Text("Image Creation / Editing Prompt") },
                                minLines = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("image_prompt_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.createOrEditImage(imagePrompt, aspectRatio = selectedImageRatio) },
                                enabled = !studioState.isGenerating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_image_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryLight)
                            ) {
                                if (studioState.isGenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating Image...")
                                } else {
                                    Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Image with Gemini 3.1 Flash")
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // VEO 3 VIDEO GENERATION (TEXT-TO-VIDEO & ANIMATE IMAGE INTO VIDEO)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TertiaryLight
                            ) {
                                Text(
                                    text = "veo-3.1-fast-generate-preview",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Veo 3 Video Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Generate videos from text prompts or animate uploaded photos. Aspect ratio: 16:9 (landscape) or 9:16 (portrait).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Veo Aspect Ratio (Mandatory 16:9 or 9:16)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = selectedVeoRatio == "16:9",
                                    onClick = { selectedVeoRatio = "16:9" },
                                    label = { Text("16:9 (Landscape)") }
                                )
                                FilterChip(
                                    selected = selectedVeoRatio == "9:16",
                                    onClick = { selectedVeoRatio = "9:16" },
                                    label = { Text("9:16 (Portrait)") }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (uploadedImageUri != null) "Photo Selected (Animate Image to Video)" else "Upload Photo to Animate into Video (Optional)")
                            }

                            if (uploadedImageUri != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ready to animate: $uploadedImageUri",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = veoPrompt,
                                onValueChange = { veoPrompt = it },
                                label = { Text("Veo Video Scene Description") },
                                minLines = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("veo_prompt_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.generateVeoVideo(
                                        prompt = veoPrompt,
                                        base64InputImage = if (uploadedImageUri != null) "BASE64_PLACEHOLDER" else null,
                                        aspectRatio = selectedVeoRatio
                                    )
                                },
                                enabled = !studioState.isGenerating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_veo_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TertiaryLight)
                            ) {
                                if (studioState.isGenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating Veo Video...")
                                } else {
                                    Icon(Icons.Default.MovieFilter, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (uploadedImageUri != null) "Animate Image into Video (Veo 3)" else "Generate Video from Text (Veo 3)")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Output Media Card
        if (studioState.lastResult != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("studio_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Generated Asset Output",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = studioState.lastResult?.modelUsed ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                        Text(
                            text = studioState.lastResult?.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
