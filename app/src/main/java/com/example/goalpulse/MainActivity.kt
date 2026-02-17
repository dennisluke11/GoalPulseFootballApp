package com.example.goalpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.goalpulse.ui.navigation.Screen
import com.example.goalpulse.ui.screens.FixtureDetailScreen
import com.example.goalpulse.ui.screens.FixturesScreen
import com.example.goalpulse.ui.screens.HomeScreen
import com.example.goalpulse.ui.screens.LeagueDetailScreen
import com.example.goalpulse.ui.screens.LeaguesScreen
import com.example.goalpulse.ui.screens.TeamDetailScreen
import com.example.goalpulse.ui.screens.TeamsScreen
import com.example.goalpulse.ui.theme.GoalPulseTheme
import com.example.goalpulse.ui.viewmodel.FootballViewModel
import org.koin.androidx.compose.koinViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoalPulseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GoalPulseNavigation()
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun GoalPulseNavigation() {
    val navController = rememberNavController()
    val viewModel: FootballViewModel = koinViewModel()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLeagues = { navController.navigate(Screen.Leagues.route) },
                onNavigateToTeams = { navController.navigate(Screen.Teams.route) },
                onNavigateToFixtures = { navController.navigate(Screen.Fixtures.route) }
            )
        }
        
        composable(Screen.Leagues.route) {
            LeaguesScreen(
                viewModel = viewModel,
                onNavigateToDetail = { leagueJson ->
                    navController.navigate(Screen.LeagueDetail.createRoute(leagueJson))
                }
            )
        }
        
        composable(Screen.Teams.route) {
            TeamsScreen(
                viewModel = viewModel,
                onNavigateToDetail = { teamJson ->
                    navController.navigate(Screen.TeamDetail.createRoute(teamJson))
                }
            )
        }
        
        composable(Screen.Fixtures.route) {
            FixturesScreen(
                viewModel = viewModel,
                onNavigateToDetail = { fixtureJson ->
                    navController.navigate(Screen.FixtureDetail.createRoute(fixtureJson))
                }
            )
        }
        
        composable(
            route = Screen.LeagueDetail.route,
            arguments = listOf(navArgument("leagueJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val leagueJson = URLDecoder.decode(
                backStackEntry.arguments?.getString("leagueJson") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            LeagueDetailScreen(
                leagueJson = leagueJson,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TeamDetail.route,
            arguments = listOf(navArgument("teamJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamJson = URLDecoder.decode(
                backStackEntry.arguments?.getString("teamJson") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            TeamDetailScreen(
                teamJson = teamJson,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.FixtureDetail.route,
            arguments = listOf(navArgument("fixtureJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val fixtureJson = URLDecoder.decode(
                backStackEntry.arguments?.getString("fixtureJson") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            FixtureDetailScreen(
                fixtureJson = fixtureJson,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
