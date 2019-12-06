package ng.com.dayma.paymentdummy.touchhelpers;

public interface ScheduleClickAdapterListener {
    void onItemClicked(int adapterPosition, long cursorDataId);

    boolean onSingleClick();
}
