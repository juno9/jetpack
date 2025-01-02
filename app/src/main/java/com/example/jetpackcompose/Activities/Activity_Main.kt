package com.example.jetpackcompose.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcompose.R

import com.example.jetpackcompose.ui.theme.JetpackcomposeTheme

class Activity_Main : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackcomposeTheme {
                MainScreen()
            }
        }



    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Composable

fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("", "friends", R.drawable.friends),
        BottomNavItem("", "chat", R.drawable.chat),
        BottomNavItem("", "settings", R.drawable.settings)
    )

    NavigationBar {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(50.dp) // 아이콘 크기 조정
                            .padding(3.dp) // 아이콘 여백 조정
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}



@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "friends") {
        composable("friends") { FriendsScreen() }
        composable("chat") { ChatScreen() }
        composable("settings") { SettingsScreen() }
    }
}

@Composable
fun FriendsScreen() {
    val friends = listOf("John Doe", "Jane Smith", "Alice Brown", "Bob Johnson") // 샘플 데이터

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(friends.size) { index ->
            FriendItem(name = friends[index])
        }
    }
}
@Composable
fun FriendItem(name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}
@Composable
fun ChatScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("채팅 화면", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("설정 화면", modifier = Modifier.align(Alignment.Center))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackcomposeTheme {
        MainScreen()
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val iconResId: Int
)

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
