package com.example.goalpulse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.goalpulse.ui.strings.Strings
import com.example.goalpulse.ui.theme.Dimens
import coil.compose.AsyncImage
import com.example.goalpulse.ui.viewmodel.TeamDetailViewModel
import com.example.goalpulse.ui.viewmodel.TeamDetailUiState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamJson: String,
    viewModel: TeamDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val teamDetailState by viewModel.teamDetailState.collectAsState()
    
    LaunchedEffect(teamJson) {
        viewModel.loadTeam(teamJson)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.TEAM_DETAILS) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Strings.BACK)
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = teamDetailState) {
            is TeamDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TeamDetailUiState.Success -> {
                val team = state.team
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
                            text = team.team?.name ?: Strings.UNKNOWN_TEAM,
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
                            text = Strings.TEAM_INFORMATION,
                            fontSize = Dimens.textLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                        )
                        
                        DetailRow(Strings.TEAM_ID, team.team?.id?.toString() ?: Strings.NOT_AVAILABLE)
                        DetailRow(Strings.NAME, team.team?.name ?: Strings.NOT_AVAILABLE)
                        DetailRow(Strings.CODE, team.team?.code ?: Strings.NOT_AVAILABLE)
                        DetailRow(Strings.COUNTRY, team.team?.country ?: Strings.NOT_AVAILABLE)
                        team.team?.founded?.let { founded ->
                            DetailRow(Strings.FOUNDED, founded.toString())
                        }
                        team.team?.national?.let { national ->
                            DetailRow(Strings.NATIONAL_TEAM, if (national) Strings.YES else Strings.NO)
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
                                text = Strings.VENUE_INFORMATION,
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
                            
                            DetailRow(Strings.VENUE_ID, venue.id?.toString() ?: Strings.NOT_AVAILABLE)
                            DetailRow(Strings.ADDRESS, venue.address ?: Strings.NOT_AVAILABLE)
                            DetailRow(Strings.CITY, venue.city ?: Strings.NOT_AVAILABLE)
                            venue.capacity?.let { capacity ->
                                DetailRow(Strings.CAPACITY, "$capacity")
                            }
                            DetailRow(Strings.SURFACE, venue.surface ?: Strings.NOT_AVAILABLE)
                            
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
            is TeamDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(Dimens.paddingDefault)
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(Dimens.paddingDefault))
                        Button(onClick = { viewModel.loadTeam(teamJson) }) {
                            Text(Strings.RETRY)
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

