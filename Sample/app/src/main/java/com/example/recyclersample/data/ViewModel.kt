/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.recyclersample.data

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class ViewModel(private val context: Context,
                private val scope: CoroutineScope) {

    private val screenState = MutableLiveData(
            ScreenState(
                list = flowerList(context.resources),
                hasNext = true
            )
    )

    val screenStateImmutable: LiveData<ScreenState>
    get() = screenState

    private val currentScreenState: ScreenState
        get() = screenState.value!!

    private val currentList: List<Flower>
    get() = currentScreenState.list

    /* Returns a random flower asset for flowers that are added. */
    private fun getRandomFlowerImageAsset(): Int? {
        val randomNumber = (currentList.indices).random()
        return currentList[randomNumber].image
    }

    private var job: Job? = null
    fun loadMoreFlowers() {
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            delay(2_000L)
            insertFlowers()
        }
    }

    fun insertFlowers() {
        val updatedList = currentList.toMutableList()

        repeat(10) {
            val image = getRandomFlowerImageAsset()
            val newFlower = Flower(
                updatedList.size.toLong(),
                "Flower ${updatedList.size}",
                image,
            )
            updatedList.add(newFlower)
        }

        screenState.postValue(currentScreenState.copy(
            list = updatedList
        ))
    }
}