package ng.com.dayma.paymentdummy;

import android.os.Parcel;
import android.os.Parcelable;

public final class MemberInfo implements Parcelable {
    private String mJamaatname;
    private int mChandaNo;
    private String mFullname;
    private String mMemberId;
    private int mId;

    public MemberInfo(String jamaatName, int chandaNo, String fullname){
        mJamaatname = jamaatName;
        mChandaNo = chandaNo;
        mFullname = fullname;
        mMemberId = chandaNo + "- " + fullname;
    }

    public MemberInfo(int id, String jamaatname, int chandaNo, String fullname){
        mJamaatname= jamaatname;
        mChandaNo = chandaNo;
        mFullname = fullname;
        mMemberId = chandaNo + "- " + fullname;
        mId = id;
    }

    protected MemberInfo(Parcel source) {
        mJamaatname = source.readString();
        mMemberId = source.readString();
        mChandaNo = source.readInt();
        mFullname = source.readString();
    }

    public int getId(){
        return mId;
    }

    public String getMemberId() {
        return mMemberId;
    }

    public void setMemberId(){
        mMemberId = mChandaNo + "- " + mFullname;
    }

    public String getJamaatname(){
        return mJamaatname;
    }

    public int getChandaNo(){
        return mChandaNo;
    }


    public String getFullname(){
        return mFullname;
    }

    public void setJamaatname(String name){
        mJamaatname = name;
    }

    public void setChandaNo(int chandaNo){
        mChandaNo = chandaNo;
    }

    public void setFullname(String fullname){
        mFullname = fullname;
    }

    public static final Creator<MemberInfo> CREATOR = new Creator<MemberInfo>() {
        @Override
        public MemberInfo createFromParcel(Parcel source) {
            return new MemberInfo(source);
        }

        @Override
        public MemberInfo[] newArray(int size) {
            return new MemberInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mJamaatname);
        dest.writeInt(mChandaNo);
        dest.writeString(mFullname);
    }
}
