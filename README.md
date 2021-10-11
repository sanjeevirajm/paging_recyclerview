
# paging_recyclerview
A different approach for pagination in recyclerview

Pagination can be done in multiple ways in recycler view
1. onScrollListener and progress bar in the bottom

  We will add onScrollListerenr and it will check whether the user has scrolled to the last position.
  If the last position is reached, we will show the progress bar and start fetching next page.
  Concern: 
  *Placing progress bar in bottom is not a good idea. When the user scrolls to last position and scrolls again to top, the progress bar will overlap with recyclerview items. We can write code to handle it, but it will get complicated.

2. onScrollListener and progress bar item inside recyclerview.

  It's same as above. But the progress bar issue is avoided by adding progress bar as a item in recyclerview adapter.
  Concern: 
  *Adding another item in recyclerview adapter requires changes in multiple places. We need to make the adapter list nullable and add null check in multiple places. We will lose null safety. There is another approach to make it null safety. But it requires more changes.

3. Just Progress bar item inside RecyclerView

We will add null as the last item in adapter list. When onBind is called for progress item, we will start fetching next page.
It has the same concern mentioned above. Another new concern is that adding pre-fetching is tricky.

4. Android's paging library

The only disadvantage is it's super complex. It might be useful if the list is huge.

5. PagedAdapter

*It does pre fetching, and it can be customised. And the pre-fetching trigger technique is better than Android's Paging library.
*It uses DiffUtil, the diff runs in background
 *No internet connection case is handled with retry option
 *Progress bar will automatically align to the center of the screen if there is only one item on the screen
 *LoadingView can be customized
 *Duplicate loadMore call won't be made
 *How to use PagedAdapter? - Just extend PagedAdapter, IDE will ask to override some methods, do it. That's all.
 *Don't call notify methods like notifyDataSetChanged, notifyItemChanged, etc.. Instead use updateList() method.
Note: updateList() method should be called every time when response is received. No matter the response is success or failure.

