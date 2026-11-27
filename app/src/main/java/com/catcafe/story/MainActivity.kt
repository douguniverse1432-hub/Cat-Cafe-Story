package com.catcafe.story

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class CafeCat(val name: String, val breed: String, val mood: String, val level: Int)

private data class CafeProgress(
    var coins: Int = 250,
    var day: Int = 1,
    var reputation: Int = 0,
    var roomLevel: Int = 1,
    var servedToday: Int = 0,
    var cats: MutableList<CafeCat> = mutableListOf(
        CafeCat("Mochi", "Calico", "Curious", 1),
        CafeCat("Miso", "Tabby", "Sleepy", 1)
    ),
    var storyChapter: Int = 0,
    var claimedGoals: MutableSet<String> = mutableSetOf()
)

private enum class CafeTab(val label: String, val icon: ImageVector) {
    CAFE("Café", Icons.Default.Home),
    CATS("Cats", Icons.Default.Cat),
    STORY("Story", Icons.Default.AutoAwesome),
    GOALS("Goals", Icons.Default.TaskAlt)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progress = loadProgress(this)
        setContent {
            MaterialTheme {
                CafeGame(progress = progress, onSave = { saveProgress(this, progress) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CafeGame(progress: CafeProgress, onSave: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var refresh by remember { mutableIntStateOf(0) }
    fun changed() {
        onSave()
        refresh++
    }
    // Reading refresh makes each small, local game action redraw the screen.
    val redraw = refresh

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🐾 Cat Café Story", fontWeight = FontWeight.Bold) },
                actions = { Text("🪙 ${progress.coins}   ⭐ ${progress.reputation}  ", fontWeight = FontWeight.SemiBold) }
            )
        },
        bottomBar = {
            NavigationBar {
                CafeTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { androidx.compose.material3.Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (CafeTab.entries[selectedTab]) {
            CafeTab.CAFE -> CafeScreen(progress, padding, ::changed)
            CafeTab.CATS -> CatsScreen(progress, padding, ::changed)
            CafeTab.STORY -> StoryScreen(progress, padding, ::changed)
            CafeTab.GOALS -> GoalsScreen(progress, padding, ::changed)
        }
        if (redraw < 0) Text("")
    }
}

@Composable
private fun CafeScreen(progress: CafeProgress, padding: PaddingValues, changed: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Day ${progress.day} · Cozy corner café", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Your little café is growing. Welcome guests, care for your cats, and make this a place people remember.")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🏠 Room Lv. ${progress.roomLevel}")
                        Text("🐈 ${progress.cats.size} cats")
                        Text("☕ ${progress.servedToday} served today")
                    }
                }
            }
        }
        item {
            Text("Today's service", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Guests are waiting for a warm drink and a little cat time.")
            Button(onClick = {
                progress.coins += 35 + (progress.roomLevel * 5)
                progress.reputation += 2
                progress.servedToday++
                changed()
            }, modifier = Modifier.fillMaxWidth()) { Text("Serve a guest  ·  +${35 + progress.roomLevel * 5} coins") }
        }
        item {
            Text("Café upgrades", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            GameCard("Add a cozy corner", "Room level ${progress.roomLevel} → ${progress.roomLevel + 1}", "🪑", "Upgrade · ${100 * progress.roomLevel} coins", enabled = progress.coins >= 100 * progress.roomLevel) {
                progress.coins -= 100 * progress.roomLevel
                progress.roomLevel++
                progress.reputation += 5
                changed()
            }
        }
        item {
            Button(onClick = {
                progress.day++
                progress.servedToday = 0
                changed()
            }, modifier = Modifier.fillMaxWidth()) { Text("Close café and start Day ${progress.day + 1}") }
        }
    }
}

@Composable
private fun CatsScreen(progress: CafeProgress, padding: PaddingValues, changed: () -> Unit) {
    val availableNames = listOf("Pudding", "Bean", "Clover", "Sesame", "Peaches", "Nori", "Biscuit", "Luna")
    val breeds = listOf("Tuxedo", "Ginger", "Calico", "Siamese", "Tabby", "Tortoiseshell")
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text("Your cats", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Happy cats make every visit special. Give them a treat to lift their mood.")
            Spacer(Modifier.height(6.dp))
        }
        items(progress.cats) { cat ->
            GameCard("🐈 ${cat.name} · ${cat.breed}", "Mood: ${cat.mood}     Bond level ${cat.level}", "💛", "Give a treat · 15 coins", enabled = progress.coins >= 15) {
                progress.coins -= 15
                val index = progress.cats.indexOf(cat)
                if (index >= 0) progress.cats[index] = cat.copy(mood = "Purring", level = cat.level + 1)
                progress.reputation++
                changed()
            }
        }
        item {
            GameCard("Meet a new friend", "Adopt a rescue cat and welcome them to the café.", "🐾", "Adopt · 80 coins", enabled = progress.coins >= 80 && progress.cats.size < 8) {
                progress.coins -= 80
                val name = availableNames[(progress.cats.size - 2).coerceIn(0, availableNames.lastIndex)]
                val breed = breeds[(progress.cats.size - 2).coerceIn(0, breeds.lastIndex)]
                progress.cats.add(CafeCat(name, breed, "Shy", 1))
                changed()
            }
            if (progress.cats.size >= 8) Text("Your café is full. Upgrade your space to welcome more cats!")
        }
    }
}

