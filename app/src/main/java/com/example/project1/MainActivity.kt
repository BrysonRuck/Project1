package com.example.project1

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var homeButton: TextView
    private lateinit var favoritesButton: TextView
    private lateinit var profileButton: TextView
    private lateinit var pageTitle: TextView
    private lateinit var pageSubtitle: TextView
    private lateinit var pageBody: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeButton = findViewById(R.id.homeButton)
        favoritesButton = findViewById(R.id.favoritesButton)
        profileButton = findViewById(R.id.profileButton)
        pageTitle = findViewById(R.id.pageTitle)
        pageSubtitle = findViewById(R.id.pageSubtitle)
        pageBody = findViewById(R.id.pageBody)

        homeButton.setOnClickListener { showPage("Home") }
        favoritesButton.setOnClickListener { showPage("Favorites") }
        profileButton.setOnClickListener { showPage("Profile") }

        showPage("Home")
    }

    private fun showPage(page: String) {
        pageTitle.text = page

        when (page) {
            "Favorites" -> {
                pageSubtitle.setText(R.string.favorites_subtitle)
                pageBody.setText(R.string.favorites_body)
            }
            "Profile" -> {
                pageSubtitle.setText(R.string.profile_subtitle)
                pageBody.setText(R.string.profile_body)
            }
            else -> {
                pageSubtitle.setText(R.string.home_subtitle)
                pageBody.setText(R.string.home_body)
            }
        }

        setSelected(homeButton, page == "Home")
        setSelected(favoritesButton, page == "Favorites")
        setSelected(profileButton, page == "Profile")
    }

    private fun setSelected(button: TextView, selected: Boolean) {
        val selectedColor = getColor(R.color.nav_selected)
        val defaultColor = getColor(R.color.nav_default)
        val color = if (selected) selectedColor else defaultColor

        button.isSelected = selected
        button.setTextColor(color)
        button.compoundDrawableTintList = ColorStateList.valueOf(color)
    }
}
