/*
 * Copyright (c) 2015 Huajian Jiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.expandablerecyclerview.widget;

import android.view.View;


/**
 * <p>
 * 子列表项 ChildViewHolder ，用于实现子列表项视图的创建
 * 客户端 ChildViewHolder 应该实现该类实现可扩展的 {@code RecyclerView}
 */
public class ChildViewHolder<C> extends BaseExpandableViewHolder
{
    private static final String TAG = "ChildViewHolder";

    private int parentPosition;

    private int childPosition;

    private C child;

    public ChildViewHolder(View itemView) {
        super(itemView);
    }

    public int getParentPosition() {
        return parentPosition;
    }

    void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    public int getChildPosition() {
        return childPosition;
    }

    void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    public C getChild() {
        return child;
    }

    void setChild(C child) {
        this.child = child;
    }
}
