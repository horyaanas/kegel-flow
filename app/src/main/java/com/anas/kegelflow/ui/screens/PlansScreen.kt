package com.anas.kegelflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.data.CustomPlan
import com.anas.kegelflow.ui.model.PlanLevel
import com.anas.kegelflow.ui.model.WorkoutPlan
import com.anas.kegelflow.ui.theme.PrimaryTeal
import com.anas.kegelflow.ui.viewmodel.KegelViewModel
import com.anas.kegelflow.util.LocalizationHelper

@Composable
fun PlansScreen(
    viewModel: KegelViewModel,
    onStartWorkout: () -> Unit
) {
    val appSettings by viewModel.appSettings.collectAsState()
    val language = appSettings.language
    val selectedPlan by viewModel.selectedPlan.collectAsState()
    val customPlans by viewModel.customPlans.collectAsState()

    var showCreateCustomDialog by remember { mutableStateOf(false) }

    val isAr = language == AppLanguage.ARABIC

    if (showCreateCustomDialog) {
        CreateCustomPlanDialog(
            language = language,
            onDismiss = { showCreateCustomDialog = false },
            onSave = { name, contract, relax, reps ->
                viewModel.createCustomPlan(name, contract, relax, reps)
                showCreateCustomDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("plans_screen")
    ) {
        // Top Title Header
        Text(
            text = LocalizationHelper.getString("tab_plans", language),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
        )

        // Preset Plans List
        Text(
            text = if (isAr) "الخطط المجهزة" else "Preset Plans",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        WorkoutPlan.getAllPresets().forEach { plan ->
            PlanCard(
                plan = plan,
                isSelected = selectedPlan.level == plan.level,
                language = language,
                onSelect = {
                    viewModel.setSelectedPlan(plan)
                },
                onStart = {
                    viewModel.setSelectedPlan(plan)
                    onStartWorkout()
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Plans Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = LocalizationHelper.getString("custom_plan", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { showCreateCustomDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("create_custom_plan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = LocalizationHelper.getString("create_custom_plan", language),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (customPlans.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAr) "لم تقم بإنشاء خطط مخصصة بعد." else "No custom plans created yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            customPlans.forEach { custom ->
                val plan = WorkoutPlan(
                    level = PlanLevel.CUSTOM,
                    titleAr = custom.name,
                    titleEn = custom.name,
                    descriptionAr = "${custom.contractSeconds}ث ضغط + ${custom.relaxSeconds}ث استرخاء × ${custom.reps} تكرارات",
                    descriptionEn = "${custom.contractSeconds}s Contract + ${custom.relaxSeconds}s Relax × ${custom.reps} reps",
                    contractSeconds = custom.contractSeconds,
                    relaxSeconds = custom.relaxSeconds,
                    reps = custom.reps
                )

                CustomPlanCard(
                    plan = plan,
                    isSelected = selectedPlan.level == PlanLevel.CUSTOM && selectedPlan.titleAr == custom.name,
                    language = language,
                    onSelect = { viewModel.setSelectedPlan(plan) },
                    onStart = {
                        viewModel.setSelectedPlan(plan)
                        onStartWorkout()
                    },
                    onDelete = { viewModel.deleteCustomPlan(custom.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PlanCard(
    plan: WorkoutPlan,
    isSelected: Boolean,
    language: AppLanguage,
    onSelect: () -> Unit,
    onStart: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("plan_card_${plan.level.name}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onSelect,
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryTeal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) plan.titleAr else plan.titleEn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = plan.getFormattedDuration(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAr) plan.descriptionAr else plan.descriptionEn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    modifier = Modifier.testTag("start_plan_${plan.level.name}_button")
                ) {
                    Text(
                        text = LocalizationHelper.getString("start_workout", language),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomPlanCard(
    plan: WorkoutPlan,
    isSelected: Boolean,
    language: AppLanguage,
    onSelect: () -> Unit,
    onStart: () -> Unit,
    onDelete: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("custom_plan_card_${plan.titleAr}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onSelect,
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryTeal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = plan.titleAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_custom_plan_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(
                text = if (isAr) plan.descriptionAr else plan.descriptionEn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text(
                        text = LocalizationHelper.getString("start_workout", language),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateCustomPlanDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (name: String, contract: Int, relax: Int, reps: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var contractStr by remember { mutableStateOf("5") }
    var relaxStr by remember { mutableStateOf("5") }
    var repsStr by remember { mutableStateOf("5") }

    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("create_custom_plan_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = LocalizationHelper.getString("create_custom_plan", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(LocalizationHelper.getString("plan_name", language)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_plan_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contractStr,
                    onValueChange = { contractStr = it },
                    label = { Text(LocalizationHelper.getString("contract_time", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_plan_contract_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = relaxStr,
                    onValueChange = { relaxStr = it },
                    label = { Text(LocalizationHelper.getString("relax_time", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_plan_relax_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = repsStr,
                    onValueChange = { repsStr = it },
                    label = { Text(LocalizationHelper.getString("reps_count", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_plan_reps_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_custom_plan_button")
                    ) {
                        Text(text = LocalizationHelper.getString("cancel", language))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val finalName = if (name.isBlank()) {
                                if (isAr) "خطة مخصصة" else "Custom Plan"
                            } else name
                            val contract = contractStr.toIntOrNull() ?: 5
                            val relax = relaxStr.toIntOrNull() ?: 5
                            val reps = repsStr.toIntOrNull() ?: 5
                            onSave(finalName, contract, relax, reps)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        modifier = Modifier.testTag("save_custom_plan_button")
                    ) {
                        Text(
                            text = LocalizationHelper.getString("save", language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
