package com.example.goalpulse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.goalpulse.ui.theme.Dimens
import coil.compose.AsyncImage
import com.example.goalpulse.data.model.Team
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamJson: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val team = try {
        Gson().fromJson(teamJson, Team::class.java)
    } catch (e: Exception) {
        null
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Team Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (team == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load team details")
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.paddingDefault),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.cornerRadiusMedium)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.paddingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault)
                    ) {
                        team.team?.logo?.let { logoUrl ->
                            AsyncImage(
                                model = logoUrl,
                                contentDescription = team.team?.name,
                                modifier = Modifier.size(Dimens.iconHuge)
                            )
                        }
                        
                        Text(
                            text = team.team?.name ?: "Unknown Team",
                            fontSize = Dimens.textTitle,
                            fontWeight = FontWeight.Bold
                        )
                        
                        team.team?.code?.let { code ->
                            Text(
                                text = code,
                                fontSize = Dimens.textMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.cornerRadiusSmall)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.paddingDefault),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault)
                    ) {
                        Text(
                            text = "Team Information",
                            fontSize = Dimens.textLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                        )
                        
                        DetailRow("Team ID", team.team?.id?.toString() ?: "N/A")
                        DetailRow("Name", team.team?.name ?: "N/A")
                        DetailRow("Code", team.team?.code ?: "N/A")
                        DetailRow("Country", team.team?.country ?: "N/A")
                        team.team?.founded?.let { founded ->
                            DetailRow("Founded", founded.toString())
                        }
                        team.team?.national?.let { national ->
                            DetailRow("National Team", if (national) "Yes" else "No")
                        }
                    }
                }
                
                team.venue?.let { venue ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.cornerRadiusSmall)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.paddingDefault),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault)
                        ) {
                            Text(
                                text = "Venue Information",
                                fontSize = Dimens.textLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                            )
                            
                            venue.name?.let { name ->
                                Text(
                                    text = name,
                                    fontSize = Dimens.textMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            DetailRow("Venue ID", venue.id?.toString() ?: "N/A")
                            DetailRow("Address", venue.address ?: "N/A")
                            DetailRow("City", venue.city ?: "N/A")
                            venue.capacity?.let { capacity ->
                                DetailRow("Capacity", "$capacity")
                            }
                            DetailRow("Surface", venue.surface ?: "N/A")
                            
                            venue.image?.let { imageUrl ->
                                Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = venue.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(Dimens.imageDetail)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = Dimens.textSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = Dimens.textSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

