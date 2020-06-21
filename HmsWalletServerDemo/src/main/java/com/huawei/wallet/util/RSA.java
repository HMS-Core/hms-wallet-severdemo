/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.wallet.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA-encryption utility class.
 *
 * @since 2019-12-12
 */
public class RSA {

    /**
     * Signature algorithm.
     */
    private static final String SIGN_ALGORITHMS256 = "SHA256WithRSA";

    private static final BouncyCastleProvider BOUNCY_CASTLE_PROVIDER = new BouncyCastleProvider();

    /**
     * Sign content.
     *
     * @param content data to be signed.
     * @param privateKey merchant's private key.
     * @return the signed value.
     */
    public static String sign(String content, String privateKey, String signType) {
        String charset = "utf-8";
        try {
            PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(privatePKCS8);

            java.security.Signature signatureObj = java.security.Signature.getInstance(SIGN_ALGORITHMS256);
            signatureObj.initSign(priKey);
            signatureObj.update(content.getBytes(charset));

            byte[] signed = signatureObj.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return "";
    }

    /**
     * Encrypt bytes.
     *
     * @param bytes bytes to be encrypted.
     * @param publicKey public key.
     * @param algorithm encryption algorithm.
     * @param inputCharset charset.
     * @return the encrypted string.
     * @throws Exception
     */
    public static String encrypt(byte[] bytes, String publicKey, String algorithm, String inputCharset)
        throws Exception {
        Key key = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(algorithm, BOUNCY_CASTLE_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        if (CommonUtil.isNull(inputCharset)) {
            inputCharset = "utf-8";
        }
        byte[] b1 = cipher.doFinal(bytes);
        String encoded = com.huawei.wallet.util.Base64.encode(b1);
        return encoded;
    }

    /**
     * Convert a public key string to a PublicKey.
     *
     * @param key the public key string.
     * @return the converted PublicKey.
     * @throws Exception
     */
    private static PublicKey getPublicKey(String key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(com.huawei.wallet.util.Base64.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
}
