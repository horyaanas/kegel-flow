package com.anas.kegelflow.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.ui.theme.AccentPeach
import com.anas.kegelflow.ui.theme.PrimaryTeal
import com.anas.kegelflow.ui.theme.RelaxCyan
import com.anas.kegelflow.ui.theme.SecondaryMint
import com.anas.kegelflow.util.LocalizationHelper

@Composable
fun GuideModal(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Steps, 1: Anatomy, 2: Mistakes

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 16.dp)
                .testTag("guide_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = PrimaryTeal,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = LocalizationHelper.getString("guide_title", language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .testTag("close_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Segmented Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf(
                        LocalizationHelper.getString("guide_tab_anatomy", language),
                        LocalizationHelper.getString("guide_tab_steps", language),
                        LocalizationHelper.getString("guide_tab_mistakes", language)
                    )

                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PrimaryTeal else Color.Transparent,
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            TextButton(
                                onClick = { selectedTab = index },
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        0 -> AnatomySection(language = language)
                        1 -> StepsSection(language = language)
                        2 -> MistakesSection(language = language)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Medical Disclaimer Box
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = PrimaryTeal.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryTeal,
                                modifier = Modifier.size(20.dp).padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = LocalizationHelper.getString("disclaimer_notice", language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("understand_guide_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text(
                        text = if (isAr) "فهمت الدليل وأريد التمرين" else "Got It, Let's Workout",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun AnatomySection(language: AppLanguage) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = LocalizationHelper.getString("anatomy_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = LocalizationHelper.getString("anatomy_sub", language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Anatomical Pelvic Floor Diagram
            PelvicFloorIllustration(modifier = Modifier.fillMaxWidth().height(180.dp), language = language)

            Spacer(modifier = Modifier.height(16.dp))

            // Practical Tip
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PrimaryTeal.copy(alpha = 0.10f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = LocalizationHelper.getString("anatomy_tip", language),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryTeal,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun StepsSection(language: AppLanguage) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideStepCard(
            stepNumber = 1,
            title = LocalizationHelper.getString("guide_step1_title", language),
            desc = LocalizationHelper.getString("guide_step1_desc", language),
            icon = Icons.Default.Search,
            iconColor = PrimaryTeal
        )
        GuideStepCard(
            stepNumber = 2,
            title = LocalizationHelper.getString("guide_step2_title", language),
            desc = LocalizationHelper.getString("guide_step2_desc", language),
            icon = Icons.Default.ArrowUpward,
            iconColor = PrimaryTeal
        )
        GuideStepCard(
            stepNumber = 3,
            title = LocalizationHelper.getString("guide_step3_title", language),
            desc = LocalizationHelper.getString("guide_step3_desc", language),
            icon = Icons.Default.Air,
            iconColor = RelaxCyan
        )
        GuideStepCard(
            stepNumber = 4,
            title = LocalizationHelper.getString("guide_step4_title", language),
            desc = LocalizationHelper.getString("guide_step4_desc", language),
            icon = Icons.Default.Spa,
            iconColor = SecondaryMint
        )
    }
}

@Composable
private fun MistakesSection(language: AppLanguage) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MistakeCard(
            title = LocalizationHelper.getString("mistake1_title", language),
            desc = LocalizationHelper.getString("mistake1_desc", language)
        )
        MistakeCard(
            title = LocalizationHelper.getString("mistake2_title", language),
            desc = LocalizationHelper.getString("mistake2_desc", language)
        )
        MistakeCard(
            title = LocalizationHelper.getString("mistake3_title", language),
            desc = LocalizationHelper.getString("mistake3_desc", language)
        )
    }
}

@Composable
private fun GuideStepCard(
    stepNumber: Int,
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun MistakeCard(
    title: String,
    desc: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(22.dp).padding(top = 1.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Custom Canvas-based anatomical illustration of the pelvic floor cradle muscles, bladder, and upward pull direction.
 */
@Composable
fun PelvicFloorIllustration(
    modifier: Modifier = Modifier,
    language: AppLanguage
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pelvicPulse")
    val pulseY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseY"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = PrimaryTeal.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Pelvic Bone Arch (Outer support wings)
            val bonePath = Path().apply {
                moveTo(w * 0.15f, h * 0.45f)
                cubicTo(w * 0.25f, h * 0.18f, w * 0.75f, h * 0.18f, w * 0.85f, h * 0.45f)
                cubicTo(w * 0.90f, h * 0.75f, w * 0.80f, h * 0.88f, w * 0.65f, h * 0.88f)
                cubicTo(w * 0.50f, h * 0.92f, w * 0.50f, h * 0.92f, w * 0.35f, h * 0.88f)
                cubicTo(w * 0.20f, h * 0.88f, w * 0.10f, h * 0.75f, w * 0.15f, h * 0.45f)
                close()
            }
            drawPath(
                path = bonePath,
                color = Color(0xFF64748B).copy(alpha = 0.18f),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // 2. Bladder / Organ Dome (Center Upper)
            val bladderCenter = Offset(w * 0.5f, h * 0.42f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFBAE6FD), Color(0xFF38BDF8).copy(alpha = 0.35f)),
                    center = bladderCenter,
                    radius = w * 0.18f
                ),
                radius = w * 0.17f,
                center = bladderCenter
            )
            drawCircle(
                color = Color(0xFF0284C7).copy(alpha = 0.6f),
                radius = w * 0.17f,
                center = bladderCenter,
                style = Stroke(width = 3.5f)
            )

            // 3. Pelvic Floor Muscles (Hammock Sling Cradle under bladder)
            val musclePath = Path().apply {
                moveTo(w * 0.22f, h * 0.58f)
                cubicTo(
                    w * 0.35f, h * 0.84f + pulseY,
                    w * 0.65f, h * 0.84f + pulseY,
                    w * 0.78f, h * 0.58f
                )
                cubicTo(
                    w * 0.65f, h * 0.72f + pulseY,
                    w * 0.35f, h * 0.72f + pulseY,
                    w * 0.22f, h * 0.58f
                )
                close()
            }

            // Glow around active muscle
            drawPath(
                path = musclePath,
                color = PrimaryTeal.copy(alpha = glowAlpha * 0.45f)
            )
            drawPath(
                path = musclePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryTeal, SecondaryMint, PrimaryTeal)
                ),
                style = Stroke(width = 9f, cap = StrokeCap.Round)
            )

            // 4. Upward Pull Arrow Indicator (Contraction motion)
            val arrowX = w * 0.5f
            val arrowTopY = h * 0.58f + pulseY
            val arrowBottomY = h * 0.74f + pulseY
            drawLine(
                color = Color(0xFFF59E0B),
                start = Offset(arrowX, arrowBottomY),
                end = Offset(arrowX, arrowTopY),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            val arrowHead = Path().apply {
                moveTo(arrowX - 10f, arrowTopY + 12f)
                lineTo(arrowX, arrowTopY)
                lineTo(arrowX + 10f, arrowTopY + 12f)
            }
            drawPath(
                path = arrowHead,
                color = Color(0xFFF59E0B),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
        }

        // Overlay Text Badges
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = LocalizationHelper.getString("anatomy_label_bladder", language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF64748B).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = LocalizationHelper.getString("anatomy_label_pubic_bone", language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryTeal,
                    contentColor = Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = LocalizationHelper.getString("anatomy_label_pelvic_floor", language),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

