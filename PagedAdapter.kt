import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import paging.utils.*
import kotlinx.coroutines.*

abstract class PagedAdapter<T>(
    val context: Context,
    private val coroutineScope: CoroutineScope,
    private val pagedDiffCallback: PagedDiffCallback<T>,
    private var prefetchTriggerCount: Int = 25): Recycler.Adapter<ViewHolder>() {

    companion object {
        const val TYPE_LOADER = 55555
    }

    abstract fun getViewTypeForPosition(position: Int): Int
    abstract fun loadMore(position: Int)
    abstract fun onCreateViewHolderDelegate(parent: ViewGroup, viewType: Int): ViewHolder
    abstract fun onBindViewHolderDelegate(holder: GeneralViewHolder, position: Int)

    private var hasNextPage: Boolean = false
    private var calledLoadMorePosition = -1

    fun checkAndCallLoadMore(position: Int, skipCheck: Boolean = false) {
        log = "skipCheck: $skipCheck, calledLoadMorePosition: $calledLoadMorePosition, position: $position"
        if (skipCheck || calledLoadMorePosition != position) {
            if (NetworkUtils.isNetAvailable) {
                calledLoadMorePosition = position
                loadMore(position)
            } else {
                context.toast(R.string.no_internet_connection)
            }
        }
    }

    final override fun getItemCount(): Int {
        return if (hasNextPage) {
            list.size + 1
        } else {
            list.size
        }
    }

    final override fun getItemViewType(position: Int): Int {
        if (hasNextPage) {
            if (position == list.size) {
                return TYPE_LOADER
            }
        }
        return getViewTypeForPosition(position)
    }

    open fun getLoadingViewHolder(parent: ViewGroup, context: Context): LoadingViewHolder {
        val convertView = LayoutInflater.from(context).inflate(R.layout.loading_view, parent, false)
        return LoadingViewHolder(convertView, context, this)
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralViewHolder {
        log = "onCreateViewHolder: $viewType"
        return if (viewType == TYPE_LOADER) {
            getLoadingViewHolder(parent, context)
        } else {
            onCreateViewHolderDelegate(parent, viewType)
        }
    }

    final override fun onBindViewHolder(holder: GeneralViewHolder, position: Int) {
        log = "onBindViewHolder: ${holder::javaClass.name}, hasNextPage:$hasNextPage, position: $position, itemCount - (prefetchTriggerCount + 1): ${itemCount - (prefetchTriggerCount + 1)}, itemCount: $itemCount"
        if (holder is LoadingViewHolder) {
            holder.onBind()
        } else {
            //Note: itemCount means (no of items + loader item)
            //25 >= 51 - (25 + 1)
            if (hasNextPage && position >= itemCount - (prefetchTriggerCount + 1)) {
                checkAndCallLoadMore(itemCount - 1)
            }
            onBindViewHolderDelegate(holder, position)
        }
    }

    private var list = emptyList<T>()
    private var diffJob: Job? = null

    fun getItem(position: Int): T {
        return list[position]
    }

    fun setHashNext(hasNext: Boolean) {
        updateList(list, hasNext)
    }

    fun updateList(newList: List<T>, hasNextPageNewValue: Boolean) {
        val updatedList: List<T> = ArrayList(newList)
        diffJob?.cancel()
        diffJob = coroutineScope.launch(Dispatchers.Default) {
            val diffCallbackWithLoader: DiffUtil.Callback = object: DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return itemCount
                }

                override fun getNewListSize(): Int {
                    return updatedList.size + if (hasNextPageNewValue) 1 else 0
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val isOldPositionALoader = oldItemPosition == list.size
                    val isNewPositionALoader = newItemPosition == updatedList.size

                    return if (isOldPositionALoader && isNewPositionALoader) {
                        return false // actually both are same, we should return true. But returning true will call notifyItemMoved which automatically scrolls to the last loader item. This isn't expected behaviour. So returning false to avoid it.
                    } else if (isOldPositionALoader || isNewPositionALoader) {
                        return false
                    } else {
                        pagedDiffCallback.areItemsTheSame(list[oldItemPosition], updatedList[newItemPosition])
                    }
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                    val isOldPositionALoader = oldItemPosition == list.size
//                    val isNewPositionALoader = newItemPosition == updatedList.size

//                    return if (isOldPositionALoader && isNewPositionALoader) {
//                        return false  // it won't happen since we have already returned false for this case in areItemsTheSame method
//                    } else if (isOldPositionALoader || isNewPositionALoader) {
//                        return false  // it won't happen since we have already returned false for this case in areItemsTheSame method
//                    }
                    return pagedDiffCallback.areContentsTheSame(list[oldItemPosition], updatedList[newItemPosition])
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//                    val isOldPositionALoader = oldItemPosition == list.size
//                    val isNewPositionALoader = newItemPosition == updatedList.size

                    //it won't happen since we have already returned false for this case in areItemsTheSame method
//                    return if (!isOldPositionALoader && !isNewPositionALoader) {
//                        pagedDiffCallback.getChangePayload(list[oldItemPosition], updatedList[newItemPosition])
//                    }
                    //it won't happen since we have already returned false for this case in areItemsTheSame method
//                    else if (isOldPositionALoader && isNewPositionALoader) {
//                        "dummy_payload_to_disable_fade_animation"
//                    }
                    return pagedDiffCallback.getChangePayload(list[oldItemPosition], updatedList[newItemPosition])
                }
            }
            val result = DiffUtil.calculateDiff(diffCallbackWithLoader)
            ensureActive()
            withContext(Dispatchers.Main) {
                list = updatedList
                hasNextPage = hasNextPageNewValue
                calledLoadMorePosition = -1
                result.dispatchUpdatesTo(this@PagedAdapter)
            }
        }
    }
}

abstract class PagedDiffCallback<T> {
    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    fun getChangePayload(oldItem: T, newItem: T): Any? {
        return null
    }
}

class LoadingViewHolder(itemView: View,
                        private val context: Context,
                        private val pagedAdapter: PagedAdapter<*>) : ViewHolder(itemView) {
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    private val retryTextView: AppCompatTextView = itemView.findViewById(R.id.textViewRetry)

    init
    {
        retryTextView.setOnClickListener {
            if (NetworkUtils.isNetAvailable())
            {
                retryTextView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                pagedAdapter.checkAndCallLoadMore(adapterPosition, skipCheck = true)
            }
            else
            {
                context.toast(R.string.no_internet_connection)
            }
        }
    }

    fun onBind() {
        if (NetworkUtils.isNetAvailable())
        {
            retryTextView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            pagedAdapter.checkAndCallLoadMore(adapterPosition)
        }
        else
        {
            retryTextView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            context.toast(R.string.no_internet_connection)
        }

        with(itemView.layoutParams) {
            height = if (pagedAdapter.itemCount == 1) {
                LinearLayout.LayoutParams.MATCH_PARENT
            } else {
                Util.convertDpToPixels(context, 60F)
            }
            itemView.layoutParams = this
        }
    }
}
