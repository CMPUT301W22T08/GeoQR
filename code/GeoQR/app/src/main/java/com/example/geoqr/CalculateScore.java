package com.example.geoqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.hash.Hashing;
//import com.google.zxing.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;

public class CalculateScore{

    //    private byte[] qr_byte;
//    private Bitmap bmp;
    private String Result = "";
    private String hex_result = "";

    private char[] hex_result_array;

    ArrayList<String> continuous;

    /**
     * get the content of the qr to Result
     * @param qr-string item that the content of qr
     */
    public CalculateScore(String qr){
        //this.qr_byte = qr;
        this.Result = qr;
    }

//    public void byteArray_to_Result(){
//        Result result = null;
//
//        // array byte to bitmap
//        // https://stackoverflow.com/questions/13854742/byte-array-of-image-into-imageview
//        bmp = BitmapFactory.decodeByteArray(qr_byte, 0, qr_byte.length);
//
//        // array byte to string result
//        Result = new String(qr_byte, StandardCharsets.UTF_8); // for UTF-8 encoding
//    }

    /**
     * turn result to incoded hex result
     * output the hex result array
     */
    public void Result_to_hexResult(){
        // hash result to hex
        // https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
        hex_result = Hashing.sha256()
                .hashString(Result, StandardCharsets.UTF_8)
                .toString();

        // hex to char-array for next step simplicity
        // https://www.geeksforgeeks.org/iterate-over-the-characters-of-a-string-in-java/#:~:text=In%20this%20approach%2C%20we%20convert,loop%20or%20for%2Deach%20loop.
        hex_result_array = hex_result.toCharArray();

    }

    /**
     * within the hex result array, get the chained up ones and store inside string called continuse
     */
    public void find_hex_cont(){
        char prev = ' ';
        char prev_inList = ' ';
        int index = 0;
        continuous = new ArrayList<>();

        // extracts groups from char array
        for (int i = 0; i < hex_result_array.length; i++){
            if (prev == hex_result_array[i]){

                if (index == 0){
                    continuous.add(index,String.valueOf(hex_result_array[i]));
                    prev_inList = hex_result_array[i];
                } else if(prev_inList == hex_result_array[i]){
                    continuous.set(index, continuous.get(index)+ hex_result_array[i]);
                } else{
                    index += 1;
                    continuous.add(index,String.valueOf(hex_result_array[i]));
                }
            }
            prev = hex_result_array[i];
        }
    }

    /**
     * using the continuse string and check one by one in hashtable hex_dict for the int result
     * @return total - the int score of the qr code
     */
    public Integer find_total(){
        // to get the above pre-condition so can get result
        // byteArray_to_Result();
        Result_to_hexResult();
        find_hex_cont();


        Integer total = 0;
        long temp;
        Hashtable<String, Integer> hex_dict = new Hashtable<>();

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
        for (int i = 0; i < continuous.size(); i++ ){
            temp = continuous.get(i).chars().count();
            if (temp > 1){
                total += (int) Math.pow(hex_dict.get(continuous.get(i)), temp);
            }else{
                total += hex_dict.get(continuous.get(i));
            }
        }
        return total;
    }

    /**
     * return hex array for namming referance
     * @return hex_result - the hex result string format
     */
    // getters
    public String getQRHex(){
        return hex_result;
    }

//    public Bitmap getBitmap(){
//        return bmp;
//    }

}
