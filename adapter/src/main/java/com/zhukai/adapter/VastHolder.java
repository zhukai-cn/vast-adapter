package com.zhukai.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 通用适配器viewHolder
 *
 * @author zhukai
 */
public class VastHolder extends RecyclerView.ViewHolder {

    /**
     * ItemView的布局下标
     */
    public int itemViewIndex = -1;


    VastHolder(@NonNull View itemView) {
        super(itemView);
    }

    private SparseArray<View> views = new SparseArray<>();

    /**
     * 通过id获取View
     *
     * @param id 资源id
     */
    public <V extends View> V getViewById(@IdRes int id) {
        View view = views.get(id);
        if (null == view) {
            view = itemView.findViewById(id);
            views.append(id, view);
        }
        return (V) view;
    }

    /**
     * 设置文本
     *
     * @param id view_id
     * @param s  文本
     */
    public void setText(@IdRes int id, String s) {
        TextView view = getViewById(id);
        view.setText(s);
    }

    /**
     * 设置文本
     *
     * @param id    view_id
     * @param resId 文本资源id
     */
    public void setText(@IdRes int id, @StringRes int resId) {
        TextView view = getViewById(id);
        view.setText(resId);
    }

    /**
     * 设置文本颜色
     *
     * @param id      view_id
     * @param colorId color资源id
     */
    public void setTextColorRes(@IdRes int id, @ColorRes int colorId) {
        setTextColor(id, ContextCompat.getColor(itemView.getContext(), colorId));
    }

    /**
     * 设置文本颜色
     *
     * @param id    view_id
     * @param color color资源id
     */
    public void setTextColor(@IdRes int id, @ColorInt int color) {
        TextView view = getViewById(id);
        view.setTextColor(color);
    }

    /**
     * 设置图片
     *
     * @param id    view_id
     * @param resId drawable资源id
     */
    public void setImageResource(@IdRes int id, @DrawableRes int resId) {
        ImageView view = getViewById(id);
        view.setImageResource(resId);
    }

    /**
     * 设置图片
     *
     * @param id     view_id
     * @param bitmap bitmap
     */
    public void setImageBitmap(@IdRes int id, Bitmap bitmap) {
        ImageView view = getViewById(id);
        view.setImageBitmap(bitmap);
    }

    /**
     * 设置图片
     *
     * @param id       view_id
     * @param drawable drawable
     */
    public void setImageDrawable(@IdRes int id, Drawable drawable) {
        ImageView view = getViewById(id);
        view.setImageDrawable(drawable);
    }

    /**
     * 设置背景
     *
     * @param id    view_id
     * @param resId drawable资源id
     */
    public void setBackgroundResource(@IdRes int id, @DrawableRes int resId) {
        View view = getViewById(id);
        view.setBackgroundResource(resId);
    }

    /**
     * 设置背景
     *
     * @param id      view_id
     * @param colorId color资源id
     */
    public void setBackgroundColorRes(@IdRes int id, @ColorRes int colorId) {
        setBackgroundColor(id, ContextCompat.getColor(itemView.getContext(), colorId));
    }

    /**
     * 设置背景
     *
     * @param id    view_id
     * @param color color值
     */
    public void setBackgroundColor(@IdRes int id, @ColorInt int color) {
        View view = getViewById(id);
        view.setBackgroundColor(color);
    }

    /**
     * 设置View显示状态
     *
     * @param id         view_id
     * @param visibility VISIBLE, INVISIBLE, GONE
     */
    public void setVisibility(@IdRes int id, int visibility) {
        View view = getViewById(id);
        view.setVisibility(visibility);
    }

}
