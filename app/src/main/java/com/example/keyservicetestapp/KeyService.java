package com.example.keyservicetestapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyService extends Service {
    private final IBinder myBinder = new MyBinder();
    KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
    private KeyPair myKeyPair;
    private SharedPreferences sharePref;
    PublicKey myPublicKey;
    PrivateKey myPrivateKey;
    private KeyFactory kf = KeyFactory.getInstance("RSA");

    public KeyService() throws NoSuchAlgorithmException, InvalidKeySpecException {
    }

    public class MyBinder extends Binder {
        KeyService getService(){
            return KeyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        if (null != sharePref) {
            //Retrieve Key Pair
            String publicKey = sharePref.getString("mypublic", null);
            String privateKey = sharePref.getString("myprivate", null);
            if (null != publicKey) {
                //-----Retrieve Public Key
                byte[] publicBytes = Base64.getDecoder().decode(publicKey);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                try {
                    myPublicKey = kf.generatePublic(keySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                //-----Retrieve Private Key
                byte[] privateBytes = Base64.getDecoder().decode(privateKey);
                keySpec = new X509EncodedKeySpec(privateBytes);
                try {
                    myPrivateKey = kf.generatePrivate(keySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new NullPointerException();
        }
        return myBinder;
    }

    public KeyPair getMyKeyPair() {
        keygen.initialize(2048);
        if (null == myKeyPair){
            myKeyPair = keygen.generateKeyPair();
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putString("mypublic",Base64.getEncoder().encodeToString(myKeyPair.getPublic().getEncoded()));
            editor.putString("myprivate",Base64.getEncoder().encodeToString(myKeyPair.getPrivate().getEncoded()));
            editor.commit();
            return null;
        } else {
            return myKeyPair;
        }
    }

    public void storePublicKey(String partnerName, String publicKey){
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString(partnerName, publicKey);
        editor.commit();
    }

    public RSAPublicKey getPublicKey(String partnerName) throws InvalidKeySpecException {
        String publicKey = sharePref.getString(partnerName, null);
        if (null != publicKey){
            byte[] publicBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keyspec = new X509EncodedKeySpec(publicBytes);
            RSAPublicKey key = (RSAPublicKey) kf.generatePublic(keyspec);
            return key;
        } else {
            return null;
        }
    }

    public String returnMyPublicKeyFromSharePref(){
        return sharePref.getString("mypublic",null);
    }

    public String returnMyPrivateKeyFromSharePref(){
        return sharePref.getString("myprivate",null);
    }

    public void resetMyKeyPair() {
        SharedPreferences.Editor editor = sharePref.edit();
        editor.remove("mypublic");
        editor.remove("myprivate");
        editor.apply();
        myKeyPair = null;
    }

    public void resetKey(String partnerName){
        SharedPreferences.Editor editor = sharePref.edit();
        editor.remove(partnerName);
        editor.apply();
    }
}
