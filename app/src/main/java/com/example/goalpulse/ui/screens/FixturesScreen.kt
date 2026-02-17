package com.example.goalpulse.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.goalpulse.ui.theme.Dimens
import coil.compose.AsyncImage
import com.example.goalpulse.data.model.Fixture
import com.example.goalpulse.ui.viewmodel.FixturesViewModel
import com.example.goalpulse.ui.viewmodel.FixturesUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(
    viewModel: FixturesViewModel,
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fixturesState by viewModel.fixturesState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadFixtures(leagueId = 39)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Football Fixtures") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = Dimens.paddingDefault,
                    end = Dimens.paddingDefault,
                    bottom = Dimens.paddingDefault
                )
        ) {
        
        when (val state = fixturesState) {
            is FixturesUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading fixtures...")
                }
            }
            is FixturesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is FixturesUiState.Success -> {
                if (state.fixtures.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No fixtures found")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault)
                    ) {
                        items(state.fixtures) { fixture ->
                            FixtureItem(
                                fixture = fixture,
                                onClick = {
                                    val fixtureJson = com.google.gson.Gson().toJson(fixture)
                                    onNavigateToDetail(fixtureJson)
                                }
                            )
                        }
                    }
                }
            }
            is FixturesUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                        Button(onClick = { viewModel.loadFixtures() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
fun FixtureItem(
    fixture: Fixture,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.cornerRadiusSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.paddingDefault)
        ) {
            fixture.league?.let { league ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = league.name ?: "Unknown League",
                        fontSize = Dimens.textSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    league.logo?.let { logoUrl ->
                        AsyncImage(
                            model = logoUrl,
                            contentDescription = league.name,
                            modifier = Modifier.size(Dimens.iconSmall)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.spacingSmall))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    fixture.teams?.home?.let { homeTeam ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            homeTeam.logo?.let { logoUrl ->
                                AsyncImage(
                                    model = logoUrl,
                                    contentDescription = homeTeam.name,
                                    modifier = Modifier.size(Dimens.imageSmall)
                                )
                                Spacer(modifier = Modifier.width(Dimens.spacingSmall))
                            }
                            Text(
                                text = homeTeam.name,
                                fontSize = Dimens.textDefault,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                    fixture.teams?.away?.let { awayTeam ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            awayTeam.logo?.let { logoUrl ->
                                AsyncImage(
                                    model = logoUrl,
                                    contentDescription = awayTeam.name,
                                    modifier = Modifier.size(Dimens.imageSmall)
                                )
                                Spacer(modifier = Modifier.width(Dimens.spacingSmall))
                            }
                            Text(
                                text = awayTeam.name,
                                fontSize = Dimens.textDefault,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    fixture.goals?.let { goals ->
                        Text(
                            text = "${goals.home ?: "-"}",
                            fontSize = Dimens.textLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                        Text(
                            text = "${goals.away ?: "-"}",
                            fontSize = Dimens.textLarge,
                            fontWeight = FontWeight.Bold
                        )
                    } ?: run {
                        Text(
                            text = "-",
                            fontSize = Dimens.textLarge
                        )
                        Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                        Text(
                            text = "-",
                            fontSize = Dimens.textLarge
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))
            
            Divider()
            
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                fixture.fixture?.date?.let { dateString ->
                    Text(
                        text = formatDate(dateString),
                        fontSize = Dimens.textExtraSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                fixture.fixture?.status?.short?.let { status ->
                    Text(
                        text = status,
                        fontSize = Dimens.textExtraSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

