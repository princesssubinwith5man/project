package org.techtown.myapplication;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class GetPhone {
    private Context mContext;

    public GetPhone(Context context){
        mContext = context;
    }

    public String getNumber(String facName) throws IOException {
        //String path = System.getProperty("user.dir");

        BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getAssets().open("phone_number2.csv")));
        String line = "";

        while((line = br.readLine()) != null){
            String[] token = line.split(",");
            if(token[4].equals(facName) && token.length==8){
                token[7] = token[7].replace("-", "");
                return token[7];
            }
        }

        return null;
    }
}
