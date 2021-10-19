package com.lottery.biying.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lottery.biying.R;
import com.lottery.biying.util.LotteryButtonOnCheckListener;
import com.lottery.biying.util.ScreenUtils;

/**
 * Created by Administrator on 2017/11/27.
 */
public class ButtonNumber extends LotteryButton {

    public boolean SelectStatus=false;
    public boolean PressStatus=false;
    public int DefaultWidth;
    public int DefaultHeight;
    public float DefaultTextSize=13;
    public Bitmap PressDrawable;
    Context context;
    WindowManager windowManager;
    View hululayout;
    public String value;
    private int statusbarHeight=0;
    LotteryButtonOnCheckListener onCheckListener;
    ButtonTypes buttonTypes;
    Object objects[];



    @Override
    public ButtonTypes getType() {

        return buttonTypes;
    }



    public Boolean getChecked()
    {
        return SelectStatus;
    }

    @Override
    public void setOnCheckListener(LotteryButtonOnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public void  setChecked(Boolean checked)
    {
         SelectStatus=checked;
            postInvalidate();
    }
    public void  setCheckedOpposite()
    {
        SelectStatus=!SelectStatus;
        postInvalidate();
    }

    @Override
    public void fillUpData(Object... obj) {
        objects=obj;
        TextView value1=(TextView)hululayout.findViewById(R.id.value1);
        TextView value2=(TextView)hululayout.findViewById(R.id.value2);
        value2.setText(value);
        value1.setText(value);
        if(value.length()>1 && !Character.isDigit(value.charAt(1)))
        {
            setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        }
        else
            invalidate();
    }

    @Override
    public void setText(String value)
    {
        this.value=value;

    }

    public String getText()
    {
        return value;
    }

    @Override
    public Object[] getData() {
        return objects;
    }

    @Override
    public View getCurrentView() {
        return this;
    }

    public void setTextSize(int value,int size)
    {
        DefaultTextSize=TypedValue.applyDimension(
                value, size, getContext().getResources().getDisplayMetrics());
        postInvalidate();
    }

    public ButtonNumber(Context context) {
        super(context);
        Init(context, null,ButtonTypes.Circle);
    }

    public ButtonNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context,attrs,ButtonTypes.Circle);
    }

    public ButtonNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs,ButtonTypes.Circle);
    }

    public void Init(Context context,AttributeSet attrs,ButtonTypes buttonTypes)
    {
        this.buttonTypes=buttonTypes;
        windowManager=((Activity)context).getWindowManager();
        hululayout=View.inflate(context, R.layout.cricle_button, null);

        this.context=context;
        PressDrawable= BitmapFactory.decodeResource(context.getResources(), R.drawable.hulu);
        DefaultWidth= ScreenUtils.getDIP2PX(context, 39)-2;
        DefaultHeight= ScreenUtils.getDIP2PX(context,39)-2;
//        setLayoutParams(new ViewGroup.LayoutParams(ScreenUtils.getDIP2PX(context, 39),ScreenUtils.getDIP2PX(context, 39)));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        if(attrs!=null)
        {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.ButtonNumber);
            if (a.hasValue(R.styleable.ButtonNumber_text)) {
                value = a.getText(R.styleable.ButtonNumber_text).toString();
            }
            if (a.hasValue(R.styleable.ButtonNumber_textsize)) {
                DefaultTextSize =  a.getDimension(R.styleable.ButtonNumber_textsize, DefaultTextSize);
            }
            a.recycle();
        }

        TextView value1=(TextView)hululayout.findViewById(R.id.value1);
        TextView value2=(TextView)hululayout.findViewById(R.id.value2);
        value2.setText(value);
        value1.setText(value);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width=MeasureSpec.getSize(widthMeasureSpec);
//        int height=MeasureSpec.getSize(heightMeasureSpec);
//        if(width>0)
//        DefaultWidth=MeasureSpec.getSize(widthMeasureSpec);
//        if(height>0)
//        DefaultHeight=MeasureSpec.getSize(heightMeasureSpec);

