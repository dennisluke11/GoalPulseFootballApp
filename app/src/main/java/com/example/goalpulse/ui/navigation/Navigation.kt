package com.example.goalpulse.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Leagues : Screen("leagues")
    object Teams : Screen("teams")
    object Fixtures : Screen("fixtures")
    
    object LeagueDetail : Screen("league_detail/{leagueJson}") {
        fun createRoute(leagueJson: String) = "league_detail/${java.net.URLEncoder.encode(leagueJson, java.nio.charset.StandardCharsets.UTF_8.toString())}"
    }
    
    object TeamDetail : Screen("team_detail/{teamJson}") {
        fun createRoute(teamJson: String) = "team_detail/${java.net.URLEncoder.encode(teamJson, java.nio.charset.StandardCharsets.UTF_8.toString())}"
    }
    
    object FixtureDetail : Screen("fixture_detail/{fixtureJson}") {
        fun createRoute(fixtureJson: String) = "fixture_detail/${java.net.URLEncoder.encode(fixtureJson, java.nio.charset.StandardCharsets.UTF_8.toString())}"
    }
}

