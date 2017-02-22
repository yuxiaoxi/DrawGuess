package com.zhy.graph.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gra.zhy.com.graph.R;

/**
 * Created by yuzhuo on 2017/2/22.
 */
public class NewBasicSingleItem extends LinearLayout {

    protected TextView titleTextView;
    protected TextView subTitleTextView;
    protected ImageView iconImageView;
    protected TextView countTextView;
    protected ImageView moreImageView;
    protected ImageView redDotImageView;

    public NewBasicSingleItem(Context context) {
        this(context, null);
    }

    public NewBasicSingleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BasicSingleItem);

        int iconResId = array.getResourceId(R.styleable.BasicSingleItem_item_icon, 0);

        String title = array.getString(R.styleable.BasicSingleItem_item_title);
        String titleHint = array.getString(R.styleable.BasicSingleItem_item_titleHint);
        int defSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18,
                getResources().getDisplayMetrics());
        int titleSize = array.getDimensionPixelSize(R.styleable.BasicSingleItem_item_titleSize,
                defSize);
        int titleColor = array.getColor(R.styleable.BasicSingleItem_item_titleColor, 0xff323232);

        String subTitle = array.getString(R.styleable.BasicSingleItem_item_subTitle);
        String subTitleHint = array.getString(R.styleable.BasicSingleItem_item_subTitleHint);
        defSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                getResources().getDisplayMetrics());
        int subTitleSize = array.getDimensionPixelSize(
                R.styleable.BasicSingleItem_item_subTitleSize, defSize);
        int subTitleColor = array.getColor(R.styleable.BasicSingleItem_item_subTitleColor,
                0xff878787);

        String count = array.getString(R.styleable.BasicSingleItem_item_count);
        defSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18,
                getResources().getDisplayMetrics());
        int countSize = array.getDimensionPixelSize(R.styleable.BasicSingleItem_item_countSize,
                defSize);
        int countColor = array.getColor(R.styleable.BasicSingleItem_item_countColor, 0xff878787);

        int indicatorId = array.getResourceId(R.styleable.BasicSingleItem_item_indicator, 0);
        array.recycle();

        iconImageView = (ImageView) findViewById(R.id.s_icon);
        setIcon(iconResId);

        titleTextView = (TextView) findViewById(R.id.s_title);
        setTitle(title);
        setTitleSize(titleSize);
        setTitleColor(titleColor);
        setTitleHint(titleHint);

        subTitleTextView = (TextView) findViewById(R.id.s_subtitle);
        setSubTitle(subTitle);
        setSubTitleSize(subTitleSize);
        setSubTitleColor(subTitleColor);
        setSubTitleHint(subTitleHint);

        countTextView = (TextView) findViewById(R.id.s_count);
        setCount(count);
        setCountColor(countColor);
        setCountSize(countSize);

        moreImageView = (ImageView) findViewById(R.id.s_more);
        setIndicator(indicatorId);

        redDotImageView = (ImageView) findViewById(R.id.s_reddot);
    }

    protected void inflateLayout(Context context) {
        inflate(context, R.layout.new_basic_single_item, this);
    }

    public ImageView getIcon() {
        return iconImageView;
    }

    public void setIconSize(int width, int height) {
        android.view.ViewGroup.LayoutParams lps = iconImageView.getLayoutParams();
        lps.width = width;
        lps.height = height;
        iconImageView.setLayoutParams(lps);
    }

    public void setIconEnable(boolean enabled) {
        iconImageView.setEnabled(enabled);
    }

    public boolean isIconEnabled() {
        return iconImageView.isEnabled();
    }

    public void setIconSelected(boolean selected) {
        iconImageView.setSelected(selected);
    }

    public boolean isIconSelected() {
        return iconImageView.isSelected();
    }

    public TextView getTitleView() {
        return titleTextView;
    }

    public TextView getSubTitleView() {
        return subTitleTextView;
    }

    public TextView getCountView() {
        return countTextView;
    }

    public ImageView getIndicator() {
        return moreImageView;
    }

    public void setIndicatorSize(int width, int height) {
        android.view.ViewGroup.LayoutParams lps = moreImageView.getLayoutParams();
        lps.width = width;
        lps.height = height;
        moreImageView.setLayoutParams(lps);
    }

    public void setIndicatorEnable(boolean enabled) {
        moreImageView.setEnabled(enabled);
    }

    public boolean isIndicatorEnabled() {
        return moreImageView.isEnabled();
    }

    public void setIndicatorSelected(boolean selected) {
        moreImageView.setSelected(selected);
    }

    public boolean isIndicatorSelected() {
        return moreImageView.isSelected();
    }

    public void setIcon(int resId) {
        if (resId > 0) {
            iconImageView.setImageResource(resId);
        }
        iconImageView.setVisibility(resId == 0 ? GONE : VISIBLE);
    }

    public void setIcon(Drawable icon) {
        iconImageView.setImageDrawable(icon);
    }

    public void setTitle(CharSequence title) {
        titleTextView.setText(title);
    }

    public void setTitleHint(CharSequence hint) {
        titleTextView.setHint(hint);
    }

    public void setTitleSize(int size) {
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setTitleColor(int color) {
        titleTextView.setTextColor(color);
    }

    public void setSubTitle(CharSequence title) {
        subTitleTextView.setText(title);
    }

    public void setSubTitleHint(CharSequence hint) {
        subTitleTextView.setHint(hint);
    }

    public void setSubTitleSize(int size) {
        subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setSubTitleColor(int color) {
        subTitleTextView.setTextColor(color);
    }

    public void setCount(CharSequence count) {
        countTextView.setText(count);
        if (TextUtils.isEmpty(count)) {
            countTextView.setVisibility(GONE);
        } else {
            countTextView.setVisibility(VISIBLE);
        }
    }

    public void setCountSize(int size) {
        countTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setCountColor(int value) {
        countTextView.setTextColor(value);
    }

    public void setIndicator(int drawableId) {
        if (drawableId > 0) {
            moreImageView.setImageResource(drawableId);
        }
        moreImageView.setVisibility(drawableId == 0 ? GONE : VISIBLE);
    }

    public void setIndicator(Drawable drawable) {
        moreImageView.setImageDrawable(drawable);
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        setIconEnable(clickable);
        setIndicatorEnable(clickable);
    }

    public void clearTitle() {
        setTitle(null);
        setSubTitle(null);
    }

    public void showRedDot(boolean visible) {
        redDotImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public boolean isRightImageViewSelected() {
        return moreImageView.isSelected();
    }

    public void setRightImageViewSelected(boolean selected) {
        moreImageView.setSelected(selected);
    }
}
