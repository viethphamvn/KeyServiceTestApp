package com.example.keyservicetestapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

public class KeyService extends Service {
    IBinder myBinder = new MyBinder();

    public KeyService() {
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
        return;
    }

    public void storePublicKey(String partnerName, String publicKey){

    }

    public RSAPublicKey getPublicKey(String partnerName){
        return;
    }

    public void resetMyKeyPair(){

    }

    public void resetKey(String partnerName){

    }
}
