package com.example.fxinsight.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0B1E), Color(0xFF1A1A3A), Color(0xFF2D1B4D))
                )
            )
    ) {
        // Background Glow
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .size(300.dp)
                .blur(100.dp)
                .background(Color(0xFF4A90E2).copy(alpha = 0.1f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Light) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HelpSection(
                    title = "About FX Insight",
                    content = "FX Insight is a full-stack mobile project built to demonstrate real-time currency conversion, market trend analysis, alert workflows, and AI-generated market summaries. It showcases a Kotlin Android frontend connected to a deployed backend with live data, persistent user features, and cloud-based services."
                )

                HelpSection(
                    title = "Project Highlights",
                    content = "• Real-time currency conversion with live exchange rates.\n• Favorites system for quickly switching between saved currency pairs.\n• Market screen with historical charting, weekly statistics, and AI-generated insights.\n• Conversion history tracking.\n• Price alert workflow with triggered alert states.\n• Cloud deployment, backend API integration, and authenticated user flows."
                )

                HelpSection(
                    title = "Core Screens",
                    content = "• Dashboard: Convert currencies, swap pairs, manage favorites, and view active or triggered alerts.\n• Market: Explore trends, chart data, weekly statistics, and AI insight summaries.\n• History: Review previously completed conversions.\n• Profile: Access account-related settings and project information."
                )

                HelpSection(
                    title = "How the Alert Flow Works",
                    content = "1. Open the alert feature from the dashboard.\n2. Choose a currency pair.\n3. Select whether the alert should trigger when the rate moves above or below a target value.\n4. Save the alert.\n5. When the condition is met, the dashboard surfaces the triggered result in the alert section."
                )

                HelpSection(
                    title = "Technical Notes",
                    content = "This project focuses on practical mobile engineering and backend integration. It includes state management, API communication, cloud deployment, authenticated features, live numeric formatting, and UI handling for real-world cases such as precision issues, route handling, and input synchronization."
                )

                HelpSection(
                    title = "Known Scope of the Project",
                    content = "FX Insight is a showcase project created for portfolio and freelancing purposes. It is not intended to provide financial advice or production-grade trading functionality. Market values and AI insights are presented for demonstration of system design, UX, and engineering workflow."
                )

                HelpSection(
                    title = "Support / Project Context",
                    content = "This app is a developer portfolio project built to demonstrate full-stack Android application development. It highlights Kotlin UI development, backend API design, deployment, cloud configuration, and feature integration in a polished mobile experience."
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun HelpSection(title: String, content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF00FFC2),
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 22.sp
                )
            )
        }
    }
}
