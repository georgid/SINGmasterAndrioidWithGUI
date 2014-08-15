package com.kamengoranchev.singmaster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kamen.goranchev on 28.07.2014.
 */
public class VoiceChart extends View {

    private static float[] points = new float[]{10, 111, 20, 134, 30, 150, 40, 144, 50, 249, 60, 301, 70, 111, 80, 111};

    public VoiceChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLines(points, 0, points.length, new Paint());
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
