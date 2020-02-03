package com.example.keyservicetestapp;

import android.content.Intent;
import android.os.IBinder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    public void CreatedPubKeyandReturnedPubKeyFromSharePrefValid() throws NoSuchAlgorithmException {
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
    public void CreatedPriKeyandReturnedPriKeyFromSharePrefValid() throws NoSuchAlgorithmException {
        //-----------------------------ADD TEST FOR SERVICE FROM HERE-------------------------------
        KeyPair key = service.getMyKeyPair();
        if (null == key) {
            key = service.getMyKeyPair();
        }
        String myPrivateKey = Base64.getEncoder().encodeToString(key.getPrivate().getEncoded());
        String expectedPrivateKey = service.returnMyPrivateKeyFromSharePref();
        assertEquals(myPrivateKey, expectedPrivateKey);
    }

    @Test
    public void EnCryptAndDecryptTextValid() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (null == service.getMyKeyPair()){
            service.getMyKeyPair();
        }
        PublicKey myPublicKey = service.getMyKeyPair().getPublic();
        PrivateKey myPrivateKey = service.getMyKeyPair().getPrivate();

        String sampleText = "This is an awesome chat app!";

        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, myPublicKey);
        byte[] encryptedText= c.doFinal(sampleText.getBytes());

        c.init(Cipher.DECRYPT_MODE, myPrivateKey);
        String actualText = new String(c.doFinal(encryptedText));

        assertEquals(sampleText, actualText);

    }

    @Test
    public void StoreAndRetrievePartnerKeyValid_1() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String personName = "Lee Mai";
        service.resetKey(personName);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair key = keyPairGenerator.generateKeyPair();
        PublicKey partnerOneKey = key.getPublic();

        service.storePublicKey(personName, Base64.getEncoder().encodeToString(partnerOneKey.getEncoded()));

        assertEquals(partnerOneKey, service.getPublicKey(personName));
    }

    @Test
    public void StoreAndRetrievePartnerKeyValid_2() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String personName = "Elliott Fix";
        service.resetKey(personName);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        String partnerTwoKey = Base64.getEncoder().encodeToString(keyPairGenerator.generateKeyPair().getPublic().getEncoded());
        service.storePublicKey(personName, partnerTwoKey);

        assertEquals(partnerTwoKey, Base64.getEncoder().encodeToString(service.getPublicKey(personName).getEncoded()));

    }

}
