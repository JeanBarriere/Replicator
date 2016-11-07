package com.example.jean.replicator.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jean on 11/6/16.
 */

public class OtpURL {
    public String issuer;
    public String account;
    public String secret;
    public String digits;
    public String content;

    public OtpURL(String content) {
        try {
            this.content = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.content = "";
        }
        this.issuer = "";
        this.account = "";
        this.secret = "";
        this.digits = "6";
    }

    public boolean parse() {
        try {
            List<String> items = Arrays.asList(Arrays.asList(this.content.split("\\s*/\\s*")).get(3).split("\\s*\\?\\s*"));
            List<String> tinfos = Arrays.asList(items.get(1).split("\\s*\\&\\s*"));
            List<String> ainfos = Arrays.asList(items.get(0).split("\\s*:\\s*"));

            for (String in : tinfos) {
                List<String> kv = Arrays.asList(in.split("\\s*=\\s*"));

                if (kv.get(0).equals("secret"))
                    this.secret = kv.get(1);
                else if (kv.get(0).equals("issuer"))
                    this.issuer = kv.get(1);
                else if (kv.get(0).equals("digits"))
                    this.digits = kv.get(1);
            }

            this.account = ainfos.get(0);
            if (ainfos.size() == 2)
                this.account = ainfos.get(1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        if (this.account.length() == 0 || this.issuer.length() == 0 || this.secret.length() == 0)
            return false;
        return true;
    }
}
