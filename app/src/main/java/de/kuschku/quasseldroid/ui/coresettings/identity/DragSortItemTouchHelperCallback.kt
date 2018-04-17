package de.kuschku.quasseldroid.ui.coresettings.identity

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class DragSortItemTouchHelperCallback(private val adapter: IdentityNicksAdapter) :
  ItemTouchHelper.Callback() {
  override fun isLongPressDragEnabled() = true

  override fun isItemViewSwipeEnabled() = true

  override fun getMovementFlags(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder): Int {
    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
    return makeMovementFlags(dragFlags,
                             swipeFlags)
  }

  override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                      target: RecyclerView.ViewHolder): Boolean {
    adapter.moveNick(viewHolder.adapterPosition, target.adapterPosition)
    return true
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    adapter.removeNick(viewHolder.adapterPosition)
  }
}