package com.sj.contactlistview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sj.contactlistview.R;
import com.sj.contactlistview.util.DensityUtil;

/**
 * @author SJ
 */
public class DropView extends View {
    /**
     * 字母画笔
     */
    private Paint wordsPaint;
    /**
     * 字母背景画笔
     */
    private Paint bgPaint;
    /**
     * 宽度
     */
    private float width;
    /**
     * 高度
     */
    private float height;
    /**
     * 字母平均高度的api
     */
    private Paint.FontMetrics fontMetrics;
    /**
     * 画笔路径
     */
    private Path path;
    private RectF rectF;
    /**
     * 水滴圆的半径
     */
    private float radio = 0;
    /**
     * 索引字符
     */
    private String word;

    public DropView(Context context) {
        this(context, null);
    }

    public DropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropView);
        //选中时的圆形背景颜色
        int bgColor = a.getColor(R.styleable.DropView_background_color, Color.GRAY);
        //字母大小，推荐为dp，可不受系统字体大小更改影响
        float textSize = a.getDimension(R.styleable.DropView_android_textSize, DensityUtil.dip2px(getContext(), 20));
        a.recycle();

        path = new Path();
        rectF = new RectF();

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);

        fontMetrics = new Paint.FontMetrics();
        wordsPaint = new Paint();
        wordsPaint.setColor(Color.WHITE);
        wordsPaint.setAntiAlias(true);
        wordsPaint.setTextSize(textSize);
        wordsPaint.getFontMetrics(fontMetrics);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        height = height > width ? width : height;
        width = height < width ? height : width;

        initPath();
    }

    /**
     * 设置水滴图形路径
     */
    private void initPath() {
        radio = radio == 0 ? getRadio(width, height) : radio;
        path.moveTo(width, height / 2);
        rectF.top = height / 2 - radio;
        rectF.left = width / 2 - radio;
        rectF.bottom = rectF.top + radio * 2;
        rectF.right = rectF.left + radio * 2;
        path.arcTo(rectF, 45, 270);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制水滴
        canvas.drawPath(path, bgPaint);
        if (word != null) {
            //获取文字的宽高
            float wordWidth = wordsPaint.measureText(word, 0, 1);
            float wordHeight = -(fontMetrics.ascent + fontMetrics.descent);
            //绘制字母
            float wordX = width / 2 - wordWidth / 2;
            float wordY = height / 2 + wordHeight / 2;
            canvas.drawText(word, wordX, wordY, wordsPaint);
        }
    }

    private float getRadio(float width, float height) {
        return (float) ((Math.sqrt(width / 2 * width / 2 + (height / 2 * height / 2))) / 2);
    }

    public void setWord(String word) {
        this.word = word;
        invalidate();
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
    }
}
