package com.example.geoqr;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public class Test_calcScore {
    private CalculateScore test(){
//        String url = "https://chart.googleapis.com/chart?cht=qr&chl=BFG5DGW54&chs=180x180&choe=UTF-8&chld=L|2";
//        byte[] fileContent = new byte[0];
//        try {
//            fileContent = IOUtils.toByteArray(new URL(url));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////
////        PowerMockito.mockStatic(testScore.class);

        CalculateScore c = new CalculateScore("BFG5DGW54");
        return c;
    }

    @Test
    void tests(){
        CalculateScore z = test();
        //z.byteArray_to_Result();
        z.Result_to_hexResult();
        z.find_hex_cont();
        z.find_total();

        assertEquals(19,(int)z.find_total());
    }
}
