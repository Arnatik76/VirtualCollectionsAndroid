# Virtual Collections App (Android Source Code)

This repository contains the source code of the Android application "Virtual Collections". The application allows users to create, manage, and share collections of various media items (such as movies, books, games, etc.).

## Project Description

Virtual Collections is a platform for organizing personal collections and displaying them. Users can:
* Register and log in.
* Create public or private collections.
* Add media elements to your collections by specifying details (title, creator, description, cover, links, release date, content type).
* View other users' public collections.
* Manage your collections and profile.
* Leave comments and like collections.
* Search for media items to add.

## Key features (implemented in the Android version)

* **Authentication:**
    * Registration of a new user.
    * Log in by email and password.
    * Password recovery (email request).
    * Log out of the system.
* **Collection Management:**
* Creation of a new collection (title, description, cover URL, publicity).
    * View the list of public collections (home page).
    * View the list of collections of the current user ("My Collections").
    * View collection details (cover, information, item list, comments, collaborators, statistics).
    * Adding media items to a collection (with the ability to create a new media item on the fly).
    * Likes/unlinks of collections.
    * Adding comments to collections.
    * Editing collections.
* **Media Element Management:**
* View the details of a media element (cover, title, creator, type, description, release date, date added, external link, tags).
    * Editing media elements.
    * Search for media elements.
* **User Profile Management:**
    * View user profile (avatar, username, display name, email, bio, registration date, last login date).
    * Profile editing (display name, bio, avatar URL).
* **Interface:**
* Bottom navigation (Home, My Collections, Profile).
    * Support for light and dark themes.
    * Using Material Components.

## Technology Stack (Current Android Version)

* **Language:** Kotlin
* **Architecture:** Android Fragments.
*   **UI:**
* XML for layout layout.
    *   Material Components for Android.
    * RecyclerView for displaying lists.
    * Navigation Component for navigating between screens.
    * Glide to download images.
* **Network:**
* Retrofit 2 for making HTTP API requests.
    * OkHttp 3 (including HttpLoggingInterceptor) as an HTTP client.
    * Gson for JSON serialization/deserialization.
* **Local storage:**
* SharedPreferences for storing the authentication token and user data.
* **Build:** Gradle

## Android Project Structure

* `app/src/main/java/com/example/finalproject/`: Kotlin source code.
    * `api/`: Classes for working with API (RetrofitClient, ApiService, AuthInterceptor).
    * `adapters/`: Adapters for RecyclerView (CollectionAdapter, CommentAdapter, MediaItemAdapter, etc.).
    * `fragments/`: Fragments representing different application screens (grouped by `collection`, `mediaitem', `user`).
    * `models/`: Data classes for entities (Collection, User, MediaItem, etc.) and API requests/responses.
    * `utils/`: Auxiliary classes (AuthTokenProvider).
    * `MainActivity.kt': The main activity of the application.
* `app/src/main/res/`: Application resources.
    * `layout/`: XML layout files for activities and fragments.
    * `drawable/`: Images and vector graphics.
    * `values/`: Strings, colors, styles, themes.
    * `navigation/`: Navigation graph (`nav_graph.xml `).
    * `menu/`: A menu (for example, for the BottomNavigationView).
* `AndroidManifest.xml `: Application manifest.

## API Endpoints

The application interacts with the backend in the following main groups of endpoints:

* **Authentication:** `/vc-users/auth/` (login, register, forgotten-password)
* **Users:** `/vc-users/users/` (getting a profile, updating your profile)
* **Collections:** `/vc-collections/collections/` (create, receive, like, comment, add items, etc.)
* **Media elements:** `/vc-content/media-items/` (create, receive, update, search, content types)

A detailed description of all the endpoints can be found in the file `app/src/main/java/com/example/finalproject/api/ApiService.kt'.

## Design themes

The project supports both light and dark themes. The definitions of the topics are in:
* `res/values/themes.xml ` (light theme "Aurora Day")
* `res/values-night/themes.xml ` (dark theme of "Aurora Night")

## How to launch this Android project

1. Make sure that you have the latest version of Android Studio installed.
2. Clone this repository (frontend).
3. **Clone and run the backend service:** Instructions for starting the server part are in its repository: [VirtualCollectionsMicroservice](https://github.com/Arnatik76/VirtualCollectionsMicroservice ). Make sure that it is running and available at `http://10.0.2.2:8765 /` (this is the standard IP address for accessing the `localhost` of the host machine from the Android emulator).
4. Open the Android application project in Android Studio.
5. Wait for Gradle to sync.
6. Launch the application on the emulator or connected device.
## Backend / Server part

The server part for this application is also available on GitHub. She is responsible for all business logic, database management, and provides an API for client applications.
**Link to the backend repository:** [VirtualCollectionsMicroservice](https://github.com/Arnatik76/VirtualCollectionsMicroservice )

The API used by this Android application is provided by this backend. Base URL: `http://10.0.2.2:8765 /` (for accessing localhost from the Android emulator).
