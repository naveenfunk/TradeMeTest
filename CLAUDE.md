# Project Instructions

## Project Context
This is an Android application utilizing a multi-module architecture, built natively in Android Studio using modern Android development practices. The app features three main tabs ("Discover", "Watchlist", "My Trade Me") and focuses on displaying categorized items (Auctions vs. Classifieds).

## Core Tech Stack
*   **Language:** Kotlin (Latest stable)
*   **UI Toolkit:** Jetpack Compose (Material 3)
*   **Architecture:** Clean Architecture + MVI (Model-View-Intent) following SOLID principles
*   **Asynchronous Programming:** Kotlin Coroutines & Flows
*   **Networking:** Retrofit (with OkHttp)
*   **Local Storage:** Room Database
*   **Dependency Injection:** Hilt
*   **Navigation:** Jetpack Compose Navigation

## Strict Architectural Rules

### 1. Multi-Module Enforcements
*   **Separation of Concerns:** Feature modules (`feature-discover`, `feature-watchlist`, `feature-profile`) must NEVER depend on each other.
*   **Core Modules:** Shared UI components and theming must reside in `core-theme` or a designated `core-ui` module.
*   **Data & Domain:** The UI layer must only interact with the Domain layer (UseCases). The Data layer (Retrofit/Room) is strictly isolated and accessed via interfaces defined in the Domain layer.

### 2. UI & Compose Rules (MVI)
*   **MVI Enforcement:** ViewModels must expose a *single* `StateFlow` representing the UI state.
*   **Intent Driven:** User actions must be passed to the ViewModel as Intents (e.g., sealed class `DiscoverIntent`).
*   **No UI Business Logic:** Composables must be purely functional and stateless where possible. All formatting logic (e.g., determining which price to show based on `IsClassified`) must be calculated in the Domain or ViewModel layer, not within the Composable.
*   **Use strings.xml:** Always define all UI labels in strings.xml. Never hardcode any UI labels in their .kt files except in previews.

### 3. Theming Design Tokens
Adhere strictly to the defined light theme palette. Do not invent colors.
*   Primary (Tasman 500): `#148FE2`
*   Secondary (Feijoa 500): `#29A754`
*   Text Dark (Bluff Oyster 800): `#393531`
*   Text Light (Bluff Oyster 600): `#85807B` (Also used as the list divider color)
*   Background: White

### 4. Testing Requirements
*   **Domain/Presentation:** ViewModels and UseCases must have comprehensive Unit Tests using JUnit and MockK/Mockito. Focus on testing state reductions and logic formatting (Auction vs. Classified pricing).
*   **UI Tests:** Write Compose UI tests ensuring items render correctly, independent action bars display correctly, and clicks (Search/Cart) trigger the correct intents.

## Agent Operating Guidelines
1.  **Analyze First:** Always review existing code in the skeleton app before generating new files or modifying architecture.
3.  **No Deprecated Code:** Do not use XML layouts, `LiveData`, `AsyncTask`, or MVC/MVP patterns.
4.  **Step-by-Step Execution:** Follow the iterative steps provided by the user. Do not combine the UI implementation step with the API implementation step.
