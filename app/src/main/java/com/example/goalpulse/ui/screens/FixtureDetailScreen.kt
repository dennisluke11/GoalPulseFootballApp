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
import com.example.goalpulse.data.model.Fixture
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixtureDetailScreen(
    fixtureJson: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fixture = try {
        Gson().fromJson(fixtureJson, Fixture::class.java)
    } catch (e: Exception) {
        null
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
        if (fixture == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(Strings.FAILED_LOAD_FIXTURE)
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
                                DetailRow(Strings.DATE, formatDate(date))
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

