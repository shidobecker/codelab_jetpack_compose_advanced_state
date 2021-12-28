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

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.CraneEditableUserInput
import androidx.compose.samples.crane.base.CraneUserInput
import androidx.compose.samples.crane.base.rememberEditableUserInputState
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Invalid
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Valid
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

enum class PeopleUserInputAnimationState { Valid, Invalid }

class PeopleUserInputState {
    var people by mutableStateOf(1)
        private set

    val animationState: MutableTransitionState<PeopleUserInputAnimationState> =
        MutableTransitionState(Valid)

    fun addPerson() {
        people = (people % (MAX_PEOPLE + 1)) + 1
        updateAnimationState()
    }

    private fun updateAnimationState() {
        val newState =
            if (people > MAX_PEOPLE) Invalid
            else Valid

        if (animationState.currentState != newState) animationState.targetState = newState
    }
}

@Composable
fun PeopleUserInput(
    titleSuffix: String? = "",
    onPeopleChanged: (Int) -> Unit,
    peopleState: PeopleUserInputState = remember { PeopleUserInputState() }
) {
    Column {
        val transitionState = remember { peopleState.animationState }
        val tint = tintPeopleUserInput(transitionState)

        val people = peopleState.people
        CraneUserInput(
            text = if (people == 1) "$people Adult$titleSuffix" else "$people Adults$titleSuffix",
            vectorImageId = R.drawable.ic_person,
            tint = tint.value,
            onClick = {
                peopleState.addPerson()
                onPeopleChanged(peopleState.people)
            }
        )
        if (transitionState.targetState == Invalid) {
            Text(
                text = "Error: We don't support more than $MAX_PEOPLE people",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.secondary)
            )
        }
    }
}

@Composable
fun FromDestination() {
    CraneUserInput(text = "Seoul, South Korea", vectorImageId = R.drawable.ic_location)
}


/**
 * snapshotFlow
The code above is missing functionality to notify ToDestinationUserInput's caller when the input changes. Due to how the app is structured, we don't want to hoist the EditableUserInputState any higher up in the hierarchy because we want to couple the other composables such as FlySearchContent with this state. How can we call the onToDestinationChanged lambda from ToDestinationUserInput and still keep this composable reusable?

We can trigger a side-effect using LaunchedEffect every time the input changes and call the onToDestinationChanged lambda:
 */
@Composable
fun ToDestinationUserInput(onToDestinationChanged: (String) -> Unit) {
    val editableUserInputState = rememberEditableUserInputState(hint = "Choose Destination")

    CraneEditableUserInput(
        state =  editableUserInputState,
        caption = "To",
        vectorImageId = R.drawable.ic_plane,
    )

    /**
     * We've already used LaunchedEffect and rememberUpdatedState before, but the code above also uses a new API! We use the snapshotFlow API to convert Compose State<T> objects into a Flow. When the state read inside snapshotFlow mutates, the Flow will emit the new value to the collector. In our case, we convert the state into a flow to use the power of flow operators. With that, we filter when the text is not the hint, and collect the emitted items to notify the parent that the current destination changed.
     */
    val currentOnDestinationChanged by rememberUpdatedState(onToDestinationChanged)
    LaunchedEffect(editableUserInputState) {
        snapshotFlow { editableUserInputState.text }
            .filter { !editableUserInputState.isHint }.collect {
                currentOnDestinationChanged(editableUserInputState.text)
            }
    }
}

@Composable
fun DatesUserInput() {
    CraneUserInput(
        caption = "Select Dates",
        text = "",
        vectorImageId = R.drawable.ic_calendar
    )
}

@Composable
private fun tintPeopleUserInput(
    transitionState: MutableTransitionState<PeopleUserInputAnimationState>
): State<Color> {
    val validColor = MaterialTheme.colors.onSurface
    val invalidColor = MaterialTheme.colors.secondary

    val transition = updateTransition(transitionState, label = "")
    return transition.animateColor(
        transitionSpec = { tween(durationMillis = 300) }, label = ""
    ) {
        if (it == Valid) validColor else invalidColor
    }
}

@Preview
@Composable
fun PeopleUserInputPreview() {
    CraneTheme {
        PeopleUserInput(onPeopleChanged = {})
    }
}
