package com.CSCE4901.Mint.Search;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.CSCE4901.Mint.R;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<SearchItem> mItems;
    private Context mContext;
    FirebaseAuth firebaseAuth; //auth decleration
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database

    String TITLE;
    String CAT;
    String DES;
    String FLAG;

    int POS;
    //String DATE;
    boolean editCHECK=false;

    public SearchAdapter(ArrayList itemList) {
        //mAdapter = adapter;
        mItems = itemList;
    }

    public void updatedList(ArrayList<SearchItem> mItems)
    {
        mItems.clear();
        mItems.addAll(mItems);
        notifyDataSetChanged();
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

        SharedPreferences.Editor editor = mContext.getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
        editor.putInt("CHECK",0); //set as false aka not finished
        editor.apply();

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
               //Toast.makeText(mContext, String.format("%d", position + 1), Toast.LENGTH_SHORT).show();
            }
        });

        holder.mOPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gone options button
                holder.mOPT.setVisibility(View.INVISIBLE);
                //variables visible XML
                holder.mEdit.setVisibility(View.VISIBLE);
                holder.mDel.setVisibility(View.VISIBLE);
                holder.mX.setVisibility(View.VISIBLE);


            }

            });

        holder.mX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables gone XML
                holder.mEdit.setVisibility(View.INVISIBLE);
                holder.mDel.setVisibility(View.INVISIBLE);
                holder.mX.setVisibility(View.INVISIBLE);


                //visible options button
                holder.mOPT.setVisibility(View.VISIBLE);

                //updatedList(mItems);
            }
        });

        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize Firebase Auth instance
                firebaseAuth = FirebaseAuth.getInstance();

                POS=position;
                editCHECK = true;

                //get email of signed in user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();

                final String ID= mItems.get(position).id;
                final String date = mItems.get(position).date;


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
                /*Intent goIntent = new Intent(mContext, update_entry.class);
                goIntent.putExtra("key", data); //send data hashMap
                mContext.startActivity(goIntent);*/

                //go(position,data);

                SharedPreferences pref = mContext.getSharedPreferences("PreferencesName", MODE_PRIVATE);
                int CHECK = pref.getInt("CHECK", 0);

                while(CHECK==0) {
                    Toast.makeText(mContext, String.format("its 0"), Toast.LENGTH_SHORT).show();
                    if(CHECK==1) {
                        Toast.makeText(mContext, String.format("its 1"), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    CHECK=pref.getInt("CHECK",0);
                }

                //mAdapter.notifyDataSetChanged();
                //int check = go(position);
                /*SharedPreferences pref = mContext.getSharedPreferences("PreferencesName", MODE_PRIVATE);
                int CHECK=pref.getInt("CHECK",0);//
                while(check!=CHECK)
                {
                    Toast.makeText(mContext, String.format("DIFF"), Toast.LENGTH_SHORT).show();
                    if(check==CHECK) {
                        Toast.makeText(mContext, String.format("SAME"), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    CHECK=pref.getInt("CHECK",0);
                }*/


/*
                //Intent newIntent = new Intent(SearchAdapter.this, );
                Intent goIntent = new Intent(mContext, update_entry.class);
                goIntent.putExtra("key", data); //send data hashMap
                mContext.startActivity(goIntent);

                */
                    /*Toast.makeText(mContext, String.format("CF: "+checkFinish), Toast.LENGTH_SHORT).show();
                    if(checkFinish==1) {
                            Toast.makeText(mContext, String.format("CF::" + checkFinish), Toast.LENGTH_SHORT).show();

                        }
                    else {
                        mContext.startActivity(goIntent);
                        checkFinish = prefs.getInt("CHECK",0); //get int
                    }*/


                /*Toast.makeText(mContext, String.format(""+UserEmail), Toast.LENGTH_SHORT).show();
                DocumentReference DOC = db.collection(UserEmail).document(ID);
                DOC.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    //If the user is able to get data from the database
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        //Check if the document exists in the database
                        final String DATE = document.getString("date");
                        if (DATE.equals(date)) {
                            Toast.makeText(mContext, String.format("" + DATE), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, String.format(""+DATE+" NOT THE SAME DATE "+date), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

                //Toast.makeText(mContext, String.format(""+DATE), Toast.LENGTH_SHORT).show();


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

                //remove from algolia
                Client client = new Client("SPV08Z7AV0", "adee0fbb15896a566a5ac1a39e322bb4");
                assert UserEmail != null;
                Index index = client.getIndex(UserEmail);
                index.deleteObjectAsync(ID, null);

                //remove from recycler
                mItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mItems.size());

                //delete entry
                db.collection(UserEmail).document(ID).delete();

                Toast.makeText(mContext, String.format("Deleting Entry"), Toast.LENGTH_SHORT).show();
            }
        });

        /*int CHECK=go(position);
        while(CHECK!=1)
        {
            if(CHECK==1)
            {
                Toast.makeText(mContext, String.format("CHECK: "+CHECK), Toast.LENGTH_SHORT).show();
            }
            else {
                CHECK=go(position);
            }
        }*/

    }


    /*public void go(int POS, HashMap<String, String> data) {

        SharedPreferences.Editor pref = mContext.getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
        pref.putInt("CHECK", 0);

        Intent goIntent = new Intent(mContext, update_entry.class);
        goIntent.putExtra("key", data); //send data hashMap
        mContext.startActivity(goIntent);

        SharedPreferences pref = mContext.getSharedPreferences("PreferencesName", MODE_PRIVATE);
        int CHECK = pref.getInt("CHECK", 0);//
        //Toast.makeText(mContext, String.format("CHECK:  "+CHECK), Toast.LENGTH_SHORT).show();

        SearchAdapter.this.notifyDataSetChanged();

    }*/

}
