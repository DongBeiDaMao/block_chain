package com.damao.Utils;


import java.security.*;
import java.util.Base64;

public class Encryption {
    /**
     * 传入字符串，返回 SHA-256 加密字符串
     * @param strText
     * @return SHA256加密
     */
    public static String getSHA256(final String strText) {
        return SHA(strText, "SHA-256");
    }
    static {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得密钥的String
     * @param key 密钥
     * @return 密钥的String
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }


    /**
     * 字符串 SHA 加密
     * @param strText 要加密的文本
     * @param encryptType 加密的算法类型
     * @return 加密结果
     */
    private static String SHA(final String strText, final String encryptType) {
        String strResult = null; /* 返回值*/
        if (strText != null && strText.length() > 0) { /* 是否是有效字符串*/
            try {/*SHA 加密开始 创建加密对象，传入加密类型*/
                MessageDigest messageDigest = MessageDigest.getInstance(encryptType);
                messageDigest.update(strText.getBytes()); /* 传入要加密的字符串*/
                byte byteBuffer[] = messageDigest.digest(); /* 得到 byte 数组*/
                StringBuffer strHexString = new StringBuffer(); /* 將 byte 数组转换 string 类型*/
                for (int i = 0; i < byteBuffer.length; i++) { /* 遍历 byte 数组*/
                    String hex = Integer.toHexString(0xff & byteBuffer[i]); /* 转换成16进制并存储在字符串中*/
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex); }
                strResult = strHexString.toString(); /* 得到返回結果*/
            } catch (NoSuchAlgorithmException e) {
                System.out.println("***************************无此加密算法*****************************");
            }
        }
        return strResult;
    }
}