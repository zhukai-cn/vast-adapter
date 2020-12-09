package com.zhukai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.LinkedList;
import java.util.List;

/**
 * 通用适配器
 *
 * @author zhukai
 */
public abstract class VastAdapter<D> extends RecyclerView.Adapter {

    /**
     * 默认的预加载临界值
     */
    private static final int PRELOAD_THRESHOLD_DEFAULT = 3;

    /**
     * position转换标签
     * <p>
     * 对Header和Footer的position进行标签转换，来充当viewType;以区别于正常的viewType.
     */
    private static final int HEADER_FOOTER_VIEW_TYPE_FLAG = Integer.MAX_VALUE;

    /**
     * 数据
     */
    private List<D> mData;

    /**
     * 布局id
     */
    private int[] layoutIds;

    /**
     * 头部布局
     */
    private LinkedList<View> mHeaderViews = new LinkedList<>();

    /**
     * 尾部布局
     */
    private LinkedList<View> mFooterViews = new LinkedList<>();

    /**
     * 空数据提示view
     */
    private VastHintView mVacancyView;

    /**
     * 长按监听
     */
    private OnItemLongClickListener mOnItemLongClickListener;

    /**
     * 点击监听
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * 预加载监听
     */
    private OnPreloadListener mOnPreloadListener;

    /**
     * 宿主RecyclerView
     */
    private RecyclerView mHostRv;

    /**
     * 预加载条目阈值
     */
    private int mPreloadThreshold = PRELOAD_THRESHOLD_DEFAULT;

    public VastAdapter(List<D> data, @LayoutRes int... layoutIds) {
        this.mData = data;
        this.layoutIds = layoutIds;
    }

    private OnItemLongClickListener itemLongClickListenerProxy = new OnItemLongClickListener() {
        @Override
        public boolean onLongClick(int position) {
            if (null != mOnItemLongClickListener) {
                return mOnItemLongClickListener.onLongClick(position - getHeaderCount());
            }
            return false;
        }
    };

