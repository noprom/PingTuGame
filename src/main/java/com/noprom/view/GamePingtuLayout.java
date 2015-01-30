package com.noprom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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


    public GamePintuListener mListener;     // 监听器
    private boolean isTimeEnabled = false;  // 是否允许设置时间
    private boolean isGameSuccess = false;  // 游戏是否通关
    private boolean isGameOver = false;     // 游戏是否结束
    private int mTime;                      // 每个关卡的游戏限制时间

    // 拼图接口
    public interface GamePintuListener {
        // 下一关
        void nextLevel(int nextLevel);

        // 时间改变
        void timeChanged(int currentTime);

        // 游戏结束
        void gameOver();
    }


    /**
     * 是否允许设置时间
     *
     * @param isTimeEnabled
     */
    public void setTimeEnabled(boolean isTimeEnabled) {
        this.isTimeEnabled = isTimeEnabled;
    }

    /**
     * 设置接口回调
     *
     * @param listener 监听器
     */
    public void setOnGamePintuListener(GamePintuListener listener) {
        mListener = listener;
    }


    private static final int TIME_CHANGED = 0x110;  // 时间改变
    private static final int NEXT_LEVEL = 0x111;    // 下一关
    private int mLevel = 1;
    // 处理消息更新程序UI界面
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_CHANGED: {
                    // 开始计时
                    if (isGameSuccess || isGameOver ||isPause) {
                        return;
                    }

                    if (mListener != null) {
                        mListener.timeChanged(mTime);
                    }
                    if (mTime == 0) {
                        isGameOver = true;
                        mListener.gameOver();
                    }
                    mTime--;
                    // 延迟1秒发送消息
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);

                    break;
                }
                case NEXT_LEVEL: {
                    mLevel++;
                    if (mListener != null) {
                        // 下一关
                        mListener.nextLevel(mLevel);
                    } else {
                        // 自己调用过关接口
                        nextLevel();
                    }
                    break;
                }
            }
        }

    };


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
            // 判断是否开启了时间限制
            checkTimeEnable();
            once = true;
        }
        setMeasuredDimension(mWidth, mWidth);
    }

    /**
     * 检查是否开启了时间限制功能
     */
    private void checkTimeEnable() {
        if (isTimeEnabled) {
            // 根据当前等级设置时间
            countTimeBaseLevel();
            // 通过主界面来更新显示的时间
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    /**
     * 根据当前等级设置时间
     */
    private void countTimeBaseLevel() {
        // 设置游戏时间
        mTime = (int) Math.pow(2, mLevel) * 60;
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

            // 设置Item间的横向间隙，通过rightMargin
            // 不是最后一列
            if ((i + 1) % mColum != 0) {
                lp.rightMargin = mMargin;
            }

            // 设置横向的间距
            // 不是第一列,设置元素的rightOf属性
            if (i % mColum != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, mGamePintuItems[i - 1].getId());
            }

            // 设置纵向的间距
            // 不是第一行，设置top
            if ((i + 1) > mColum) {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, mGamePintuItems[i - mColum].getId());
            }
            addView(item, lp);
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


    private ImageView mFirst;   // 点击的第一张图片
    private ImageView mSecond;  // 点击的第二张图片


    private RelativeLayout mAnimLayout; // 动画层
    private boolean isAniming;  // 正在进行动画

    /**
     * 处理每张小图的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (isAniming) return;

        // 如果两次点击的都是同一张图片则取消高亮
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        // 第一次点击
        if (mFirst == null) {
            mFirst = (ImageView) v;
            // 设置透明度
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        } else {
            mSecond = (ImageView) v;
            // 交换图片
            exchangeView();
        }

    }


    /**
     * 交换图片Item
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);    // 取消第一张图片的高亮状态

        // 准备动画层
        setUpAnimLayout();

        // 复制到动画层
        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        // 设置布局属性
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
        first.setImageBitmap(secondBitmap);
        // 设置布局属性
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        // 设置动画
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop() + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        // 动画监听
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAniming = true;   // 动画开始标识
                // 将两张图片隐藏
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                // 重置两张图的Tag
                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                // 去掉动画层
                mAnimLayout.removeAllViews();

                // 检查是否拼图成功
                checkSuccess();

                isAniming = false;  // 动画结束标识
            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });


    }


    /**
     * 判断是否拼图成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;
        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView view = mGamePintuItems[i];
            if (getItemIndexByTag((String) view.getTag()) != i) {
                isSuccess = false;
            }
        }
        if (isSuccess) {
            isGameSuccess = true;
            mHandler.removeMessages(TIME_CHANGED);

            Toast.makeText(getContext(), "骚年恭喜你升级啦!~~~", Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }

    /**
     * 重新开始游戏
     */
    public void restartGame() {
        isGameOver = false;
        mColum--;
        nextLevel();
    }

    // 游戏是处于暂停状态
    private boolean isPause;

    /**
     * 暂停游戏
     */
    public void pauseGame() {
        isPause = true;
        mHandler.removeMessages(TIME_CHANGED);
    }

    /**
     * 恢复游戏
     */
    public void resumeGame() {
        if(isPause){
            isPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }


    /**
     * 进入游戏下一关
     */
    public void nextLevel() {
        this.removeAllViews();  // 移除所有的view
        mAnimLayout = null;
        mColum++;               // 切块数目加1
        isGameSuccess = false;  // 未通关
        checkTimeEnable();
        initBitmap();
        initItem();
    }

    /**
     * 根据Tag获取ID
     *
     * @param tag Tag
     * @return ID
     */
    private int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }


    /**
     * 根据tag获取 index
     *
     * @param tag tag
     * @return index
     */
    private int getItemIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }


    /**
     * 准备动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            // 加到当前游戏面板
            addView(mAnimLayout);
        }
    }
}
