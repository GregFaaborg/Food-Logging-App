package com.CSCE4901.Mint.Search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.CSCE4901.Mint.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {
    public TextView mTitle;
    public TextView mDate;
    public TextView mDescription;
    public ImageView mFlag;
    public TextView mCat;
    public ImageButton mEdit;
    public ImageButton mX;
    public ImageButton mDel;
    public Button mOPT;
    public ImageView mEditImage;
    public ImageView mXImage;
    public ImageView mDelImage;

    public SearchViewHolder(View itemView) {
        super(itemView);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mDate = (TextView) itemView.findViewById(R.id.date);
        mDescription = (TextView) itemView.findViewById(R.id.description);
        mFlag = (ImageView) itemView.findViewById(R.id.flag);
        mCat = (TextView) itemView.findViewById(R.id.cat);
        mEdit = (ImageButton) itemView.findViewById(R.id.edit);
        mX = (ImageButton) itemView.findViewById(R.id.x);
        mDel = (ImageButton) itemView.findViewById(R.id.del);
        mOPT = (Button) itemView.findViewById(R.id.options);
        mEditImage = (ImageView) itemView.findViewById(R.id.editImage);
        mXImage = (ImageView) itemView.findViewById(R.id.xImage);
        mDelImage = (ImageView) itemView.findViewById(R.id.delImage);

    }
}

