package com.github.expandablerecyclerview.widget;

interface InnerOnParentExpandCollapseListener {
    /**
     * 父列表项展开后的回调
     *
     * @param pvh 被展开的父列表
     */
    boolean onParentExpand(ParentViewHolder pvh);

    /**
     * 父列表项折叠后的回调
     *
     * @param pvh 被折叠的父列表
     */
    boolean onParentCollapse(ParentViewHolder pvh);
}
