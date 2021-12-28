/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.samples.crane.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

private const val SplashWaitTime: Long = 2000

/**
 * A side-effect in Compose is a change to the state of the app that happens outside the scope of a composable function. For example, opening a new screen when the user taps on a button, or showing a message when the app doesn't have Internet connection.
 */

/**
 * Changing the state to show/hide the landing screen will happen in the onTimeout callback and since before calling onTimeout we need to load things using coroutines, the state change needs to happen in the context of a coroutine!
 */

/**
 * When LaunchedEffect enters the Composition, it launches a coroutine with the block of code passed as a parameter. The coroutine will be cancelled if LaunchedEffect leaves the composition.
 */
@Composable
fun LandingScreen(modifier: Modifier = Modifier, onTimeout: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        /**
         * To trigger the side-effect only once during the lifecycle of this composable, use a constant as a key, for example LaunchedEffect(true) { ... }. However, we're not protecting against changes to onTimeout now!
         */
        // This will always refer to the latest onTimeout function that
        // LandingScreen was recomposed with
        val currentOnTimeout by rememberUpdatedState(onTimeout)

        // Create an effect that matches the lifecycle of LandingScreen.
        // If LandingScreen recomposes or onTimeout changes,
        // the delay shouldn't start again.
        LaunchedEffect(true) {
            delay(SplashWaitTime) // Simulates loading things
            currentOnTimeout()
        }

        Image(painterResource(id = R.drawable.ic_crane_drawer), contentDescription = null)
    }
}
