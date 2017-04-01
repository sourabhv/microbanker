package com.metacode.mirobanker.ui.list;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metacode.mirobanker.R;
import com.metacode.mirobanker.data.model.ListItem;
import com.metacode.mirobanker.databinding.ActivityListBinding;

import timber.log.Timber;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding mBinding;
    private DatabaseReference mRef;
    private FirebaseRecyclerAdapter<ListItem, ListItemHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        mRef = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new FirebaseRecyclerAdapter<ListItem, ListItemHolder>(
                ListItem.class,
                R.layout.li_list_item,
                ListItemHolder.class,
                mRef.child("records")) {
            @Override
            protected void populateViewHolder(ListItemHolder viewHolder, ListItem model, int position) {
                Timber.d("Item: %s", model.toString());
                String key = this.getRef(position).getKey();
                model.setPhoneNumber(key);
                viewHolder.bind(model);
            }
        };

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(mAdapter);
    }
}
