package com.up.set.drivecell.customfont;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by amush on 12-Nov-17.
 */

public class TypeFactory {

    private String A_BOOK= "AvenirLTStd-Book.otf";
    private String A_LIGHT="AvenirLTStd-Light.otf";
    private String A_ROMAN="AvenirLTStd-Roman.otf";



    Typeface avenirBook;
    Typeface avenirLight;
    Typeface avenirRoman;


    public TypeFactory(Context context){
        avenirBook = Typeface.createFromAsset(context.getAssets(),A_BOOK);
        avenirLight = Typeface.createFromAsset(context.getAssets(),A_LIGHT);
        avenirRoman = Typeface.createFromAsset(context.getAssets(),A_ROMAN);

    }

}
