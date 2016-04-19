package com.kuang.deleteslideview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by KZY on 2016/4/19.
 */
public class SlideDeleteView extends ViewGroup {

    private View mSlide_content;
    private View mSlide_delete;
    private Scroller mScroller;

    public SlideDeleteView(Context context) {
        super(context, null);
    }


    public SlideDeleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //卷动器
        mScroller = new Scroller(context);
    }


    //布局完成
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("tagtag", "onFinishInflate");

        mSlide_content = getChildAt(0);
        mSlide_delete = getChildAt(1);

    }


    //测量,确定大小

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("tagtag","onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mSlide_content.measure(widthMeasureSpec, heightMeasureSpec);

        //精确测量delete宽度
        LayoutParams layoutParams = mSlide_delete.getLayoutParams();
        int          width_delete = layoutParams.width;
        int          width_delete_measureSpec  = MeasureSpec.makeMeasureSpec(width_delete, MeasureSpec.EXACTLY);
        mSlide_delete.measure(width_delete_measureSpec,heightMeasureSpec);

        //俩参数是不带模式的
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));

    }


    //确定摆放位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("tagtag","onLayout");


        int dx = 0;//mSlide_delete.getMeasuredWidth();

        //摆放content位置
        int left_content = 0-dx;
        int top_content = 0;
        int right_content = mSlide_content.getMeasuredWidth()-dx;
        int bottom_content = mSlide_content.getMeasuredHeight();

        mSlide_content.layout(left_content,top_content,right_content,bottom_content);


        //摆放delete的位置
        int left_delete = mSlide_content.getMeasuredWidth()-dx;
        int top_delete = 0;
        int right_delete = mSlide_content.getMeasuredWidth()+mSlide_delete.getMeasuredWidth()-dx;
        int bottom_delete = mSlide_delete.getMeasuredHeight();

        mSlide_delete.layout(left_delete,top_delete,right_delete,bottom_delete);
    }


    //移动(移动屏幕要简单一点，移动控件需要再调用onlayout方法进行重新摆放)
    float downX;
    float downY;
    boolean isShowDelete = false;//是否要显示删除
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:

                Log.d("tagtag","down");

                 downX = event.getRawX();
                 downY = event.getRawY();

                break;

            case MotionEvent.ACTION_MOVE:

                //Log.d("tagtag","ACTION_MOVE");

                float moveX = event.getRawX();
                float moveY = event.getRawY();
                int screenX = getScrollX();//屏幕(左上角)当前位置的x坐标
                int dx = Math.round(downX-moveX);//屏幕即将要改变的值，与手势位移相反

                //if (moveY<=mSlide_content.getMeasuredHeight()){

                    if (screenX+dx<0){
                        //左边越界
                        scrollTo(0,0);//屏幕绝对位置
                    }else if (screenX+dx>mSlide_delete.getMeasuredWidth()){
                        //右边越界

                        scrollTo(mSlide_delete.getMeasuredWidth(),0);
                    }else {


                        scrollBy(dx,0);//屏幕相对位置

                    }

                    downX = moveX;
                    downY = moveY;

                //}

                break;

            case MotionEvent.ACTION_UP:

                float upX = event.getRawX();
                float upY = event.getRawY();
                screenX = getScrollX();

                Log.d("tagtag","ACTION_UP");
                if (screenX<mSlide_delete.getMeasuredWidth()/2){
                    //隐藏删除，由动画完成
                    isShowDelete = false;
                    //scrollTo(0,0);
                    //closeDelete();

                    Log.d("tagtag","隐藏删除");

                }else {
                    //显示删除，由动画完成
                    isShowDelete = true;

                    //scrollTo(mSlide_delete.getMeasuredWidth(),0);
                    //showDelete();
                    Log.d("tagtag","显示删除");

                }
                //屏幕移动的动画
                startScrollAnimation(isShowDelete);

                if (mOnViewSlideFinishListener !=null){

                    mOnViewSlideFinishListener.setDeleteIsShow(isShowDelete);
                }

                break;



        }

        return true;
    }

    //view屏幕移动(显示或隐藏删除)的动画
    public void startScrollAnimation(boolean isShowDelete) {

        int screenX = getScrollX();//屏幕(左上角)当前位置的x坐标

        int startX = screenX;

        int startY = 0;

        int endY = 0;

        int dy = endY - startY;

        int endX;
        if (isShowDelete){

            //显示删除
             endX = mSlide_delete.getMeasuredWidth();
        }else {

            //隐藏删除
            endX = 0;
        }
        int dx = endX - startX;

        int duration = Math.abs(dx*5);// =500;
        if (duration > 500){

            duration = 500;
        }

        mScroller.startScroll(startX,startY,dx,dy,duration);

        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            //让屏幕不停的移动到currX
            scrollTo(currX, 0);
            invalidate();
        }
    }


    //显示删除
    public void showDelete(){
//
//        isShowDelete = true;
//
//        startScrollAnimation(isShowDelete);

        scrollTo(mSlide_delete.getMeasuredWidth(),0);//无动画
    }
    //隐藏删除
    public void closeDelete(){

//        isShowDelete = false;
//
//        startScrollAnimation(isShowDelete);

        scrollTo(0,0);//无动画
    }



    //如果该自定义view用在了listview或其他容器里，那么这些容器会把touch事件屏蔽掉或拦截处理，所以就得申请父控件不拦截横向事件了
    float downX_disp;
    float downY_disp;
    float downTime;
    //覆盖dispatchTouchEvent申请父控件不拦截，避免父控件拦截掉事件，和完成单击事件(关闭或打开左侧菜单)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //完成申请父控件不拦截
        switch (ev.getAction()){

            case MotionEvent.ACTION_DOWN:

                downX_disp = ev.getRawX();
                downY_disp = ev.getRawY();
               // downTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:

                float moveX_disp = ev.getRawX();
                float moveY_disp = ev.getRawY();

                float dx = moveX_disp - downX_disp;
                float dy = moveY_disp - downY_disp;

                if (Math.abs(dx)>Math.abs(dy)){
                    //横向,申请父控件(listview...)不拦截

                    requestDisallowInterceptTouchEvent(true);

                }else {
                    //纵向，对事件不做处理，也不需要再分发下去(给ontouch)了
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
/*
                //完成单击事件
                // 1. 点的位置不变
                // 2. 时间差小于500

                float upX_disp = ev.getRawX();
                float upY_disp = ev.getRawY();
                //float upTime = System.currentTimeMillis();

               if ((Math.abs(upX_disp-downX_disp)<5)&&(Math.abs(upY_disp-downY_disp)<5)){

                    if (upTime-downTime<50){

                        //单击

                        if (upY_disp>200 && isMenuOpen && upX_disp>mView_menu.getMeasuredWidth()){//200:标题栏的大概高度

                            //关闭或打开左侧菜单
                            toggle();
                            return true;//是单击，对事件做了toggle()处理，也不需要再分发下去了。
                        }

                    }

                }
*/


                break;
        }

        return super.dispatchTouchEvent(ev);
    }



    public interface OnViewSlideFinishListener{

        void setDeleteIsShow(boolean deleteIsShow);
    }
    public OnViewSlideFinishListener mOnViewSlideFinishListener;
    public void setOnViewSlideFinishListener(OnViewSlideFinishListener onViewSlideFinishListener){

        mOnViewSlideFinishListener = onViewSlideFinishListener;
    }
}
