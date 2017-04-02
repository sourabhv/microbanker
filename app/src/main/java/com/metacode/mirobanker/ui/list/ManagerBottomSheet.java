package com.metacode.mirobanker.ui.list;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.metacode.mirobanker.R;
import com.metacode.mirobanker.data.model.ListItem;
import com.metacode.mirobanker.databinding.DialogManagerBottomSheetBinding;
import com.metacode.mirobanker.util.Database;


/*
 * Copyright 2017 Sourabh Verma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@SuppressWarnings("RestrictedApi")
public class ManagerBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }
        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private OnManagerSelectListener mOnManagerSelectListener;

    private DialogManagerBottomSheetBinding mBinding;
    private FirebaseRecyclerAdapter<ListItem.Manager, ManagerItemHolder> mAdapter;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_manager_bottom_sheet, null);
        dialog.setContentView(contentView);
        mBinding = DataBindingUtil.bind(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            ((BottomSheetBehavior) behavior).setPeekHeight(metrics.heightPixels * 3 / 4);
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        mBinding.progressBar.setVisibility(View.VISIBLE);

        mAdapter = new FirebaseRecyclerAdapter<ListItem.Manager, ManagerItemHolder>(
                ListItem.Manager.class,
                android.R.layout.simple_list_item_1,
                ManagerItemHolder.class,
                Database.get().getReference().child("managers")) {
            @Override
            protected void onDataChanged() {
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.placeholder.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            protected void populateViewHolder(ManagerItemHolder viewHolder, ListItem.Manager model, int position) {
                viewHolder.bind(model);
                viewHolder.itemView.setOnClickListener(v -> {
                    if (mOnManagerSelectListener != null) {
                        mOnManagerSelectListener.onManagerSelect(model);
                        dismissAllowingStateLoss();
                    }
                });
            }
        };

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public static class ManagerItemHolder extends RecyclerView.ViewHolder {

        private TextView mText;

        public ManagerItemHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(android.R.id.text1);
        }

        void bind(ListItem.Manager manager) {
            mText.setText(manager.getName());
        }

    }

    public void setOnManagerSelectListener(OnManagerSelectListener listener) {
        mOnManagerSelectListener = listener;
    }

    public interface OnManagerSelectListener {
        void onManagerSelect(ListItem.Manager manager);
    }

}
