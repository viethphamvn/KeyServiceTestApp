package com.example.keyservicetestapp;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();


    KeyService service;

    public ExampleInstrumentedTest() throws TimeoutException {
        Intent serviceIntent =
                new Intent(ApplicationProvider.getApplicationContext(),KeyService.class);
        IBinder binder = serviceRule.bindService(serviceIntent);
        service =
                ((KeyService.MyBinder) binder).getService();
    }

    @Test
    public void CreatedPubKeyandReturnedPubKeyFromSharePrefValid() {
        service.resetMyKeyPair();
        //-----------------------------ADD TEST FOR SERVICE FROM HERE-------------------------------
        KeyPair key = service.getMyKeyPair();
        if (null == key) {
            key = service.getMyKeyPair();
        }
        String myPublicKey = Base64.getEncoder().encodeToString(key.getPublic().getEncoded());
        String expectedPublicKey = service.returnMyPublicKeyFromSharePref();
        assertEquals(myPublicKey, expectedPublicKey);
    }

    @Test
    public void CreatedPriKeyandReturnedPriKeyFromSharePrefValid() {
        //-----------------------------ADD TEST FOR SERVICE FROM HERE-------------------------------
        KeyPair key = service.getMyKeyPair();
        if (null == key) {
            key = service.getMyKeyPair();
        }
        String myPrivateKey = Base64.getEncoder().encodeToString(key.getPrivate().getEncoded());
        String expectedPrivateKey = service.returnMyPrivateKeyFromSharePref();
        assertEquals(myPrivateKey, expectedPrivateKey);
    }
}
