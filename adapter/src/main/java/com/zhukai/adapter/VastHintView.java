package com.zhukai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class VastHintView extends RelativeLayout {

    private RelativeLayout root;
    private ImageView img;
    private TextView text;

    private VastHintView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_vast_hint_view, this);
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
        root = findViewById(R.id.hint_root);
        img = findViewById(R.id.hint_img);
        text = findViewById(R.id.hint_text);
    }

    private RelativeLayout getRoot() {
        return this.root;
    }

    private ImageView getImg() {
        return this.img;
    }

    private TextView getText() {
        return this.text;
    }


    public static class Builder {

        private Context mContext;

        private View customView;

        private Drawable imgDrawable;

        private String textString;

        private int textColorInt;

        private int textSize;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setCustomView(View view) {
            this.customView = view;
            return this;
        }

        public Builder setDrawableRes(@DrawableRes int res) {
            this.imgDrawable = ContextCompat.getDrawable(mContext, res);
            return this;
        }

        public Builder setDrawable(Drawable drawable) {
            this.imgDrawable = drawable;
            return this;
        }

        public Builder setText(@StringRes int res) {
            this.textString = mContext.getString(res);
            return this;
        }

        public Builder setText(String str) {
            this.textString = str;
            return this;
        }

        public Builder setTextColorRes(@ColorRes int textColorRes) {
            this.textColorInt = ContextCompat.getColor(mContext,textColorRes);
            return this;
        }

        public Builder setTextColor(@ColorInt int textColorInt) {
            this.textColorInt = textColorInt;
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }


        public VastHintView build() {

            VastHintView vastHintView = new VastHintView(mContext);

            RelativeLayout hintRoot = vastHintView.getRoot();
            ImageView hintImg = vastHintView.getImg();
            TextView hintText = vastHintView.getText();

            if (null != customView) {
                hintImg.setVisibility(GONE);
                hintText.setVisibility(GONE);
                hintRoot.addView(customView);
            } else {

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

                    if (textColorInt != 0){
                        hintText.setTextColor(textColorInt);
                    }
                }
            }

            return vastHintView;
        }
    }

}
