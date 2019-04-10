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
    public Button mEdit;
    public Button mX;
    public Button mDel;
    public Button mOPT;

    public SearchViewHolder(View itemView) {
        super(itemView);
        mTitle =  itemView.findViewById(R.id.title);
        mDate =  itemView.findViewById(R.id.date);
        mDescription =  itemView.findViewById(R.id.description);
        mFlag =  itemView.findViewById(R.id.flag);
        mCat = itemView.findViewById(R.id.cat);
        mEdit = itemView.findViewById(R.id.edit_entry);
        mX = itemView.findViewById(R.id.cancel_options);
        mDel = itemView.findViewById(R.id.delete_entry);
        mOPT = itemView.findViewById(R.id.options);

    }
}

