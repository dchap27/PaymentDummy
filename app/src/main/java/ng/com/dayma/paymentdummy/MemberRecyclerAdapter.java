package ng.com.dayma.paymentdummy;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ng.com.dayma.paymentdummy.data.PaymentProviderContract;

import static ng.com.dayma.paymentdummy.utils.ValidateTextInput.validateTextInput;

public class MemberRecyclerAdapter extends RecyclerView.Adapter<MemberRecyclerAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private Cursor mCursor;
    private int mChandaNoPos;
    private int mMemberNamePos;
    private int mMemberIdPos;
    private int mMemberJamaat;
    private TextView mEditButton;
    private TextView mCancelButton;
    private AlertDialog mEditDialog;
    private EditText mChandaNoInput;
    private EditText mMemberName;
    private EditText mMemberjamaat;

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
        mMemberJamaat = mCursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME);
    }

    @Override
    public MemberRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_member_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(MemberRecyclerAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final int chandaNo = mCursor.getInt(mChandaNoPos);
        final String fullName = mCursor.getString(mMemberNamePos);
        final int memberId = mCursor.getInt(mMemberIdPos);
        final String memberJamaat = mCursor.getString(mMemberJamaat);
        holder.mChandaNoText.setText(String.valueOf(chandaNo));
        holder.mMemberNameText.setText(fullName);
        holder.mEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSelectedMemberData(fullName, memberId, chandaNo, memberJamaat);
            }
        });
    }

    private void editMemberData(int chandaNo, String name, String jamaat) {
        View dialogView = initialPopupDisplay();
        mChandaNoInput = (EditText) dialogView.findViewById(R.id.input_text_chandaNo);
        mMemberName = (EditText) dialogView.findViewById(R.id.input_text_memberName);
        mMemberjamaat = (EditText) dialogView.findViewById(R.id.input_text_jamaatName);
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.text_add_member_title);
        dialogTitle.setText("Edit Member Data");
        mChandaNoInput.setText(String.valueOf(chandaNo));
        mChandaNoInput.setFocusable(false);
        mMemberName.setText(name);
        mMemberjamaat.setText(jamaat);
        mEditButton = (TextView) dialogView.findViewById(R.id.text_add_button);
        mCancelButton = (TextView) dialogView.findViewById(R.id.text_cancel_addition);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(dialogView);
        mEditDialog = alertDialogBuilder.create();
        mEditDialog.show();

    }

    private View initialPopupDisplay() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View addDialogView = layoutInflater.inflate(R.layout.add_member_input_layout, null);
        return addDialogView;
    }


    private void editData(int memberItemId, String chandaNo, String name, String jamaat) {
        final ContentValues values = new ContentValues();
        values.put(PaymentProviderContract.Members.COLUMN_MEMBER_CHANDANO, chandaNo);
        values.put(PaymentProviderContract.Members.COLUMN_MEMBER_ID, (chandaNo) + " - " + name);
        values.put(PaymentProviderContract.Members.COLUMN_MEMBER_FULLNAME, name);
        values.put(PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME, jamaat);
        Uri memberUri = ContentUris.withAppendedId(PaymentProviderContract.Members.CONTENT_URI, memberItemId);
        mContext.getContentResolver().update(memberUri, values, null, null);
        notifyDataSetChanged();
    }

    private void editSelectedMemberData(final String fullName,
                                        final int id, final int chandaNo, final String jamaatName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(
                "Are you sure you want to edit "+ fullName + "?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                editMemberData(chandaNo, fullName, jamaatName);
                mEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processInputsData(id);
                        mEditDialog.cancel();
                        Toast.makeText(mContext,
                                fullName + " edited successfully.", Toast.LENGTH_LONG).show();
                    }
                });
                mCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditDialog.cancel();
                    }
                });
            }
        });
        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void processInputsData(int memberId) {
        final EditText[] textInputs = {mChandaNoInput, mMemberName, mMemberjamaat};
        final String memberfullName = mMemberName.getText().toString().trim();
        final String jamaatName = mMemberjamaat.getText().toString().trim().toUpperCase();
        final String chandaNo = mChandaNoInput.getText().toString().trim();
        if(validateTextInput(textInputs))
            editData(memberId, chandaNo, memberfullName, jamaatName);
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
        private final ImageView mEditIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mChandaNoText = (TextView) itemView.findViewById(R.id.textView_member_chandno);
            mMemberNameText = (TextView) itemView.findViewById(R.id.textView_member_fullname);
            mEditIcon = (ImageView) itemView.findViewById(R.id.image_member_edit_icon);

        }
    }
}
