package com.noprom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.noprom.app.R;
import com.noprom.utils.ImagePiece;
import com.noprom.utils.ImageSplitterUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by noprom.
 */
public class GamePingtuLayout extends RelativeLayout implements View.OnClickListener {

    // 块数
    private int mColum = 3;

    // 容器内边距
    private int mPadding;

    // 容器外边距
    private int mMargin;

    // 用于存储所有ImageView
    private ImageView[] mGamePintuItems;

    // 图片宽度
    private int mItemWidth;

    // 游戏面板宽度
    private int mWidth;

    // 游戏的图片
    private Bitmap mBitmap;

    // 小图片
    private List<ImagePiece> mItemBitmaps;

    private boolean once;

    public GamePingtuLayout(Context context) {
        this(context, null);
    }

    public GamePingtuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GamePingtuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(); // 初始化操作
    }

    /**
     * 测量布局的大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 取宽和高的最小值
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
        if (!once) {
            // 进行切图以及乱序
            initBitmap();
            // 设置ImageView的宽和高的属性
            initItem();
            once = true;
        }
        setMeasuredDimension(mWidth, mWidth);
    }

    /**
     * 进行切图以及乱序
     */
    private void initBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        }
        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColum);
        // 乱序操作
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 设置ImageView的宽和高等属性
     */
    private void initItem() {
        // 计算每张小图宽度
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColum - 1)) / mColum;
        mGamePintuItems = new ImageView[mColum * mColum];

        // 生成每一个Item并设置相应的属性
        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView item = new ImageView(getContext());
            // 设置点击事件
            item.setOnClickListener(this);
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
            mGamePintuItems[i] = item;
            item.setId(i + 1);

            // 存储Item的index，以便判断是否拼图成功
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            // 设置布局参数
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);

            // TODO http://www.imooc.com/video/6013 26:37


        }
    }


    /**
     * 初始化操作
     */
    private void init() {
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());
    }

    /**
     * 获取多个参数的最小值
     *
     * @return 最小值
     */
    private int min(int... params) {
        int min = params[0];
        for (int param : params) {
            if (param < min) min = param;
        }
        return min;
    }

    /**
     * 处理每张小图的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

    }
}
