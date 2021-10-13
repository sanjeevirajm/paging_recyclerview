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

package com.example.recyclersample.flowerList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclersample.R
import com.example.recyclersample.data.Flower
import kotlinx.coroutines.CoroutineScope

class FlowersAdapter(
    context: Context,
    scope: CoroutineScope,
    private val loadNextPage: (position: Int) -> Unit
) : PagedAdapter<Flower>(context = context,
        coroutineScope = scope,
        diffCallback = FlowerDiffCallback,
        prefetchTriggerCount = 5
    ) {

    override fun onCreateViewHolderDelegate(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.flower_item, parent, false)
        return FlowerViewHolder(view)
    }

    override fun onBindViewHolderDelegate(holder: RecyclerView.ViewHolder, position: Int) {
        val flower = getItem(position)
        (holder as FlowerViewHolder).bind(flower)
    }

    override fun getViewTypeForPosition(position: Int): Int {
        return 1
    }

    override fun loadMore(position: Int) {
        loadNextPage(position)
    }


    class FlowerViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val flowerTextView: TextView = itemView.findViewById(R.id.flower_text)
        private val flowerImageView: ImageView = itemView.findViewById(R.id.flower_image)
        private var currentFlower: Flower? = null

        fun bind(flower: Flower) {
            currentFlower = flower

            flowerTextView.text = flower.name
            if (flower.image != null) {
                flowerImageView.setImageResource(flower.image)
            } else {
                flowerImageView.setImageResource(R.drawable.rose)
            }
        }
    }
}

object FlowerDiffCallback : DiffUtil.ItemCallback<Flower>() {
    override fun areItemsTheSame(oldItem: Flower, newItem: Flower): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Flower, newItem: Flower): Boolean {
        return oldItem.id == newItem.id
    }
}