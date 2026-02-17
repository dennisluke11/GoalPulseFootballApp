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
import com.example.goalpulse.data.model.Team
import com.example.goalpulse.ui.viewmodel.TeamsViewModel
import com.example.goalpulse.ui.viewmodel.TeamsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    viewModel: TeamsViewModel,
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val teamsState by viewModel.teamsState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.loadPopularTeams()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.FOOTBALL_TEAMS) },
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
                searchQuery = query
                viewModel.searchTeams(query)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(Strings.SEARCH_TEAMS_PLACEHOLDER) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = Strings.SEARCH)
            },
            singleLine = true,
            shape = RoundedCornerShape(Dimens.cornerRadiusSmall)
        )
        
        Spacer(modifier = Modifier.height(Dimens.paddingDefault))
        
        when (val state = teamsState) {
            is TeamsUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(Strings.START_SEARCHING_TEAMS)
                }
            }
            is TeamsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TeamsUiState.Success -> {
                if (state.teams.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(Strings.NO_TEAMS_FOUND)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault)
                    ) {
                        items(state.teams) { team ->
                            TeamItem(
                                team = team,
                                onClick = {
                                    val teamJson = com.google.gson.Gson().toJson(team)
                                    onNavigateToDetail(teamJson)
                                }
                            )
                        }
                    }
                }
            }
            is TeamsUiState.Error -> {
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
                        Button(onClick = { viewModel.searchTeams(searchQuery) }) {
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
fun TeamItem(
    team: Team,
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
            team.team?.logo?.let { logoUrl ->
                AsyncImage(
                    model = logoUrl,
                    contentDescription = team.team.name,
                    modifier = Modifier.size(Dimens.iconXXLarge)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingDefault))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.team?.name ?: Strings.UNKNOWN,
                    fontSize = Dimens.textMedium,
                    fontWeight = FontWeight.Bold
                )
                team.team?.country?.let { country ->
                    Text(
                        text = country,
                        fontSize = Dimens.textSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                team.venue?.name?.let { venue ->
                    Text(
                        text = venue,
                        fontSize = Dimens.textExtraSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

