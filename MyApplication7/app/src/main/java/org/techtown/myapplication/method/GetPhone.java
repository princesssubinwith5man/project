package org.techtown.myapplication.method;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetPhone {
    // 파일 읽어서 전화번호 매칭해 주는 클래스임
    private final Context mContext;

    public GetPhone(Context context) {
        mContext = context;
    }

    public String getNumber(String facName) throws IOException {
        //String path = System.getProperty("user.dir");

        BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getAssets().open("phone_number.csv")));
        String line = "";

        while ((line = br.readLine()) != null) {
            String[] token = line.split(",");
            if (token[4].equals(facName) && token.length == 8) {
                token[7] = token[7].replace("-", "");
                return token[7];
            }
        }

        return null;
    }
}
