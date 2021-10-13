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

import android.content.res.Resources
import androidx.annotation.DrawableRes
import com.example.recyclersample.R

data class Flower(
    val id: Long,
    val name: String,
    @DrawableRes
    val image: Int?,
)

fun flowerList(resources: Resources): List<Flower> {
    return listOf(
        Flower(
            id = 1,
            name = resources.getString(R.string.flower1_name),
            image = R.drawable.rose,
        ),
        Flower(
            id = 2,
            name = resources.getString(R.string.flower2_name),
            image = R.drawable.freesia,
        ),
        Flower(
            id = 3,
            name = resources.getString(R.string.flower3_name),
            image = R.drawable.lily,
        ),
        Flower(
            id = 4,
            name = resources.getString(R.string.flower4_name),
            image = R.drawable.sunflower,
        ),
        Flower(
            id = 5,
            name = resources.getString(R.string.flower5_name),
            image = R.drawable.peony,
        ),
        Flower(
            id = 6,
            name = resources.getString(R.string.flower6_name),
            image = R.drawable.daisy,
        ),
        Flower(
            id = 7,
            name = resources.getString(R.string.flower7_name),
            image = R.drawable.lilac,
        ),
        Flower(
            id = 8,
            name = resources.getString(R.string.flower8_name),
            image = R.drawable.marigold,
        ),
        Flower(
            id = 9,
            name = resources.getString(R.string.flower9_name),
            image = R.drawable.poppy,
        ),
        Flower(
            id = 10,
            name = resources.getString(R.string.flower10_name),
            image = R.drawable.daffodil,
        ),
        Flower(
            id = 11,
            name = resources.getString(R.string.flower11_name),
            image = R.drawable.dahlia,
        )
    )
}