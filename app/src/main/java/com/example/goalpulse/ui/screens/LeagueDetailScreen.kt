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
import com.example.goalpulse.data.model.League
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDetailScreen(
    leagueJson: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val league = try {
        Gson().fromJson(leagueJson, League::class.java)
    } catch (e: Exception) {
        null
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("League Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (league == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load league details")
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
                        league.league?.logo?.let { logoUrl ->
                            AsyncImage(
                                model = logoUrl,
                                contentDescription = league.league?.name,
                                modifier = Modifier.size(Dimens.iconHuge)
                            )
                        }
                        
                        Text(
                            text = league.league?.name ?: "Unknown League",
                            fontSize = Dimens.textTitle,
                            fontWeight = FontWeight.Bold
                        )
                        
                        league.league?.type?.let { type ->
                            Text(
                                text = type,
                                fontSize = Dimens.textDefault,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            text = "League Information",
                            fontSize = Dimens.textLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                        )
                        
                        DetailRow("League ID", league.league?.id?.toString() ?: "N/A")
                        DetailRow("Name", league.league?.name ?: "N/A")
                        DetailRow("Type", league.league?.type ?: "N/A")
                    }
                }
                
                league.country?.let { country ->
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
                                text = "Country Information",
                                fontSize = Dimens.textLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                            )
                            
                            country.flag?.let { flagUrl ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingDefault)
                                ) {
                                    AsyncImage(
                                        model = flagUrl,
                                        contentDescription = country.name,
                                        modifier = Modifier.size(Dimens.imageSmall)
                                    )
                                    Text(
                                        text = country.name ?: "Unknown",
                                        fontSize = Dimens.textMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } ?: run {
                                DetailRow("Country", country.name ?: "N/A")
                            }
                            
                            DetailRow("Country Code", country.code ?: "N/A")
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