@Composable
private fun StoryScreen(progress: CafeProgress, padding: PaddingValues, changed: () -> Unit) {
    val chapters = listOf(
        "A Place to Begin" to "The old café is quiet when you first unlock the door. A handwritten note rests on the counter: ‘A home is made by the ones who return.’ Mochi pads in, curious about your plans.",
        "A Familiar Face" to "A regular visitor lingers by the window. They remember this café from years ago, when it was full of laughter and warm cups. Maybe your little place can feel that way again.",
        "Room for One More" to "On a rainy afternoon, a tiny cat waits beneath the awning. Miso watches from the doorway. There is a blanket by the stove and, now, one more reason to keep the lights on.",
        "The Neighborhood Table" to "Word has spread. Neighbors bring stories, recipes, and kind hands to help. Your café is becoming more than a place to eat—it is a place to belong."
    )
    val unlocked = progress.storyChapter.coerceIn(0, chapters.lastIndex)
    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Little moments", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Story chapter ${unlocked + 1} of ${chapters.size}")
        GameCard("📖 ${chapters[unlocked].first}", chapters[unlocked].second, "✨", "Continue story · 40 reputation", enabled = progress.reputation >= 40 && unlocked < chapters.lastIndex) {
            progress.reputation -= 40
            progress.storyChapter = (progress.storyChapter + 1).coerceAtMost(chapters.lastIndex)
            changed()
        }
        Text("Unlocked chapters", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        for (index in 0..unlocked) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(chapters[index].first, fontWeight = FontWeight.Bold)
                    Text(chapters[index].second)
                }
            }
        }
        if (unlocked < chapters.lastIndex) Text("Earn reputation by serving guests to discover what happens next.")
    }
}

@Composable
private fun GoalsScreen(progress: CafeProgress, padding: PaddingValues, changed: () -> Unit) {
    val goals = listOf(
        Triple("Serve 3 guests", progress.servedToday.coerceAtMost(3), 3),
        Triple("Welcome 3 café cats", progress.cats.size.coerceAtMost(3), 3),
        Triple("Reach 10 reputation", progress.reputation.coerceAtMost(10), 10)
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text("Daily goals", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Small steps make a lovely café. Complete each goal and collect your reward.")
        }
        items(goals) { (title, current, target) ->
            val claimed = title in progress.claimedGoals
            GameCard(title, "$current / $target complete", "🎯", if (claimed) "Reward claimed" else "Claim · 50 coins", enabled = current >= target && !claimed) {
                progress.coins += 50
                progress.claimedGoals.add(title)
                changed()
            }
        }
        item {
            Button(onClick = {
                progress.claimedGoals.clear()
                progress.servedToday = 0
                progress.day++
                changed()
            }, modifier = Modifier.fillMaxWidth()) { Text("Start a new day and refresh goals") }
            Text("Achievements", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            Text(if (progress.cats.size >= 3) "🏆 Cat Collector · Adopted three café cats" else "🔒 Cat Collector · Welcome three cats")
            Text(if (progress.reputation >= 10) "🏆 Neighborhood Favorite · Earned 10 reputation" else "🔒 Neighborhood Favorite · Earn 10 reputation")
        }
    }
}

@Composable
private fun GameCard(title: String, description: String, icon: String, action: String, enabled: Boolean, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 28.sp, modifier = Modifier.size(40.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onClick, enabled = enabled, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(action)
                }
            }
        }
    }
}

private fun loadProgress(context: Context): CafeProgress {
    val prefs = context.getSharedPreferences("cat_cafe_save", Context.MODE_PRIVATE)
    val names = prefs.getString("cats", "Mochi|Calico|Curious|1;Miso|Tabby|Sleepy|1")
        .orEmpty().split(';').filter { it.isNotBlank() }
    val cats = names.mapNotNull { encoded ->
        val fields = encoded.split('|')
        if (fields.size != 4) null else CafeCat(fields[0], fields[1], fields[2], fields[3].toIntOrNull() ?: 1)
    }.toMutableList()
    val claimed = prefs.getStringSet("claimed", emptySet()).orEmpty().toMutableSet()
    return CafeProgress(
        coins = prefs.getInt("coins", 250),
        day = prefs.getInt("day", 1),
        reputation = prefs.getInt("reputation", 0),
        roomLevel = prefs.getInt("room", 1),
        servedToday = prefs.getInt("served", 0),
        cats = cats.ifEmpty { mutableListOf(CafeCat("Mochi", "Calico", "Curious", 1), CafeCat("Miso", "Tabby", "Sleepy", 1)) },
        storyChapter = prefs.getInt("story", 0),
        claimedGoals = claimed
    )
}

private fun saveProgress(context: Context, progress: CafeProgress) {
    context.getSharedPreferences("cat_cafe_save", Context.MODE_PRIVATE).edit()
        .putInt("coins", progress.coins)
        .putInt("day", progress.day)
        .putInt("reputation", progress.reputation)
        .putInt("room", progress.roomLevel)
        .putInt("served", progress.servedToday)
        .putInt("story", progress.storyChapter)
        .putString("cats", progress.cats.joinToString(";") { "${it.name}|${it.breed}|${it.mood}|${it.level}" })
        .putStringSet("claimed", progress.claimedGoals.toSet())
        .apply()
}
