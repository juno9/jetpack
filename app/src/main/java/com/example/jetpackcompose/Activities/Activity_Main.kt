package com.example.jetpackcompose.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Top row with "Friends" text and search button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "친구",
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = { /* Search action */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "돋보기 검색 버튼"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // My profile row
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Gray) // Placeholder for profile image
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "이름", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "상태 메시지",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section title for "Quick Friends"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "친구",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "총 4명", // Update this dynamically as needed
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of friends
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(10) { index -> // Replace with actual list size or data
                FriendItem(name = "친구 $index", status = "상태 메시지 $index")
            }
        }
    }
}

@Composable
fun FriendItem(name: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.Gray) // Placeholder for profile image
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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
