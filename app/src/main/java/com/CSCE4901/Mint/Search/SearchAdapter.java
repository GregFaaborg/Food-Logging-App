package com.CSCE4901.Mint.Search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.CSCE4901.Mint.R;
import com.CSCE4901.Mint.update_entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<SearchItem> mItems;
    FirebaseAuth firebaseAuth; //auth decleration
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database

    String TITLE;
    String CAT;
    String DES;
    String FLAG;
    String DATE;

    Context mContext;
    public SearchAdapter(ArrayList itemList) {
        mItems = itemList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        mContext = parent.getContext();
        SearchViewHolder holder = new SearchViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {

        //get item and concatenize with their appropriate option
        String titleHolder="Title: "+mItems.get(position).title;
        holder.mTitle.setText(titleHolder);//holder.mTitle.setText(mItems.get(position).title);

        //holder.mCat.setText(mItems.get(position).category);
        String catHolder="Category: "+mItems.get(position).category;
        holder.mCat.setText(catHolder);

        String desHolder="Description:\n"+mItems.get(position).description;
        holder.mDescription.setText(desHolder);

        String dateHolder="Date: "+mItems.get(position).date;
        holder.mDate.setText(dateHolder);

        String FlagHolder = mItems.get(position).flag; //get flag string from database stored in FlagHolder

        //Toast.makeText(mContext, FlagHolder, Toast.LENGTH_SHORT).show();

        if(FlagHolder.equals("1")) {
            //MAKE FLAG STAR YELLOW
            holder.mFlag.setColorFilter(Color.parseColor("#CCCC00"));
        }
        else {
            //MAKE FLAG STAR TO NORMAL DEFAULT COLOR
            holder.mFlag.setColorFilter(Color.parseColor("#696969"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, String.format("%d", position + 1), Toast.LENGTH_SHORT).show();
            }
        });

        holder.mOPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gone options button
                holder.mOPT.setVisibility(View.GONE);
                //variables visible XML
                holder.mEdit.setVisibility(View.VISIBLE);
                holder.mDel.setVisibility(View.VISIBLE);
                holder.mX.setVisibility(View.VISIBLE);
                holder.mDelImage.setVisibility(View.VISIBLE);
                holder.mXImage.setVisibility(View.VISIBLE);
                holder.mEditImage.setVisibility(View.VISIBLE);

            }

            });

        holder.mX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables gone XML
                holder.mEdit.setVisibility(View.GONE);
                holder.mDel.setVisibility(View.GONE);
                holder.mX.setVisibility(View.GONE);
                holder.mDelImage.setVisibility(View.GONE);
                holder.mXImage.setVisibility(View.GONE);
                holder.mEditImage.setVisibility(View.GONE);

                //visible options button
                holder.mOPT.setVisibility(View.VISIBLE);
            }
        });

        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize Firebase Auth instance
                firebaseAuth = FirebaseAuth.getInstance();

                //get email of signed in user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();

                String ID= mItems.get(position).id;

                //get all the information in a HashMap
                HashMap<String, String> data = new HashMap<>();
                data.put("title", mItems.get(position).title);
                data.put("category", mItems.get(position).category);
                data.put("description", mItems.get(position).description);
                data.put("flag", mItems.get(position).flag);
                data.put("date", mItems.get(position).date);
                data.put("ID",mItems.get(position).id);
                data.put("email", UserEmail);


                //Intent newIntent = new Intent(SearchAdapter.this, );
                Intent goIntent = new Intent(mContext, update_entry.class);
                goIntent.putExtra("key", data); //send data hashMap
                mContext.startActivity(goIntent);
                /*
                DocumentReference updatedDoc = db.collection(UserEmail).document(ID);
                updatedDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    //If the user is able to get data from the database
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //Check if the document exists in the database
                        if (documentSnapshot.exists()) {
                            //get document fields
                            TITLE=documentSnapshot.getString("title");
                            CAT=documentSnapshot.getString("category");
                            DES=documentSnapshot.getString("description");
                            FLAG=documentSnapshot.getString("flag");
                            DATE=documentSnapshot.getString("date");
                            //output the data in the text views of the page
                            first.setText(FIRST);
                            last.setText(LAST);
                            docEmail.setText(docEMAIL);
                            emailDB.setText(email);
                        }
                    }
                });
                */
                //get Updated stuff
                /*String editTitle = "Title: "+mItems.get(position).title;
                holder.mTitle.setText(editTitle);
                String editCat = "Category: "+mItems.get(position).category;
                holder.mTitle.setText(editCat);
                String editDes = "Description:\n"+mItems.get(position).description;
                holder.mTitle.setText(editDes);
                String editDate = "Date: "+mItems.get(position).date;
                holder.mTitle.setText(editDate);

                String FlagHolder = mItems.get(position).flag; //get flag string from database stored in FlagHolder

                //Toast.makeText(mContext, FlagHolder, Toast.LENGTH_SHORT).show();

                if(FlagHolder.equals("1")) {
                    //MAKE FLAG STAR YELLOW
                    holder.mFlag.setColorFilter(Color.parseColor("#CCCC00"));
                }
                else {
                    //MAKE FLAG STAR TO NORMAL DEFAULT COLOR
                    holder.mFlag.setColorFilter(Color.parseColor("#696969"));
                }*/


            }
        });

        holder.mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize Firebase Auth instance
                firebaseAuth = FirebaseAuth.getInstance();

                //get email of signed in user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();

                String ID= mItems.get(position).id;

                //remove from recycler
                mItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mItems.size());

                //delete entry
                db.collection(UserEmail).document(ID).delete();

                Toast.makeText(mContext, String.format("Deleting Entry"), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
