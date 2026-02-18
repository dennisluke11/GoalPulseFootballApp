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
import com.example.goalpulse.ui.components.DetailRow
import com.example.goalpulse.ui.viewmodel.FixtureDetailViewModel
import com.example.goalpulse.ui.viewmodel.FixtureDetailUiState
import com.example.goalpulse.util.DateFormatter
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixtureDetailScreen(
    fixtureJson: String,
    viewModel: FixtureDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fixtureDetailState by viewModel.fixtureDetailState.collectAsState()
    
    LaunchedEffect(fixtureJson) {
        viewModel.loadFixture(fixtureJson)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.FIXTURE_DETAILS) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Strings.BACK)
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = fixtureDetailState) {
            is FixtureDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is FixtureDetailUiState.Success -> {
                val fixture = state.fixture
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(Dimens.paddingDefault),
                    verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault)
                ) {
                fixture.league?.let { league ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.cornerRadiusSmall)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.paddingDefault),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = league.name ?: Strings.UNKNOWN_LEAGUE,
                                fontSize = Dimens.textMedium,
                                fontWeight = FontWeight.Bold
                            )
                            league.logo?.let { logoUrl ->
                                AsyncImage(
                                    model = logoUrl,
                                    contentDescription = league.name,
                                    modifier = Modifier.size(Dimens.iconLarge)
                                )
                            }
                        }
                    }
                }
                
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                fixture.teams?.home?.logo?.let { logoUrl ->
                                    AsyncImage(
                                        model = logoUrl,
                                        contentDescription = fixture.teams?.home?.name,
                                        modifier = Modifier.size(Dimens.imageLarge)
                                    )
                                }
                                Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                                Text(
                                    text = fixture.teams?.home?.name ?: Strings.HOME,
                                    fontSize = Dimens.textDefault,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                fixture.goals?.let { goals ->
                                    Text(
                                        text = "${goals.home ?: "-"}",
                                        fontSize = Dimens.textHuge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = Strings.VS,
                                        fontSize = Dimens.textSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${goals.away ?: "-"}",
                                        fontSize = Dimens.textHuge,
                                        fontWeight = FontWeight.Bold
                                    )
                                } ?: run {
                                    Text(
                                        text = Strings.VS,
                                        fontSize = Dimens.textXLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                fixture.teams?.away?.logo?.let { logoUrl ->
                                    AsyncImage(
                                        model = logoUrl,
                                        contentDescription = fixture.teams?.away?.name,
                                        modifier = Modifier.size(Dimens.imageLarge)
                                    )
                                }
                                Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                                Text(
                                    text = fixture.teams?.away?.name ?: Strings.AWAY,
                                    fontSize = Dimens.textDefault,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                fixture.fixture?.let { fixtureInfo ->
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
                                text = Strings.MATCH_INFORMATION,
                                fontSize = Dimens.textLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                            )
                            
                            DetailRow(Strings.FIXTURE_ID, fixtureInfo.id.toString())
                            fixtureInfo.date?.let { date ->
                                DetailRow(Strings.DATE, DateFormatter.formatDate(date))
                            }
                            fixtureInfo.status?.let { status ->
                                DetailRow(Strings.STATUS, status.long ?: status.short ?: Strings.NOT_AVAILABLE)
                                status.elapsed?.let { elapsed ->
                                    DetailRow(Strings.ELAPSED_TIME, "$elapsed ${Strings.MINUTES}")
                                }
                            }
                            DetailRow(Strings.REFEREE, fixtureInfo.referee ?: Strings.NOT_AVAILABLE)
                            DetailRow(Strings.TIMEZONE, fixtureInfo.timezone ?: Strings.NOT_AVAILABLE)
                        }
                    }
                }
                
                fixture.fixture?.venue?.let { venue ->
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
                            
                            DetailRow(Strings.VENUE, venue.name ?: Strings.NOT_AVAILABLE)
                            DetailRow(Strings.ADDRESS, venue.address ?: Strings.NOT_AVAILABLE)
                            DetailRow(Strings.CITY, venue.city ?: Strings.NOT_AVAILABLE)
                            venue.capacity?.let { capacity ->
                                DetailRow(Strings.CAPACITY, "$capacity")
                            }
                        }
                    }
                }
                
                fixture.score?.let { score ->
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
                                text = Strings.SCORE_DETAILS,
                                fontSize = Dimens.textLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = Dimens.spacingSmall)
                            )
                            
                            score.fulltime?.let { fulltime ->
                                DetailRow(Strings.FULL_TIME, "${fulltime.home ?: "-"} - ${fulltime.away ?: "-"}")
                            }
                            score.halftime?.let { halftime ->
                                DetailRow(Strings.HALF_TIME, "${halftime.home ?: "-"} - ${halftime.away ?: "-"}")
                            }
                        }
                    }
                }
                }
            }
            is FixtureDetailUiState.Error -> {
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
                        Button(onClick = { viewModel.loadFixture(fixtureJson) }) {
                            Text(Strings.RETRY)
                        }
                    }
                }
            }
        }
    }
}

