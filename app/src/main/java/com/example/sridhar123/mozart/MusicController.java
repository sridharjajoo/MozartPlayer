package com.example.sridhar123.mozart;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import android.widget.MediaController;
import android.widget.MediaController;

/**
 * Created by sridhar123 on 11/12/16.
 */

public class MusicController extends MediaController {


    private Context c;
    public MusicController(Context c) {

       super(c);
        this.c = c;
        Log.v("MusicController","Inside MusicController class");
    }

    public void hide() {
    }

}
