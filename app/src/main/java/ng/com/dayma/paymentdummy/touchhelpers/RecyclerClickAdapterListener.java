package ng.com.dayma.paymentdummy.touchhelpers;

public interface RecyclerClickAdapterListener {
    void onItemClicked(int adapterPosition, long cursorDataId);

    boolean onSingleClick();
}
