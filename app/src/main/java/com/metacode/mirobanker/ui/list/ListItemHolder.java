package com.metacode.mirobanker.ui.list;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.metacode.mirobanker.R;
import com.metacode.mirobanker.data.model.ListItem;
import com.metacode.mirobanker.databinding.LiListItemBinding;

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
public class ListItemHolder extends RecyclerView.ViewHolder {

    private LiListItemBinding mBindings;

    public ListItemHolder(View itemView) {
        super(itemView);
        mBindings = DataBindingUtil.bind(itemView);
    }

    public void bind(ListItem listItem) {
        mBindings.setItem(listItem);
        Glide.with(itemView.getContext())
                .load(listItem.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(mBindings.image);
    }

}
