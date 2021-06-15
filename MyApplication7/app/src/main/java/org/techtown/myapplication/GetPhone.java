package org.techtown.myapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class GetPhone {
    public String getNumber(String facName) throws IOException {
        //String path = System.getProperty("user.dir");
        File file = new File("phone_number.csv");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line = "";

        while((line = br.readLine()) != null){
            String[] token = line.split(",", -1);
            if(token[4].equals(facName)){
                return token[8];
            }
        }


        return null;
    }
}
