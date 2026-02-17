package com.example.goalpulse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.goalpulse.ui.strings.Strings
import com.example.goalpulse.ui.theme.Dimens

@Composable
fun HomeScreen(
    onNavigateToLeagues: () -> Unit,
    onNavigateToTeams: () -> Unit,
    onNavigateToFixtures: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.paddingDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = Strings.APP_NAME,
            fontSize = Dimens.textHuge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingSmall))
        
        Text(
            text = Strings.APP_TAGLINE,
            fontSize = Dimens.textDefault,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingXXLarge))
        
        NavigationCard(
            title = Strings.LEAGUES,
            description = Strings.SEARCH_LEAGUES_DESC,
            onClick = onNavigateToLeagues
        )
        
        Spacer(modifier = Modifier.height(Dimens.paddingDefault))
        
        NavigationCard(
            title = Strings.TEAMS,
            description = Strings.SEARCH_TEAMS_DESC,
            onClick = onNavigateToTeams
        )
        
        Spacer(modifier = Modifier.height(Dimens.paddingDefault))
        
        NavigationCard(
            title = Strings.FIXTURES,
            description = Strings.VIEW_FIXTURES_DESC,
            onClick = onNavigateToFixtures
        )
    }
}

@Composable
fun NavigationCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.cardHeight),
        shape = RoundedCornerShape(Dimens.cornerRadiusSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.paddingDefault),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = Dimens.textLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Dimens.spacingExtraSmall))
            Text(
                text = description,
                fontSize = Dimens.textSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

