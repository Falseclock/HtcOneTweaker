package kz.virtex.htc.tweaker;

import android.app.Application;
import android.content.Context;

public class Contexter extends Application
{
    private static Contexter mContexter = null;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContexter = this;
    }
    public static Context get()
    {
        return mContexter.getApplicationContext();
    }
}
