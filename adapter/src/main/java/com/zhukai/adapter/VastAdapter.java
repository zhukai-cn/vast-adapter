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
    private View mVacancyHintView;

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
            return new VastHolder(mVacancyHintView);
        }
        int position = positionFlag(viewType);
        if (isHeader(position)) {
            return new VastHolder(mHeaderViews.get(position));
        }
        if (isFooter(position)) {
            return new VastHolder(mFooterViews.get(position - getDataSize() - getHeaderCount()));
        }

        final VastHolder vastHolder = new VastHolder(LayoutInflater.from(parent.getContext()).inflate(layoutIds[viewType], parent, false));
        vastHolder.itemViewIndex = viewType;

        //点击事件处理
        if (null != mOnItemClickListener) {
            vastHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(vastHolder);
                    if (position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onClick(position);
                    }
                }
            });
        }
        //长按事件处理
        if (null != mOnItemLongClickListener) {
            vastHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition(vastHolder);
                    if (position != RecyclerView.NO_POSITION) {
                        return mOnItemLongClickListener.onLongClick(position);
                    }
                    return false;
                }
            });
        }
        onCreateHolder(vastHolder);
        return vastHolder;
    }

    /**
     * 获取对应Holder的AdapterPosition值
     */
    public int getAdapterPosition(VastHolder holder) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }
        return adapterPosition - getHeaderCount();
    }

    /**
     * 获取对应Holder的LayoutPosition值
     */
    public int getLayoutPosition(VastHolder holder) {
        return holder.getLayoutPosition() - getHeaderCount();
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isVacancyVisibility() || isHeader(position)
                || isFooter(position)) {
            return;
        }

        int dataPosition = position - getHeaderCount();
        bindHolder((VastHolder) holder, mData.get(dataPosition), dataPosition);

        //处理预加载
        if (checkPreload(dataPosition)) {
            mOnPreloadListener.onPreload();
        }
    }

    /**
     * 检测是否触发预加载
     */
    private boolean checkPreload(int position) {
        return null != mOnPreloadListener
                && mHostRv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                && Math.max(getDataSize() - 1 - position, 0) == mPreloadThreshold;
    }

    @Override
    public final int getItemViewType(int position) {
        if (isVacancyVisibility()) {
            return -1;
        }
        if (isHeader(position) || isFooter(position)) {
            return positionFlag(position);
        }
        return getItemViewIndex(position);
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
        return getHeaderCount() + getFooterCount() + getDataSize();
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
        int frontCount = getHeaderCount() + getDataSize();
        return position >= frontCount && position - frontCount < getFooterCount();
    }

    /**
     * 是否展示无列表提示View
     */
    private boolean isVacancyVisibility() {
        return (mHeaderViews.size() + mFooterViews.size() + getDataSize()) < 1 && null != mVacancyHintView;
    }

    /**
     * 获取数据总量
     */
    public int getDataSize() {
        return mData.size();
    }

    /**
     * 添加头部view
     *
     * @param view 添加对象view
     * @return 对应的下标
     */
    public int addHeaderView(View view) {
        int index = mHeaderViews.size();
        if (!addHeaderView(index, view)) {
            index = -1;
        }
        return index;
    }

    /**
     * 添加/插入头部
     *
     * @param index 目标下标
     * @param view  添加对象view
     * @return 是否成功
     */
    public boolean addHeaderView(int index, View view) {
        if (index < 0 || view == null) {
            return false;
        }
        mHeaderViews.add(index, view);
        return true;
    }

    /**
     * 获取头部view数量
     */
    public final int getHeaderCount() {
        return mHeaderViews.size();
    }

    /**
     * 移除头部view
     *
     * @param index 下标
     * @return 移除的HeaderView
     */
    public View removeHeaderView(int index) {
        if (index >= 0 && mHeaderViews.size() > index) {
            return mHeaderViews.remove(index);
        }
        return null;
    }

    /**
     * 移除头部view
     *
     * @param view 指定view
     * @return 返回移除的对应下标
     */
    public int removeHeaderView(View view) {
        int index = -1;
        if (null != view) {
            index = mHeaderViews.indexOf(view);
            if (null == removeHeaderView(index)) {
                index = -1;
            }
        }
        return index;
    }

    /**
     * 移除全部头部view
     *
     * @return 返回移除的总个数
     */
    public int removeAllHeaderView() {
        int count = getHeaderCount();
        mHeaderViews.clear();
        return count;
    }

    /**
     * 添加尾部View
     *
     * @param view 添加对象view
     * @return 对应的下标
     */
    public int addFooterView(View view) {
        int index = mFooterViews.size();
        if (!addFooterView(index, view)) {
            index = -1;
        }
        return index;
    }

    /**
     * 添加/插入尾部view
     *
     * @param index 目标下标
     * @param view  添加对象view
     * @retrun 是否成功
     */
    public boolean addFooterView(int index, View view) {
        if (index < 0 || view == null) {
            return false;
        }
        mFooterViews.add(index, view);
        return true;
    }

    /**
     * 获取尾部view的个数
     */
    public final int getFooterCount() {
        return mFooterViews.size();
    }

    /**
     * 移除尾部view
     *
     * @param index 下标
     * @return 移除的FooterView
     */
    public View removeFooterView(int index) {
        if (index >= 0 && mFooterViews.size() > index) {
            return mFooterViews.remove(index);
        }
        return null;
    }

    /**
     * 移除尾部view
     *
     * @param view 指定view
     * @return 返回移除的对应下标
     */
    public int removeFooterView(View view) {
        int index = -1;
        if (null != view) {
            index = mFooterViews.indexOf(view);
            if (null == removeFooterView(index)) {
                index = -1;
            }
        }
        return index;
    }

    /**
     * 移除全部尾部view
     *
     * @return 返回移除的总个数
     */
    public int removeAllFooterView() {
        int count = getFooterCount();
        mFooterViews.clear();
        return count;
    }

    /**
     * 设置空缺view
     */
    public void setVacancyView(View vacancyView) {
        this.mVacancyHintView = vacancyView;
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
     * Holder创建完成
     */
    public void onCreateHolder(VastHolder holder){

    }

    /**
     * 绑定布局数据
     */
    public abstract void bindHolder(VastHolder holder, D data, int position);

    /**
     * 多布局需求时，根据下标得到对应的布局资源id
     *
     * @return 返回layout的下标
     */
    public int getItemViewIndex(int position) {
        return 0;
    }

}
