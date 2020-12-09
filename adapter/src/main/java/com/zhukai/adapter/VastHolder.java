package com.zhukai.adapter;

import android.graphics.Bitmap;
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

    private SparseArray<View> views = new SparseArray<>();

    private int layoutIndex = -1;

    void setLayoutIndex(int index) {
        this.layoutIndex = index;
    }

    public int getLayoutIndex() {
        return layoutIndex;
    }

    VastHolder(@NonNull View itemView) {
        super(itemView);
    }

    void setOnLongClickListener(final VastAdapter.OnItemLongClickListener l) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return l.onLongClick(getLayoutPosition());
            }
        });
    }

    void setOnClickListener(final VastAdapter.OnItemClickListener l) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l.onClick(getLayoutPosition());
            }
        });
    }

    public <V extends View> V getViewById(@IdRes int id) {
        View view = views.get(id);
        if (null == view) {
            view = itemView.findViewById(id);
            views.append(id, view);
        }
        return (V) view;
    }

    public void setText(@IdRes int id, String s) {
        TextView view = getViewById(id);
        view.setText(s);
    }

    public void setText(@IdRes int id, @StringRes int resId) {
        TextView view = getViewById(id);
        view.setText(resId);
    }

    public void setImageResource(@IdRes int id, @DrawableRes int resId) {
        ImageView view = getViewById(id);
        view.setImageResource(resId);
    }

    public void setImageBitmap(@IdRes int id, Bitmap bitmap) {
        ImageView view = getViewById(id);
        view.setImageBitmap(bitmap);
    }

    public void setBackgroundResource(@IdRes int id, @DrawableRes int resId) {
        View view = getViewById(id);
        view.setBackgroundResource(resId);
    }

    public void setBackgroundColorRes(@IdRes int id, @ColorRes int colorId) {
        setBackgroundColor(id, ContextCompat.getColor(itemView.getContext(), colorId));
    }

    public void setBackgroundColor(@IdRes int id, @ColorInt int colorId) {
        View view = getViewById(id);
        view.setBackgroundColor(colorId);
    }

    public void setVisibility(@IdRes int id, int visibility) {
        View view = getViewById(id);
        view.setVisibility(visibility);
    }

}
