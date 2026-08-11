package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.QuestionType
import com.example.data.viewmodel.LevelPlayState
import com.example.ui.components.CodeSnippetView
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.CodeEmerald
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GoldenStar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayLevelScreen(
    playState: LevelPlayState,
    onOptionSelect: (String) -> Unit,
    onReorderChange: (List<String>) -> Unit,
    onBlankSelect: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onToggleHint: () -> Unit,
    onSubmit: () -> Unit,
    onNextLevel: (Int) -> Unit,
    onExportGist: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isIncorrect by remember { mutableStateOf(false) }

    when (playState) {
        is LevelPlayState.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Select a level to play!")
            }
        }

        is LevelPlayState.Playing -> {
            val level = playState.level

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("exit_level_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Level ${level.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${level.track.title} • ${level.difficulty.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onToggleHint,
                        modifier = Modifier.testTag("hint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = if (playState.isHintVisible) GoldenStar else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prompt Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = level.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CodeEmerald
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = level.prompt,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Hint Drawer
                AnimatedVisibility(visible = playState.isHintVisible) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldenStar.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Hint",
                                tint = GoldenStar
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = level.hint,
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldenStar,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Code Snippet Box
                if (level.codeSnippet.isNotEmpty()) {
                    CodeSnippetView(code = level.codeSnippet)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Interactive Controls Based on Question Type
                when (level.questionType) {
                    QuestionType.MULTIPLE_CHOICE, QuestionType.BUG_FIX -> {
                        Text(
                            text = "Select the correct option:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        level.options.forEach { option ->
                            val isSelected = playState.selectedOption == option
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onOptionSelect(option)
                                        isIncorrect = false
                                        feedbackMessage = null
                                    }
                                    .testTag("option_chip_$option"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) CodeEmerald else MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    QuestionType.FILL_BLANKS -> {
                        Text(
                            text = "Select missing keyword to fill the blank:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            level.codeBlocks.forEach { option ->
                                val isSelected = playState.filledBlank == option
                                Button(
                                    onClick = {
                                        onBlankSelect(option)
                                        isIncorrect = false
                                        feedbackMessage = null
                                    },
                                    modifier = Modifier.testTag("blank_option_$option"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) CodeEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = option, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    QuestionType.REORDER_BLOCKS -> {
                        Text(
                            text = "Tap code blocks to reorder into correct order:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        playState.reorderedBlocks.forEachIndexed { index, blockText ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        // Simple reorder shift
                                        val mutable = playState.reorderedBlocks.toMutableList()
                                        if (index < mutable.size - 1) {
                                            val temp = mutable[index]
                                            mutable[index] = mutable[index + 1]
                                            mutable[index + 1] = temp
                                            onReorderChange(mutable)
                                        }
                                    }
                                    .testTag("reorder_block_$index"),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCyan
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = blockText,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Shift",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    QuestionType.CODE_SIMULATOR -> {
                        Text(
                            text = "Edit or complete the code solution:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = playState.userCode,
                            onValueChange = onCodeChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("code_simulator_editor"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                        )
                    }
                }

                // Error Feedback Box
                if (isIncorrect) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "❌ Incorrect answer. Try reviewing the hint or selecting another option!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        val success = onSubmit()
                        if (!success) {
                            isIncorrect = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_answer_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CodeEmerald)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Submit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Check Answer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        is LevelPlayState.Completed -> {
            val level = playState.level

            LevelCompletedModal(
                level = level,
                stars = playState.stars,
                xpEarned = playState.xpEarned,
                coinsEarned = playState.coinsEarned,
                onNextLevel = { onNextLevel(level.id + 1) },
                onExportGist = {
                    onExportGist(
                        "CodeQuest_Level_${level.id}.kt",
                        "CodeQuest Solution for Level ${level.id}: ${level.title}",
                        "// CodeQuest Level ${level.id} Solution\n// Track: ${level.track.title}\n${level.codeSnippet}\n\n// Correct Answer: ${level.correctAnswer}\n// Explanation: ${level.explanation}"
                    )
                },
                onExit = onBack
            )
        }
    }
}

@Composable
fun LevelCompletedModal(
    level: com.example.data.model.Level,
    stars: Int,
    xpEarned: Int,
    coinsEarned: Int,
    onNextLevel: () -> Unit,
    onExportGist: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onExit) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(CodeEmerald.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Victory",
                        tint = CodeEmerald,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "LEVEL COMPLETED!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CodeEmerald
                )

                Text(
                    text = level.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                StarRatingBar(stars = stars, starSize = 28)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+ $xpEarned XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CyberCyan)
                        Text("Experience", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+ $coinsEarned", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GoldenStar)
                        Text("Coins", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Explanation Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = level.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onExportGist,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_gist_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Publish, contentDescription = "Gist")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Solution to GitHub Gist")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onNextLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("next_level_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CodeEmerald)
                ) {
                    Text("Next Level", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
