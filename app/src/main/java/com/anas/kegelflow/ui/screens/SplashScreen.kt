package com.anas.kegelflow.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.ui.theme.PrimaryTeal
import com.anas.kegelflow.ui.theme.SecondaryMint
import com.anas.kegelflow.util.LocalizationHelper
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    language: AppLanguage,
    onSplashFinished: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    var startAnimation by remember { mutableStateOf(false) }

    // Smooth Entrance Animations
    val scaleAnimate by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.75f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splashScale"
    )

    val alphaAnimate by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseOutCubic),
        label = "splashAlpha"
    )

    // Infinite Breathing Halo Rings Animation
    val infiniteTransition = rememberInfiniteTransition(label = "haloTransition")
    
    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1Scale"
    )

    val ring1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1Alpha"
    )

    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2Scale"
    )

    val ring2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2Alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onSplashFinished()
    }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF133E37),
                            Color(0xFF101C1A),
                            Color(0xFF091210)
                        )
                    } else {
                        listOf(
                            Color(0xFFE6F7F3),
                            Color(0xFFF2FAF7),
                            Color(0xFFF7FAF9)
                        )
                    },
                    radius = 1200f
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleAnimate)
                .alpha(alphaAnimate)
                .padding(horizontal = 32.dp)
        ) {
            // Animated Glowing Logo Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                // Outer Pulse Ring 2
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(ring2Scale)
                        .alpha(ring2Alpha)
                        .clip(CircleShape)
                        .background(PrimaryTeal)
                )

                // Outer Pulse Ring 1
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(ring1Scale)
                        .alpha(ring1Alpha)
                        .clip(CircleShape)
                        .background(PrimaryTeal)
                )

                // Core Main Glowing Badge
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryTeal, SecondaryMint)
                            )
                        )
                        .border(
                            width = 3.dp,
                            color = Color.White.copy(alpha = if (isDark) 0.25f else 0.4f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = "Kegel Flow Logo",
                        tint = Color.White,
                        modifier = Modifier.size(58.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Title in Tajawal Bold
            Text(
                text = LocalizationHelper.getString("app_title", language),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) PrimaryTeal.copy(alpha = 0.22f) else PrimaryTeal.copy(alpha = 0.10f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = PrimaryTeal.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "تطوير العضلات وإعادة التوازن" else "Strengthen & Rebalance",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF6EE7B7) else PrimaryTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(52.dp))

            // Sleek Loading Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = PrimaryTeal,
                strokeWidth = 2.5.dp
            )
        }

        // Bottom Brand Signature
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(alphaAnimate)
        ) {
            Text(
                text = if (isAr) "كيجل فلو • رفيقك الصحي اليومي" else "Kegel Flow • Daily Health Companion",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
            )
        }
    }
}
