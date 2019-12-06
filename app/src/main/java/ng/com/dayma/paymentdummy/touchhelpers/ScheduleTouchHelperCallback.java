package ng.com.dayma.paymentdummy.touchhelpers;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class ScheduleTouchHelperCallback extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdapter mTouchHelperAdapter;

    public ScheduleTouchHelperCallback(ItemTouchHelperAdapter touchHelperAdapter) {
        mTouchHelperAdapter = touchHelperAdapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int swapFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0,swapFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("OnItem swiped", "on Item Swiped adapter");
        mTouchHelperAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }

    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
    }

}
