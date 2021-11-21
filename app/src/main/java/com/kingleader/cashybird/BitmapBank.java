package com.kingleader.cashybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.kingleader.cashybird.R;

public class BitmapBank {

    Bitmap background;
    Bitmap[] bird;
    Bitmap tubeTop, tubeBottom;
    Bitmap redTubeTop, redTubeBottom,indigo_tube_top,orange_tube_bottom,magenta_tube_top,
            pink_tube_bottom,moon_tube_top,star_tube_bottom;

    public BitmapBank(Resources res,int bg) {
        background = BitmapFactory.decodeResource(res, bg);
        background = scaleImage(background);
        bird = new Bitmap[4];
        bird[0] = BitmapFactory.decodeResource(res, R.drawable.bird_frame1);
        bird[1] = BitmapFactory.decodeResource(res, R.drawable.bird_frame2);
        bird[2] = BitmapFactory.decodeResource(res, R.drawable.bird_frame3);
        bird[3] = BitmapFactory.decodeResource(res, R.drawable.bird_frame4);
        tubeTop = BitmapFactory.decodeResource(res, R.drawable.pipe_top);
        tubeBottom = BitmapFactory.decodeResource(res, R.drawable.pipe_bottom);
        redTubeTop = BitmapFactory.decodeResource(res, R.drawable.red_tube_top);
        redTubeBottom = BitmapFactory.decodeResource(res, R.drawable.red_tube_bottom);
        indigo_tube_top = BitmapFactory.decodeResource(res, R.drawable.indigo_tube_top);
        orange_tube_bottom = BitmapFactory.decodeResource(res, R.drawable.orange_tube_bottom);
        magenta_tube_top = BitmapFactory.decodeResource(res, R.drawable.magenta_tube_top);
        pink_tube_bottom = BitmapFactory.decodeResource(res, R.drawable.pink_tube_bottom);
        moon_tube_top = BitmapFactory.decodeResource(res, R.drawable.moon_tube_top);
        star_tube_bottom = BitmapFactory.decodeResource(res, R.drawable.star_tube_bottom);
    }

    // Return Red Tube-Top Bitmap
    public Bitmap getRedTubeTop(){
        return redTubeTop;
    }

    // Return Red Tube-Bottom Bitmap
    public Bitmap getRedTubeBottom(){
        return redTubeBottom;
    }

    // Return Tube-Top Bitmap
    public Bitmap getTubeTop(){
        return tubeTop;
    }

    // Return Tube-Bottom Bitmap
    public Bitmap getTubeBottom(){
        return tubeBottom;
    }

    public Bitmap getIndigoTubeTop(){
        return indigo_tube_top;
    }
    public Bitmap getOrangeTubeBottom(){ return orange_tube_bottom; }
    public Bitmap getMagentaTubeTop(){ return magenta_tube_top; }
    public Bitmap getPinkTubeBottom(){ return pink_tube_bottom; }
    public Bitmap getMoonTubeTop(){ return moon_tube_top; }
    public Bitmap getStarTubeBottom(){ return star_tube_bottom; }

    //Return Tube-width
    public int getTubeWidth(){
        return tubeTop.getWidth();
    }

    //Return Tube-height
    public int getTubeHeight(){
        return tubeTop.getHeight();
    }

    // Return bird bitmap of frame
    public Bitmap getBird(int frame){
        return bird[frame];
    }

    public int getBirdWidth(){
        return bird[0].getWidth();
    }

    public int getBirdHeight(){
        return bird[0].getHeight();
    }

    //Return background bitmap
    public Bitmap getBackground(){
        return background;
    }

    //Return background width
    public int getBackgroundWidth(){
        return background.getWidth();
    }

    //Return background height
    public int getBackgroundHeight(){
        return background.getHeight();
    }

    public Bitmap scaleImage(Bitmap bitmap){
        float widthHeightRatio = getBackgroundWidth() / getBackgroundHeight();
        /*
        We'll multiply widthHeightRatio with screenHeight to get scaled width of the bitmap.
        Then call createScaledBitmap() to create a new bitmap, scaled from an existing bitmap, when possible.
         */
        int backgroundScaledWidth = (int) widthHeightRatio * AppConstants.SCREEN_HEIGHT;
        return Bitmap.createScaledBitmap(bitmap, backgroundScaledWidth, AppConstants.SCREEN_HEIGHT, false);
    }
}
