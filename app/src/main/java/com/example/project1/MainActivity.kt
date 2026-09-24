package com.example.project1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.project1.database.AppDatabase
import com.example.project1.database.FavoriteEntity
import com.example.project1.network.FoursquareRepository
import com.example.project1.network.Restaurant
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val labelId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Favorites : Screen("favorites", R.string.nav_favorites, Icons.Default.Favorite)
    object Profile : Screen("profile", R.string.nav_profile, Icons.Default.Person)
}

const val EXTRA_USER_ID = SetPreferencesActivity.EXTRA_USER_ID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserId = UserSession.getUserId(applicationContext)

        if (currentUserId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()

        setContent {
            Project1Theme {
                val navController = rememberNavController()
                val items = listOf(Screen.Home, Screen.Favorites, Screen.Profile)

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            screen.icon,
                                            contentDescription = null
                                        )
                                    },
                                    label = {
                                        Text(stringResource(screen.labelId))
                                    },
                                    selected = currentDestination?.hierarchy?.any {
                                        it.route == screen.route
                                    } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(
                                                navController.graph.findStartDestination().id
                                            ) {
                                                saveState = true
                                            }

                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomePage(currentUserId)
                        }

                        composable(Screen.Favorites.route) {
                            FavoritePage(currentUserId)
                        }

                        composable(Screen.Profile.route) {
                            ProfilePage(
                                currentUserId = currentUserId,
                                onLogout = ::logout
                            )
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        UserSession.clearUserId(applicationContext)

        val loginIntent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(loginIntent)
        finish()
    }
}

@Composable
fun HomePage(currentUserId: Long) {
    // This are the states that drive the loading, error, empty and restaurant-list views
    var restaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(value = true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var randomizerRequest by remember { mutableStateOf(0) }

    val context = LocalContext.current

    val database = remember(context) {
        AppDatabase.getDatabase(context)
    }
    val favoriteList by database.favoriteDao()
        .observeFavoritesForUser(currentUserId)
        .collectAsState(initial = emptyList())

    // Just the restaurant IDs that are currently favorited
    val favoriteIds = favoriteList
        .map { it.restaurantId }
        .toSet()
    // coroutine is being used so that the room database can be updated at the same time as other stuff going on
    val coroutineScope = rememberCoroutineScope()

    // This is where the load preference is done and make the network request once Home enters composition
    LaunchedEffect(currentUserId, randomizerRequest) {
        isLoading = true
        errorMessage = null
        try {
            val user = database.userDao().getUserById(currentUserId)

            if (user == null) {
                error("No saved preferences found")
            }

            restaurants = FoursquareRepository()
                .searchRestaurants(user.address, user.distanceMiles)
        } catch (error: Exception) {
            errorMessage = error.message ?: "Could not load restaurants"
        } finally {
            isLoading = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Restaurants near you",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()

                errorMessage != null -> Text(errorMessage!!, color = Color.Red)

                restaurants.isEmpty() -> Text("No restaurants found for your preferences.")

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            restaurants,
                            key = { it.id }
                        ) { restaurant ->
                            RestaurantCard(
                                restaurant = restaurant,
                                isFavorite = restaurant.id in favoriteIds,
                                onFavoriteClick = {
                                    val existingFavorite = favoriteList.find {
                                        it.restaurantId == restaurant.id
                                    }

                                    coroutineScope.launch {
                                        if (existingFavorite != null) {
                                            database.favoriteDao()
                                                .deleteFavorite(existingFavorite)
                                        } else {
                                            database.favoriteDao()
                                                .addFavorite(
                                                    FavoriteEntity(
                                                        userId = currentUserId,
                                                        restaurantId = restaurant.id,
                                                        restaurantName = restaurant.name,
                                                        restaurantAddress = ""
                                                    )
                                                )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { randomizerRequest++ },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Randomize Restaurants")
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    // Pro Place Search does not return Premium photo data, so cards use a fallback image state.
    Card(Modifier.fillMaxWidth()) {
        Column {
            if (restaurant.imageUrl != null) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = "Photo of ${restaurant.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = restaurant.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFavorite) {
                        "Saved to favorites"
                    } else {
                        "Add to favorites"
                    },
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp
                )
                // Favorite button to add to Favorites page
                IconButton(
                    onClick = onFavoriteClick
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = if (isFavorite) {
                            "Remove from favorites"
                        } else {
                            "Add to favorites"
                        },
                        tint = if (isFavorite) {
                            Color.Red
                        } else {
                            Color.Gray
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfilePage(
    currentUserId: Long,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val database = remember(context) {
        AppDatabase.getDatabase(context)
    }

    val user by database.userDao()
        .observeUser(currentUserId)
        .collectAsState(initial = null)

    val favoriteCount by database.favoriteDao()
        .observeFavoriteCountForUser(currentUserId)
        .collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (user == null) {
            Text("Loading profile...")
        } else {
            Text(
                text = "Username: ${user!!.username}",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Preferred radius: ${user!!.distanceMiles} miles",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "$favoriteCount favorites",
                fontSize = 18.sp
            )
        }

        Button(
            onClick = {
                context.startActivity(
                    Intent(context, SetPreferencesActivity::class.java).apply {
                        putExtra(EXTRA_USER_ID, currentUserId)
                    }
                )
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Update your profile")
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Log out")
        }
    }
}

@Composable
fun PageContent(title: String, subtitle: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = subtitle,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = body,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
