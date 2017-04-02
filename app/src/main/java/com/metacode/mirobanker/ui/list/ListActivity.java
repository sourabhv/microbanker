package com.metacode.mirobanker.ui.list;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.facebook.CustomTabMainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metacode.mirobanker.R;
import com.metacode.mirobanker.data.model.ListItem;
import com.metacode.mirobanker.databinding.ActivityListBinding;
import com.metacode.mirobanker.ui.auth.LoginActivity;
import com.metacode.mirobanker.util.Database;

import timber.log.Timber;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding mBinding;
    private DatabaseReference mRecordsRef;
    private FirebaseRecyclerAdapter<ListItem, ListItemHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        mRecordsRef = Database.get().getReference().child("records");
        mRecordsRef.keepSynced(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mBinding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_log_out:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    return true;
                default:
                    return false;
            }
        });

        mBinding.progressBar.setVisibility(View.VISIBLE);

        mAdapter = new FirebaseRecyclerAdapter<ListItem, ListItemHolder>(
                ListItem.class,
                R.layout.li_list_item,
                ListItemHolder.class,
                mRecordsRef) {
            @Override
            protected void onDataChanged() {
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.placeholder.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

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
        mBinding.addItem.setOnClickListener(v -> startActivity(new Intent(this, AddItemActivity.class)));
    }
}
