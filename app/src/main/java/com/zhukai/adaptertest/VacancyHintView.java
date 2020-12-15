package com.zhukai.adaptertest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 * 缺省图View
 * 默认样式为整体内容居中，内容包括上为图片，下为文字。
 */
public class VacancyHintView extends LinearLayout {

    private ImageView img;
    private TextView text;

    private VacancyHintView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_vast_hint_view, this);
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);
        img = findViewById(R.id.hint_img);
        text = findViewById(R.id.hint_text);
    }

    public ImageView getHintImgView() {
        return this.img;
    }

    public TextView getHintTextView() {
        return this.text;
    }

    public static class Builder {

        private Context mContext;

        private Drawable imgDrawable;

        private String textString;

        private int textColorInt;

        private int textSize;

        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         * 设置Drawable资源id
         */
        public Builder setImageResource(@DrawableRes int res) {
            this.imgDrawable = ContextCompat.getDrawable(mContext, res);
            return this;
        }

        /**
         * 设置Drawable
         */
        public Builder setImageDrawable(Drawable drawable) {
            this.imgDrawable = drawable;
            return this;
        }

        /**
         * 设置文本资源id
         */
        public Builder setText(@StringRes int res) {
            this.textString = mContext.getString(res);
            return this;
        }

        /**
         * 设置文本
         */
        public Builder setText(String str) {
            this.textString = str;
            return this;
        }

        /**
         * 设置文字颜色资源id
         */
        public Builder setTextColorRes(@ColorRes int textColorRes) {
            this.textColorInt = ContextCompat.getColor(mContext, textColorRes);
            return this;
        }

        /**
         * 设置文字颜色值
         */
        public Builder setTextColor(@ColorInt int textColorInt) {
            this.textColorInt = textColorInt;
            return this;
        }

        /**
         * 设置字体大小
         *
         * @param textSize sp
         */
        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        /**
         * 根据内容构建缺省图View
         */
        public VacancyHintView build() {

            VacancyHintView vacancyHintView = new VacancyHintView(mContext);
            ImageView hintImg = vacancyHintView.getHintImgView();
            TextView hintText = vacancyHintView.getHintTextView();

            if (null != imgDrawable) {
                hintImg.setVisibility(VISIBLE);
                hintImg.setImageDrawable(imgDrawable);
            }

            if (!TextUtils.isEmpty(textString)) {
                hintText.setVisibility(VISIBLE);
                hintText.setText(textString);

                if (textSize > 0) {
                    hintText.setTextSize(textSize);
                }

                if (textColorInt != 0) {
                    hintText.setTextColor(textColorInt);
                }
            }
            return vacancyHintView;
        }
    }

}
