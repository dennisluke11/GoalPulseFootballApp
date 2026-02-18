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
import com.example.goalpulse.ui.viewmodel.FixturesViewModel
import com.example.goalpulse.ui.viewmodel.FixtureDetailViewModel
import com.example.goalpulse.ui.viewmodel.LeaguesViewModel
import com.example.goalpulse.ui.viewmodel.LeagueDetailViewModel
import com.example.goalpulse.ui.viewmodel.TeamsViewModel
import com.example.goalpulse.ui.viewmodel.TeamDetailViewModel
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
            val leaguesViewModel: LeaguesViewModel = koinViewModel()
            LeaguesScreen(
                viewModel = leaguesViewModel,
                onNavigateToDetail = { leagueJson ->
                    navController.navigate(Screen.LeagueDetail.createRoute(leagueJson))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Teams.route) {
            val teamsViewModel: TeamsViewModel = koinViewModel()
            TeamsScreen(
                viewModel = teamsViewModel,
                onNavigateToDetail = { teamJson ->
                    navController.navigate(Screen.TeamDetail.createRoute(teamJson))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Fixtures.route) {
            val fixturesViewModel: FixturesViewModel = koinViewModel()
            FixturesScreen(
                viewModel = fixturesViewModel,
                onNavigateToDetail = { fixtureJson ->
                    navController.navigate(Screen.FixtureDetail.createRoute(fixtureJson))
                },
                onBackClick = { navController.popBackStack() }
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
            val leagueDetailViewModel: LeagueDetailViewModel = koinViewModel()
            LeagueDetailScreen(
                leagueJson = leagueJson,
                viewModel = leagueDetailViewModel,
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
            val teamDetailViewModel: TeamDetailViewModel = koinViewModel()
            TeamDetailScreen(
                teamJson = teamJson,
                viewModel = teamDetailViewModel,
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
            val fixtureDetailViewModel: FixtureDetailViewModel = koinViewModel()
            FixtureDetailScreen(
                fixtureJson = fixtureJson,
                viewModel = fixtureDetailViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
