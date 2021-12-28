/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.compose.samples.crane.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.samples.crane.ui.captionTextStyle
import androidx.compose.ui.graphics.SolidColor

class EditableUserInputState(private val hint: String, initialText: String) {
    var text by mutableStateOf(initialText)

    val isHint: Boolean
        get() = text == hint


    /**
     * Creating a custom saver
    A Saver describes how an object can be converted into something which is Saveable. Implementations of a Saver need to override two functions:

    save to convert the original value to a saveable one.
    restore to convert the restored value to an instance of the original class.
    For our case, instead of creating a custom implementation of Saver for the EditableUserInputState class, we can use some of the existing Compose APIs such as listSaver or mapSaver (that stores the values to save in a List or Map) to reduce the amount of code that we need to write.

    It's a good practice to place Saver definitions close to the class they work with. Because it needs to be statically accessed, let's add the Saver for EditableUserInputState in a companion object. In the base/EditableUserInput.kt file, add the implementation of the Saver:
     */
    companion object {
        val Saver: Saver<EditableUserInputState, *> = listSaver(
            save = { listOf(it.hint, it.text) },
            restore = {
                EditableUserInputState(
                    hint = it[0],
                    initialText = it[1],
                )
            }
        )
    }
}

/**
 * State holders always need to be remembered in order to keep them in the Composition and not create a new one every time. It's a good practice to create a method in the same file that does this to remove boilerplate and avoid any mistakes that might occur. In the base/EditableUserInput.kt file, add this code
 */

@Composable
fun rememberEditableUserInputState(hint: String): EditableUserInputState =
    rememberSaveable(hint, saver = EditableUserInputState.Saver) {
        EditableUserInputState(hint, hint)
    }


/**
 * We're going to use EditableUserInputState instead of text and isHint, but we don't want to just use it as an internal state in CraneEditableUserInput as there's no way for the caller composable to control the state. Instead, we want to hoist EditableUserInputState so that callers can control the state of CraneEditableUserInput. If we hoist the state, then the composable can be used in previews and be tested more easily since you're able to modify its state from the caller.
 */
@Composable
fun CraneEditableUserInput(
    state: EditableUserInputState = rememberEditableUserInputState(""),
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null
) {

    CraneBaseUserInput(
        caption = caption,
        tintIcon = { !state.isHint },
        showCaption = { !state.isHint },
        vectorImageId = vectorImageId
    ) {
        BasicTextField(
            value = state.text,
            onValueChange = {
                 state.text = it
            },
            textStyle = if (state.isHint) {
                captionTextStyle.copy(color = LocalContentColor.current)
            } else {
                MaterialTheme.typography.body1.copy(color = LocalContentColor.current)
            },
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}
