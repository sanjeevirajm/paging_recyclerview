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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclersample.R
import com.example.recyclersample.Util
import com.example.recyclersample.data.ViewModel

class FlowersListActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModel(this, lifecycleScope)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val flowersAdapter = FlowersAdapter(
            context = this,
            scope = lifecycleScope,
            loadNextPage = {
                viewModel.loadMoreFlowers()
            }
        )

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = flowersAdapter

        viewModel.screenStateImmutable.observe(this, { newScreenState ->
            flowersAdapter.updateList(newScreenState.list, newScreenState.hasNext)
        })

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            Util.toggleNetAvailability()
        }
    }
}