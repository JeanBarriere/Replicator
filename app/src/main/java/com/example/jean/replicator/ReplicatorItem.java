package com.example.jean.replicator;

/*
** Created by Jean BARRIERE
** ReplicatorItem class is the account item. It contains all the properties of an account and functions to generate the temporary code
*/

import org.apache.commons.codec.binary.Base32;

import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ReplicatorItem implements Serializable {
    private byte[] secret_key; // the secret key
    private String name_deliver; // the deliver : Google, GitHub or any one
    private String name_account; // the account's name
    private static final String crypto = "HmacSHA1"; // the crypto type

    public ReplicatorItem(String secret_key, String name_deliver, String name_account) {
        this.name_deliver = name_deliver;
        Base32 base32 = new Base32();
        this.secret_key = base32.decode(secret_key.replaceAll("\\s+", "").toUpperCase().getBytes());
        this.name_account = name_account;
    }

    public void setSecretKey(byte[] secret_key) {
        this.secret_key = secret_key;
    }

    public byte[] getSecretKey() {
        return secret_key;
    }

    // hmacSha() encode the date into a byte array with the secret key
    private byte[] hmacSha() {
        try {
            Date current = new Date();
            byte[] msg = ByteBuffer.allocate(8).putLong(current.getTime() / 30000).array();
            Mac hmac = Mac.getInstance(crypto);
            hmac.init(new SecretKeySpec(this.secret_key, "RAW"));
            return hmac.doFinal(msg);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    // genCode() uses hmacSha() to convert the byte array to an integer
    private int genCode() {
        byte[] hash = hmacSha();
        int offset = hash[hash.length - 1] & 0xf;
        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        return binary % 1000000;
    }

    // getTimeLeft() return the milliseconds until a new code is required
    public long getTimeLeft() {
        Date current = new Date();
        return (30000 - (current.getTime() % 30000));
    }

    // getCode() format the genCode() int to a beautiful and readable string
    public String getCode() {
        return String.format("%03d %03d", genCode() / 1000, genCode() % 1000);
    }

    public String getAccountName() {
        return this.name_account;
    }

    public String getDeliverName() {
        return name_deliver;
    }

    // Switch case to get the icon of the deliver.
    public int getIcon() {
        switch (name_deliver) {
            case "Google":
                return R.drawable.ic_logo_google;
            case "GitHub":
                return R.drawable.ic_logo_github;
            case "Facebook":
                return R.drawable.ic_logo_facebook;
            default:
                return R.drawable.ic_logo_unknown;
        }
    }
}
