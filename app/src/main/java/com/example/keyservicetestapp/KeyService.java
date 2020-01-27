package com.example.keyservicetestapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Hashtable;

public class KeyService extends Service {
    IBinder myBinder = new MyBinder();
    KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
    private KeyPair myKeyPair;
    Hashtable<String,String> partner = new Hashtable<String,String>();

    public KeyService() throws NoSuchAlgorithmException {
    }

    public class MyBinder extends Binder {
        KeyService getService(){
            return KeyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;
    }

    public KeyPair getMyKeyPair(){
        keygen.initialize(2048);
        if (myKeyPair.equals(null)){
            myKeyPair = keygen.generateKeyPair();
        } else {
            return myKeyPair;
        }
        return null;
    }

    public void storePublicKey(String partnerName, String publicKey){
        partner.put(partnerName,publicKey);
    }

    public RSAPublicKey getPublicKey(String partnerName) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String key = partner.get(partnerName);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
        RSAPublicKey pubkey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        return pubkey;
    }

    public void resetMyKeyPair() {
        myKeyPair = null;
    }

    public void resetKey(String partnerName){
        partner.replace(partnerName, null);
    }
}
