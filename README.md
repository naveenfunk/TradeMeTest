# Trade Me Test

A native Android app for browsing Trade Me listings, built with Kotlin, Jetpack Compose, and a
multi-module Clean Architecture + MVI setup. The app has three tabs — **Discover**, **Watchlist**,
and **My Trade Me** — of which **Discover** is the fully implemented feature; Watchlist and My
Trade Me are placeholder screens.

Read [Notes for reviewers](#notes-for-reviewers) below before you go looking for the Buy Now
price in the running app — there are a few things about it that aren't obvious from a first run.

## Getting started

1. Create `secrets.properties` in the project root (it's gitignored, so it won't already exist
   after cloning):

   ```properties
   TRADEME_CONSUMER_KEY=your_key_here
   TRADEME_CONSUMER_SECRET=your_secret_here
   ```

2. Build and run:

   ```
   ./gradlew assembleDebug
   ```

## Architecture

Clean Architecture (UI → Domain → Data) + MVI, split across 8 Gradle modules:

```
:app
├── :core-theme
├── :core-navigation
├── :feature-discover ──┐
├── :feature-watchlist ─┤
└── :feature-profile ───┘
                        │
        each feature depends on:
          :core-theme      (api)
          :core-navigation (api)
          :core-ui         (impl)  ──▶ :core-theme (api)
          :core-network    (impl)  ── feature-discover only
```

| Module | Role |
|---|---|
| `:app` | `Application`/`Activity`, adaptive bottom-tab navigation, wires the three feature graphs |
| `:core-theme` | Design tokens, `MaterialTheme` (colors, typography) — no project dependencies |
| `:core-ui` | Shared stateless composables (e.g. `TradeMeTopAppBar`) |
| `:core-navigation` | Type-safe `@Serializable` route contracts shared by `:app` and all features |
| `:core-network` | Retrofit/OkHttp/Hilt plumbing, OAuth signing, base URL/credentials from `BuildConfig` |
| `:feature-discover` | The implemented feature: listings list with Clean Architecture + MVI |
| `:feature-watchlist` | Placeholder screen |
| `:feature-profile` | Placeholder screen ("My Trade Me" tab) |

## Notes for reviewers

### 1. Seeing Buy Now prices requires a one-line DI swap

The app talks to the real Trade Me sandbox API by default, and (see note 2 below) that endpoint
never actually returns a Buy Now price. To see the Buy Now UI rendered, swap the repository
binding in `feature-discover/src/main/java/nz/co/trademetest/feature/discover/di/DiscoverModule.kt`:

```kotlin
// Before (talks to the real sandbox API):
@Binds
fun bindDiscoverRepository(networkDiscoverRepository: NetworkDiscoverRepository): DiscoverRepository

// After (serves 8 hardcoded items, 4 of which have a Buy Now price):
@Binds
fun bindDiscoverRepository(fakeDiscoverRepository: FakeDiscoverRepository): DiscoverRepository
```

### 2. Buy Now price is modelled but never populated by the sandbox API

The requirement is that a Buy Now price should show whenever the API returns one. That path is
fully implemented and unit-tested end to end, but the sandbox `listings/latest.json` response
never actually includes a `BuyNowPrice` value, so it's never visible against live data:
