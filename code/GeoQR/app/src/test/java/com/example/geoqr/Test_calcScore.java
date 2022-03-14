package com.example.geoqr;

import org.junit.*;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.net.URL;

public class Test_calcScore {
    private CalculateScore test(){
        CalculateScore c = new CalculateScore("BFG5DGW54");
        return c;
    }

    @Test
    public void tests(){
        CalculateScore z = test();
        //z.byteArray_to_Result();
        z.Result_to_hexResult();

        assertEquals("dawdwad21",z.getQRHex());

        z.find_hex_cont();
        z.find_total();

        assertEquals(19,(int)z.find_total());
    }
}
