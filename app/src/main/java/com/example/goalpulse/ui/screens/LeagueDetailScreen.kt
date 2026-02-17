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
import com.example.goalpulse.ui.strings.Strings
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
                title = { Text(Strings.LEAGUE_DETAILS) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Strings.BACK)
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
                Text(Strings.FAILED_LOAD_LEAGUE)
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
                            text = league.league?.name ?: Strings.UNKNOWN_LEAGUE,
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
                            text = Strings.LEAGUE_INFORMATION,
                            fontSize = Dimens.textLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                        )
                        
                        DetailRow(Strings.LEAGUE_ID, league.league?.id?.toString() ?: Strings.NOT_AVAILABLE)
                        DetailRow(Strings.NAME, league.league?.name ?: Strings.NOT_AVAILABLE)
                        DetailRow(Strings.TYPE, league.league?.type ?: Strings.NOT_AVAILABLE)
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
                                text = Strings.COUNTRY_INFORMATION,
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
                                        text = country.name ?: Strings.UNKNOWN,
                                        fontSize = Dimens.textMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } ?: run {
                                DetailRow(Strings.COUNTRY, country.name ?: Strings.NOT_AVAILABLE)
                            }
                            
                            DetailRow(Strings.COUNTRY_CODE, country.code ?: Strings.NOT_AVAILABLE)
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

