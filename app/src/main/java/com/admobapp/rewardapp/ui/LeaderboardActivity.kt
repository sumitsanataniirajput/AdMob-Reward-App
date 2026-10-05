package com.admobapp.rewardapp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.admobapp.rewardapp.R
import com.admobapp.rewardapp.data.model.User
import com.admobapp.rewardapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var leaderboardListView: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: Button
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        userRepository = UserRepository()

        // Initialize views
        leaderboardListView = findViewById(R.id.leaderboardListView)
        progressBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            val result = userRepository.getLeaderboard()
            progressBar.visibility = android.view.View.GONE

            result.onSuccess { users ->
                displayLeaderboard(users)
            }

            result.onFailure { error ->
                Toast.makeText(
                    this@LeaderboardActivity,
                    "Failed to load leaderboard: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayLeaderboard(users: List<User>) {
        val leaderboardItems = mutableListOf<String>()
        users.forEachIndexed { index, user ->
            val rankText = "#${index + 1} - ${user.username}"
            val pointsText = "Points: ${user.points}"
            val adsText = "Ads Watched: ${user.adsWatched}"
            leaderboardItems.add("$rankText | $pointsText | $adsText")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            leaderboardItems
        )
        leaderboardListView.adapter = adapter
    }
}