    private OnItemClickListener itemClickListenerProxy = new OnItemClickListener() {
        @Override
        public void onClick(int position) {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onClick(position - getHeaderCount());
            }
        }
    };

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mHostRv = recyclerView;
        //处理GridLayoutManager头部兼容
        RecyclerView.LayoutManager lm = mHostRv.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            final GridLayoutManager glm = (GridLayoutManager) lm;
            final GridLayoutManager.SpanSizeLookup originalSpanSL = glm.getSpanSizeLookup();
            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isVacancyVisibility() || isHeader(position)
                            || isFooter(position)) {
                        return glm.getSpanCount();
                    } else if (null != originalSpanSL) {
                        return originalSpanSL.getSpanSize(position - getHeaderCount());
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        //处理StaggeredGridLayoutManager头部兼容
        RecyclerView.LayoutManager lm = mHostRv.getLayoutManager();
        if (lm instanceof StaggeredGridLayoutManager) {
            View view = holder.itemView;
            int position = holder.getAdapterPosition();
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            lp.setFullSpan(isVacancyVisibility() || isHeader(position)
                    || isFooter(position));
        }
        super.onViewAttachedToWindow(holder);
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isVacancyVisibility()) {
            return new VastHolder(mVacancyView);
        }
        int position = positionFlag(viewType);
        if (isHeader(position)) {
            return new VastHolder(mHeaderViews.get(position));
        }

        if (isFooter(position)) {
            return new VastHolder(mFooterViews.get(position - mData.size() - getHeaderCount()));
        }

        VastHolder vastHolder = new VastHolder(LayoutInflater.from(parent.getContext()).inflate(layoutIds[viewType], parent, false));
        vastHolder.setLayoutIndex(viewType);

        if (null != mOnItemClickListener) {
            vastHolder.setOnClickListener(itemClickListenerProxy);
        }
        if (null != mOnItemLongClickListener) {
            vastHolder.setOnLongClickListener(itemLongClickListenerProxy);
        }

        return vastHolder;
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isVacancyVisibility() || isHeader(position)
                || isFooter(position)) {
            return;
        }

        int dataPosition = position - getHeaderCount();
        bindView((VastHolder) holder, mData.get(dataPosition), dataPosition);

        //处理预加载
        if (checkPreload(position)) {
            mOnPreloadListener.onPreload();
        }
    }

    /**
     * 检测是否触发预加载
     */
    private boolean checkPreload(int position) {
        return null != mOnPreloadListener
                && mHostRv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                && Math.max(mData.size() - 1 - getHeaderCount() - position, 0) == mPreloadThreshold;
    }

    @Override
    public final int getItemViewType(int position) {
        if (isVacancyVisibility()) {
            return -1;
        }
        if (isHeader(position) || isFooter(position)) {
            return positionFlag(position);
        }
        return getLayoutIndex(position);
    }

    /**
     * 对Header和Footer的position进行标签转换，来充当viewType;以区别于正常的viewType.
     */
    private int positionFlag(int target) {
        return target ^ HEADER_FOOTER_VIEW_TYPE_FLAG;
    }


    @Override
    public final int getItemCount() {
        if (isVacancyVisibility()) {
            return 1;
        }
        return getHeaderCount() + getFooterCount() + mData.size();
    }

    /**
     * 是否是头view
     */
    private boolean isHeader(int position) {
        return position >= 0 && position < getHeaderCount();
    }

    /**
     * 是否是尾view
     */
    private boolean isFooter(int position) {
        int frontCount = getHeaderCount() + mData.size();
        return position >= frontCount && position - frontCount < getFooterCount();
    }

    /**
     * 是否展示无列表提示View
     */
    private boolean isVacancyVisibility() {
        return (mHeaderViews.size() + mFooterViews.size() + mData.size()) < 1 && null != mVacancyView;
    }

    /**
     * 添加头部view
     */
    public void addHeaderView(View view) {
        addHeaderView(mHeaderViews.size(), view);
    }

    /**
     * 添加/插入头部
     */
    public void addHeaderView(int index, View view) {
        mHeaderViews.add(index, view);
    }

    /**
     * 移除头部view
     *
     * @param index 下标
     */
    public void removeHeaderView(int index) {
        if (mHeaderViews.size() > index) {
            mHeaderViews.remove(index);
        }
    }

    /**
     * 移除头部view
     *
     * @param view 指定view
     */
    public void removeHeaderView(View view) {
        if (null != view) {
            mHeaderViews.remove(view);
        }
    }

    /**
     * 移除全部头部view
     */
    public void removeAllHeaderView() {
        mHeaderViews.clear();
    }

    /**
     * 获取头部view数量
     */
    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    /**
     * 添加尾部view
     */
    public void addFooterView(View view) {
        addFooterView(mFooterViews.size(), view);
    }

    /**
     * 添加/插入尾部view
     */
    public void addFooterView(int index, View view) {
        mFooterViews.add(index, view);
    }

    /**
     * 获取尾部view的个数
     */
    public int getFooterCount() {
        return mFooterViews.size();
    }

    /**
     * 移除尾部view
     *
     * @param index 下标
     */
    public void removeFooterView(int index) {
        if (mFooterViews.size() > index) {
            mFooterViews.remove(index);
        }
    }

    /**
     * 移除尾部view
     *
     * @param view 指定view
     */
    public void removeFooterView(View view) {
        if (null != view) {
            mFooterViews.remove(view);
        }
    }

    /**
     * 移除全部尾部view
     */
    public void removeAllFooterView() {
        mFooterViews.clear();
    }

    /**
     * 设置空缺view
     */
    public void setVacancyView(VastHintView vacancyView) {
        this.mVacancyView = vacancyView;
    }

    /**
     * 设置长按监听
     */
    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.mOnItemLongClickListener = itemLongClickListener;
    }

    /**
     * 设置点击监听
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    /**
     * 设置预加载预留条目
     */
    public void setPreloadThreshold(int value) {
        this.mPreloadThreshold = value;
    }

    /**
     * 设置预加载监听
     */
    public void setOnPreloadListener(OnPreloadListener onPreloadListener) {
        this.mOnPreloadListener = onPreloadListener;
    }

    /**
     * item长按监听
     */
    public interface OnItemLongClickListener {
        boolean onLongClick(int position);
    }

    /**
     * item点击监听
     */
    public interface OnItemClickListener {
        void onClick(int position);
    }

    /**
     * 预加载监听
     */
    public interface OnPreloadListener {
        void onPreload();
    }

    /**
     * 绑定布局数据
     */
    public abstract void bindView(VastHolder holder, D data, int position);

    /**
     * 多布局需求时，根据下标得到对应的布局资源id
     *
     * @return 返回layout的下标
     */
    public int getLayoutIndex(int position) {
        return 0;
    }

}
