package dam.pmdm.spyrothedragon;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class FlameView extends View {
    private Paint paint;

    public FlameView(Context context) {
        super(context);
        init();
    }

    public FlameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        setBackgroundColor(Color.TRANSPARENT);
        startAnimation(); // Iniciar la animación al crearse la vista
    }

    private void startAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.5f, 1.0f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);

        scaleX.setDuration(700);
        scaleY.setDuration(700);
        fadeOut.setDuration(700);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, fadeOut);
        animatorSet.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setAntiAlias(true);

        Paint orangePaint = new Paint();
        orangePaint.setColor(Color.parseColor("#FF6600"));
        orangePaint.setStyle(Paint.Style.FILL);
        orangePaint.setAntiAlias(true);

        Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStyle(Paint.Style.FILL);
        yellowPaint.setAntiAlias(true);

        Path flamePath = new Path();
        flamePath.moveTo(width / 2f, 0);
        flamePath.lineTo(width, height * 3f / 4f);
        flamePath.lineTo(width / 2f, height);
        flamePath.lineTo(0, height * 3f / 4f);
        flamePath.close();

        canvas.drawPath(flamePath, redPaint);

        Path innerFlamePath = new Path();
        innerFlamePath.moveTo(width / 2f, height / 4f);
        innerFlamePath.lineTo(width * 3f / 4f, height * 3f / 4f);
        innerFlamePath.lineTo(width / 2f, height);
        innerFlamePath.lineTo(width / 4f, height * 3f / 4f);
        innerFlamePath.close();

        canvas.drawPath(innerFlamePath, orangePaint);

        Path coreFlamePath = new Path();
        coreFlamePath.moveTo(width / 2f, height / 2f);
        coreFlamePath.lineTo(width * 2f / 3f, height * 3f / 4f);
        coreFlamePath.lineTo(width / 2f, height);
        coreFlamePath.lineTo(width / 3f, height * 3f / 4f);
        coreFlamePath.close();

        canvas.drawPath(coreFlamePath, yellowPaint);
    }
}
