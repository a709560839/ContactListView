package com.sj.contactlistview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.sj.contactlistview.R;
import com.sj.contactlistview.util.DensityUtil;

/**
 * @author 该view参考自https://blog.csdn.net/a_zhon/article/details/53214849
 */
public class IndexView extends View {
    /**
     * 绘制的列表导航字母
     */
    public static String[] words = {"↑", "☆", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    /**
     * 字母画笔
     */
    private Paint wordsPaint;
    /**
     * 字母背景画笔
     */
    private Paint bgPaint;
    /**
     * 字母平均高度的api
     */
    private Paint.FontMetrics fontMetrics;
    /**
     * 每一个字母的宽度
     */
    private float itemWidth;
    /**
     * 每一个字母的高度
     */
    private float itemHeight;
    /**
     * 手指按下的字母索引位置
     */
    private int touchIndex = 0;
    /**
     * 手指按下时屏幕中间的提示view
     */
    private DropView dropView;
    /**
     * 手指是否触摸
     */
    private boolean isTouch;
    /**
     * 触摸的字符的回调
     */
    private OnWordsChangeListener listener;

    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexView);
        //选中时的圆形背景颜色
        int bgColor = a.getColor(R.styleable.IndexView_words_background_color, Color.BLUE);
        //索引字母大小，推荐为dp，可不受系统字体大小更改影响
        float textSize = a.getDimension(R.styleable.IndexView_android_textSize, DensityUtil.dip2px(getContext(), 10));
        a.recycle();

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);

        fontMetrics = new Paint.FontMetrics();

        wordsPaint = new Paint();
        wordsPaint.setColor(Color.GRAY);
        wordsPaint.setAntiAlias(true);
        wordsPaint.setTextSize(textSize);
        wordsPaint.getFontMetrics(fontMetrics);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        itemWidth = getMeasuredWidth();
        itemHeight = (float) (getMeasuredHeight() / words.length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < words.length; i++) {
            //判断是不是我们按下的当前字母
            if (touchIndex == i) {
                if (isTouch) {
                    //绘制文字圆形背景
                    canvas.drawCircle(itemWidth / 2, itemHeight / 2 + i * itemHeight, -(fontMetrics.ascent + fontMetrics.descent), bgPaint);
                    wordsPaint.setColor(Color.WHITE);
                    //设置水滴view的移动
                    if (dropView != null) {
                        dropView.offsetTopAndBottom((int) (itemHeight / 2 + i * itemHeight - dropView.getMeasuredHeight() / 2));
                    }
                }
            } else {
                wordsPaint.setColor(Color.BLACK);
            }
            //获取文字的宽高
            float wordWidth = wordsPaint.measureText(words[i], 0, 1);
            float wordHeight = -(fontMetrics.ascent + fontMetrics.descent);
            //绘制字母
            float wordX = itemWidth / 2 - wordWidth / 2;
            float wordY = itemHeight / 2 + wordHeight / 2 + i * itemHeight;
            canvas.drawText(words[i], wordX, wordY, wordsPaint);
        }
    }

    /**
     * 当手指触摸按下的时候改变字母背景颜色
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouch = true;
                float y = event.getY();
                //关键点===获得我们按下的是那个索引(字母)
                int index = (int) (y / itemHeight);
                if (index != touchIndex) {
                    touchIndex = index;
                }
                //防止数组越界
                if (listener != null && 0 <= touchIndex && touchIndex <= words.length - 1) {
                    //去重
                        //回调按下的字母
                        listener.wordsChange(words[touchIndex]);
                        if (dropView != null) {
                            dropView.setWord(words[touchIndex]);
                        }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (dropView != null) {
                    dropView.setVisibility(View.GONE);
                }
                invalidate();
                performClick();
                break;
            default:
                break;
        }
        return true;
    }

    public void setDropView(DropView dropView) {
        this.dropView = dropView;
    }

    /**
     * 手指按下了哪个字母的回调接口
     */
    public interface OnWordsChangeListener {
        /**
         * .
         *
         * @param words .
         */
        void wordsChange(String words);
    }

    /**
     * 设置手指按下字母改变监听
     */
    public void setOnWordsChangeListener(OnWordsChangeListener listener) {
        this.listener = listener;
    }
}
