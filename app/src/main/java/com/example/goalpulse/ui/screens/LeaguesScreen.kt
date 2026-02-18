package com.example.goalpulse.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.goalpulse.ui.strings.Strings
import com.example.goalpulse.ui.theme.Dimens
import coil.compose.AsyncImage
import com.example.goalpulse.data.model.League
import com.example.goalpulse.ui.viewmodel.LeaguesViewModel
import com.example.goalpulse.ui.viewmodel.LeaguesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaguesScreen(
    viewModel: LeaguesViewModel,
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val leaguesState by viewModel.leaguesState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.FOOTBALL_LEAGUES) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.BACK)
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
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                viewModel.updateSearchQuery(query)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(Strings.SEARCH_LEAGUES_PLACEHOLDER) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = Strings.SEARCH)
            },
            singleLine = true,
            shape = RoundedCornerShape(Dimens.cornerRadiusSmall)
        )
        
        Spacer(modifier = Modifier.height(Dimens.paddingDefault))
        
        when (val state = leaguesState) {
            is LeaguesUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(Strings.START_SEARCHING_LEAGUES)
                }
            }
            is LeaguesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LeaguesUiState.Success -> {
                if (state.leagues.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(Strings.NO_LEAGUES_FOUND)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault)
                    ) {
                        items(state.leagues) { league ->
                            LeagueItem(
                                league = league,
                                onClick = {
                                    val leagueJson = com.google.gson.Gson().toJson(league)
                                    onNavigateToDetail(leagueJson)
                                }
                            )
                        }
                    }
                }
            }
            is LeaguesUiState.Error -> {
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
                        Button(onClick = { viewModel.loadAllLeagues() }) {
                            Text(Strings.RETRY)
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
fun LeagueItem(
    league: League,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.paddingDefault),
            verticalAlignment = Alignment.CenterVertically
        ) {
            league.league?.logo?.let { logoUrl ->
                AsyncImage(
                    model = logoUrl,
                    contentDescription = league.league.name,
                    modifier = Modifier.size(Dimens.iconXLarge)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingDefault))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = league.league?.name ?: Strings.UNKNOWN,
                    fontSize = Dimens.textMedium,
                    fontWeight = FontWeight.Bold
                )
                league.country?.name?.let { country ->
                    Text(
                        text = country,
                        fontSize = Dimens.textSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

