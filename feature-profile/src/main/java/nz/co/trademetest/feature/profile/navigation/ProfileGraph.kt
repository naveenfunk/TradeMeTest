package nz.co.trademetest.feature.profile.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import nz.co.trademetest.core.navigation.ProfileGraphRoute
import nz.co.trademetest.core.navigation.ProfileHome
import nz.co.trademetest.feature.profile.ProfileScreen

fun NavGraphBuilder.profileGraph(
    contentPadding: PaddingValues,
) {
    navigation<ProfileGraphRoute>(startDestination = ProfileHome) {
        composable<ProfileHome> {
            ProfileScreen(contentPadding = contentPadding)
        }
    }
}
