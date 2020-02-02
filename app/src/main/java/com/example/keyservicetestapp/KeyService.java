package com.example.keyservicetestapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyService extends Service {
    private final IBinder myBinder = new MyBinder();
    private KeyPair myKeyPair;
    private SharedPreferences sharePref;
    PublicKey myPublicKey;
    PrivateKey myPrivateKey;

    public KeyService() throws NoSuchAlgorithmException {
    }

    public class MyBinder extends Binder {
        KeyService getService(){
            return KeyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (null != sharePref) {
            //Retrieve Key Pair
            String publicKey = sharePref.getString(getString(R.string.my_public_key), null);
            String privateKey = sharePref.getString(getString(R.string.my_private_key), null);
            if (null != publicKey) {
                //-----Retrieve Public Key
                byte[] publicBytes = Base64.getDecoder().decode(publicKey.getBytes());
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                try {
                    myPublicKey = kf.generatePublic(keySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                //-----Retrieve Private Key
                byte[] privateBytes = Base64.getDecoder().decode(privateKey.getBytes());
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

    public KeyPair getMyKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(2048);
        if (null == myKeyPair){
            myKeyPair = keygen.generateKeyPair();
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putString(getString(R.string.my_public_key),Base64.getEncoder().encodeToString(myKeyPair.getPublic().getEncoded()));
            editor.putString(getString(R.string.my_private_key),Base64.getEncoder().encodeToString(myKeyPair.getPrivate().getEncoded()));
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

    public RSAPublicKey getPublicKey(String partnerName) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKey = sharePref.getString(partnerName, null);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (null != publicKey){
            byte[] publicBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keyspec = new X509EncodedKeySpec(publicBytes);
            RSAPublicKey key = key = (RSAPublicKey) keyFactory.generatePublic(keyspec);
            return key;
        } else {
            return null;
        }
    }

    public String returnMyPublicKeyFromSharePref(){
        return sharePref.getString(getString(R.string.my_public_key),null);
    }

    public String returnMyPrivateKeyFromSharePref(){
        return sharePref.getString(getString(R.string.my_private_key),null);
    }

    public void resetMyKeyPair() {
        SharedPreferences.Editor editor = sharePref.edit();
        editor.remove(getString(R.string.my_public_key));
        editor.remove(getString(R.string.my_private_key));
        editor.apply();
        myKeyPair = null;
    }

    public void resetKey(String partnerName){
        SharedPreferences.Editor editor = sharePref.edit();
        editor.remove(partnerName);
        editor.apply();
    }
}
