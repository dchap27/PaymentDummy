package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.com.dayma.paymentdummy.data.PaymentProviderContract;

public class MemberRecyclerAdapter extends RecyclerView.Adapter<MemberRecyclerAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private Cursor mCursor;
    private int mChandaNoPos;
    private int mMemberNamePos;
    private int mMemberIdPos;
    private int mMemberIdComboPos;

    public MemberRecyclerAdapter(Context context, Cursor cursor){
        mCursor = cursor;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null)
            return;
        // Get column indexes from mCursor
        mChandaNoPos = mCursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_CHANDANO);
        mMemberNamePos = mCursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_FULLNAME);
        mMemberIdPos = mCursor.getColumnIndex(PaymentProviderContract.Members._ID);

        mMemberIdComboPos = mCursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_ID);
    }

    @Override
    public MemberRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_member_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(MemberRecyclerAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int chandaNo = mCursor.getInt(mChandaNoPos);
        String fullName = mCursor.getString(mMemberNamePos);
        String memberIdCombo = mCursor.getString(mMemberIdComboPos);
        holder.mChandaNoText.setText(String.valueOf(chandaNo));
        holder.mMemberNameText.setText(fullName);

    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mChandaNoText;
        private final TextView mMemberNameText;

        public ViewHolder(View itemView) {
            super(itemView);
            mChandaNoText = (TextView) itemView.findViewById(R.id.textView_member_chandno);
            mMemberNameText = (TextView) itemView.findViewById(R.id.textView_member_fullname);
        }
    }
}
