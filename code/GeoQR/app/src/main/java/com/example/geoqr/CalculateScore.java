package com.example.geoqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.core.motion.utils.Utils;

import com.google.common.hash.Hashing;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculateScore{

    private byte[] qr_byte;
    private Bitmap bmp;
    private String Result = "";
    private String hex_result = "";

    private char[] hex_result_array;

    ArrayList<String> continuse;


    public CalculateScore(byte[] qr){
        this.qr_byte = qr;
    }

    private void byteArray_to_Result(){
        Result result = null;

        // array byte to bitmap
        // https://stackoverflow.com/questions/13854742/byte-array-of-image-into-imageview
        bmp = BitmapFactory.decodeByteArray(qr_byte, 0, qr_byte.length);

        // array byte to string result
        Result = new String(qr_byte, StandardCharsets.UTF_8); // for UTF-8 encoding

        return;
    }

    private void Result_to_hexResult(){
        // hash result to hex
        // https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
        hex_result = Hashing.sha256()
                .hashString(Result, StandardCharsets.UTF_8)
                .toString();

        // hex to chararray for next step simplicity
        // https://www.geeksforgeeks.org/iterate-over-the-characters-of-a-string-in-java/#:~:text=In%20this%20approach%2C%20we%20convert,loop%20or%20for%2Deach%20loop.
        hex_result_array = hex_result.toCharArray();

        return;
    }

    private void find_hex_cont(){
        char prev = ' ';
        char prev_inlist = ' ';
        int index = 0;
        continuse = new ArrayList<String>();

        // extracts groups from char array
        for (int i = 0; i < hex_result_array.length; i++){
            if (prev == hex_result_array[i]){

                if (index == 0){
                    continuse.add(index,String.valueOf(hex_result_array[i]));
                    prev_inlist = hex_result_array[i];
                }else if(prev_inlist == hex_result_array[i]){
                    continuse.set(index, continuse.get(index)+String.valueOf(hex_result_array[i]));
                }else{
                    index += 1;
                    continuse.add(index,String.valueOf(hex_result_array[i]));
                }
            }
            prev = hex_result_array[i];
        }
        return;
    }

    public Integer find_total(){
        // to get the above pre-condition so can get result
        byteArray_to_Result();
        Result_to_hexResult();
        find_hex_cont();


        Integer total = 0;
        long temp;
        Hashtable<String, Integer> hex_dict = new Hashtable<String, Integer>();

        // https://www.educative.io/edpresso/how-to-create-a-dictionary-in-java
        // use in pick out group
        hex_dict.put("0",0);
        hex_dict.put("1",1);
        hex_dict.put("2",2);
        hex_dict.put("3",3);
        hex_dict.put("4",4);
        hex_dict.put("5",5);
        hex_dict.put("6",6);
        hex_dict.put("7",7);
        hex_dict.put("8",8);
        hex_dict.put("9",9);

        hex_dict.put("a",10);
        hex_dict.put("b",11);
        hex_dict.put("c",12);
        hex_dict.put("d",13);
        hex_dict.put("e",14);
        hex_dict.put("f",15);

        // calculate each group score and add together
        for (int i = 0; i < continuse.size(); i++ ){
            temp = continuse.get(i).chars().count();
            if (temp > 1){
                total += (int) Math.pow(hex_dict.get(continuse.get(i)), temp);
            }else{
                total += hex_dict.get(continuse.get(i));
            }
        }
        return total;
    }

    // getters
    public String getQRhex(){
        return hex_result;
    }

    public Bitmap getBitmap(){
        return bmp;
    }
}
