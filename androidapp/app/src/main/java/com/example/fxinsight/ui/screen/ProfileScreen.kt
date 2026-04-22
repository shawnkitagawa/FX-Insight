package com.example.fxinsight.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fxinsight.ui.theme.FXInsightTheme
import com.example.fxinsight.ui.uistate.ProfileState
import com.example.fxinsight.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    signOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        HelpSupportScreen(onBack = { showHelp = false })
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0B0B1E), Color(0xFF1A1A3A), Color(0xFF2D1B4D))
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-50).dp)
                    .size(400.dp)
                    .blur(120.dp)
                    .background(Color(0xFF4A90E2).copy(alpha = 0.1f), CircleShape)
            )

            Crossfade(targetState = uiState.profileState, label = "ProfileState") { state ->
                when (state) {
                    is ProfileState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF00FFC2))
                        }
                    }
                    is ProfileState.Error -> {
                        ErrorStateView(
                            message = state.message,
                            onRetry = { viewModel.fetchProfile() }
                        )
                    }
                    else -> {
                        ProfileContent(
                            userName = uiState.userName,
                            avatarUrl = uiState.avatarUrl,
                            signOut = signOut,
                            onHelpClick = { showHelp = true },
                            onPhotoChange = { /* Photo upload logic can be added here */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    userName: String,
    avatarUrl: String?,
    signOut: () -> Unit,
    onHelpClick: () -> Unit,
    onPhotoChange: (android.net.Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onPhotoChange(it) }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl != null) {
                    AsyncImage(model = avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(60.dp))
                }
            }
            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF00FFC2))
            ) {
                Icon(Icons.Outlined.PhotoCamera, null, tint = Color(0xFF0B0B1E), modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = userName.ifEmpty { "User" }, style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.Light, letterSpacing = 1.sp))
        Spacer(modifier = Modifier.height(48.dp))

        // Cleaned up menu - only Help & Support
        ProfileMenuItem(Icons.Default.HelpOutline, "Help & Support", onClick = onHelpClick)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = signOut,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp).height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B).copy(alpha = 0.1f), contentColor = Color(0xFFFF4B4B)),
            border = BorderStroke(0.5.dp, Color(0xFFFF4B4B).copy(alpha = 0.2f))
        ) {
            Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light))
        }
    }
}

@Composable
fun ErrorStateView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color.Red.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Retry", color = Color.White)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.9f)),
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