//        int width=PressDrawable.getWidth();
//        int height=PressDrawable.getHeight();
//        width=MeasureSpec.makeMeasureSpec(width,MeasureSpec.getMode(widthMeasureSpec));
//        height=MeasureSpec.makeMeasureSpec(height,MeasureSpec.getMode(heightMeasureSpec));
//        setMeasuredDimension(width,height);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(!SelectStatus)
        {
            Paint paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔
            paint.setStrokeWidth(2);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);//设置填满
            paint.setAntiAlias(true);
            canvas.drawCircle(DefaultWidth/2, DefaultHeight/2, DefaultWidth/2, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(context.getResources().getColor(R.color.line));
            canvas.drawCircle((DefaultWidth)/2, (DefaultWidth)/2, (DefaultWidth)/2-1, paint);
            drawText(paint,canvas);
        }
        else
        {
            Paint paint = new Paint(); //
            paint.setStrokeWidth(2);
            paint.setColor(0xFFEE1F3B);
            paint.setStyle(Paint.Style.FILL);//设置填满
            paint.setAntiAlias(true);
            canvas.drawCircle(DefaultWidth/2, DefaultWidth/2, DefaultWidth/2, paint);

            drawText(paint, canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                PressStatus=!PressStatus;
                Showhulu();
                postInvalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                PressStatus=!PressStatus;
                Hidehulu();
                if(onCheckListener!=null) {
                    boolean need=onCheckListener.OnChecking(this, SelectStatus);
                    if(need)
                    {
                        setCheckedOpposite();
                        onCheckListener.OnChecked(this, SelectStatus);
                    }
                }
                else {
                    setCheckedOpposite();
                }
                break;

        }

        return super.onTouchEvent(event);
    }

    public void drawText(Paint paint,Canvas canvas)
    {
        if(value!=null)
        {

            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(DefaultTextSize);
            float[] WH=getTextWH(paint,value);
            paint.setColor(SelectStatus ? Color.WHITE : 0xFFEE1F3B);
            if(WH[1]>DefaultHeight) {
                while (WH[1] < DefaultHeight) {
                    DefaultTextSize--;
                    paint.setTextSize(DefaultTextSize);
                    WH=getTextWH(paint,value);
                }
            }

//            paint.setTextAlign(Paint.Align.CENTER);
            Rect bounds = new Rect();
            paint.getTextBounds(value, 0, value.length(), bounds);
//            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(value,(DefaultWidth - paint.measureText(value)) / 2 ,baseline, paint);//
        }
    }

    public static float[] getTextWH(Paint mTextPaint,String text){
        float[] values=new float[2];
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textheight = fm.bottom - fm.top;
        values[0]=mTextPaint.measureText(text);
        values[1]=textheight;
        return  values;
    }

    public void Showhulu()
    {
        int[] screes=ScreenUtils.getScreenWH((Activity)context);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.FILL_PARENT
        lp.width=WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity= Gravity.CENTER;
        int[] location = new int[2] ;
        getLocationInWindow(location);
        if(statusbarHeight==0)
        {
            Rect rectangle= new Rect();
            ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
            statusbarHeight=rectangle.top;
        }
        lp.y=location[1]-screes[1]/2-statusbarHeight-ScreenUtils.getDIP2PX(context,2);//-2挡住屁股 下+ 上-+(PressDrawable.getHeight()-DefaultHeight)/2 201
        lp.x=location[0]-screes[0]/2+(PressDrawable.getWidth()/2)-(PressDrawable.getWidth()/2-DefaultWidth/2);
//        LogTools.e("坐标", location[1] + "  " + PressDrawable.getHeight()+"  "+DefaultHeight+"  "+statusbarHeight);
        lp.format = PixelFormat.A_8; // 设置图片格式，效果为背景透明
        windowManager.addView(hululayout, lp);

    }

    public void Hidehulu()
    {
        windowManager.removeView(hululayout);
    }

    public interface OnCheckListener
    {
        public boolean OnChecking(LotteryButton buttonNumber,boolean status);//需要改变状态的话返回true
        public void OnChecked(LotteryButton buttonNumber,boolean status);
    }
}
