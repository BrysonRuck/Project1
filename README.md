# Project1 — Restaurant Discovery App

Project1 is an Android app that helps users discover restaurants near their saved location, keep track of favorites, and manage their profile preferences.

Users can create an account, log in, and remain logged in when they reopen the app. Each account has its own profile information, including a saved address and preferred search radius. Users can update that information later from the Profile tab.

## Features

- Create a new user account
- Log in with a username and password
- Keep the current user logged in between app launches
- Log out of the current account
- View restaurants near the logged-in user’s saved address
- Store a preferred search radius in the user profile
- Save and view favorite restaurants
- Update profile information, including username, address, password, and preferred radius
- Prevent duplicate usernames
- Display validation and error messages when account or profile information is invalid

## User Flow

1. When the app opens, it checks whether a user session is saved.
2. If no user is logged in, the app opens the Login screen.
3. A user can log in or create a new account.
4. After logging in, the app uses that user’s saved profile information throughout the app.
5. The user can view their profile, update preferences, view restaurants near their address, manage favorites, or log out.

## Technologies Used

- Kotlin
- Jetpack Compose
- Android Studio
- Room Database
- SharedPreferences for user-session persistence
- Navigation Compose
- OkHttp
- Foursquare Places API
- JUnit and Android instrumentation tests

## Local Setup

The app uses the Foursquare Places API to retrieve restaurant data. Before running the project, create or update the project-root `local.properties` file with your own API key:

```properties
FOURSQUARE_API_KEY=your_foursquare_api_key_here
