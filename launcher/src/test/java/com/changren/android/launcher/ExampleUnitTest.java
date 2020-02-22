package com.changren.android.launcher;

import com.changren.android.launcher.database.entity.Token;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

//        String data = "{}";
//        Gson gson = new Gson();
//        Token token = gson.fromJson(data, new TypeToken<Token>(){}.getType());
//        System.out.print(token.getToken());
    }

//    private static final String LOG_TAG = "HMACTest";
//    private static final String REGISTER_HMAC_KEY = "#@%^&";
//
//    private static String stringToSign(String data) {
//        try {
//            Mac mac = Mac.getInstance("HmacSHA1");
//            SecretKeySpec secret = new SecretKeySpec(
//                REGISTER_HMAC_KEY.getBytes("UTF-8"), mac.getAlgorithm());
//            mac.init(secret);
//            return Base64.encodeToString(mac.doFinal(data.getBytes()), Base64.NO_WRAP);
//        } catch (NoSuchAlgorithmException e) {
//            Log.e(LOG_TAG, "Hash algorithm SHA-1 is not supported", e);
//        } catch (UnsupportedEncodingException e) {
//            Log.e(LOG_TAG, "Encoding UTF-8 is not supported", e);
//        } catch (InvalidKeyException e) {
//            Log.e(LOG_TAG, "Invalid key", e);
//        }
//        return "";
//    }

//    private void test() {
//        Map<String, Object> params = new HashMap<>();
//        params.put("source", "app");
//        params.put("timestamp", System.currentTimeMillis()/1000 + "");
//        params.put("test1", "1");
//        params.put("test2", "1");
//
////        String str_sign = "GET&index/index/test?source=app&test1=1&test2=1&timestamp="
////            + System.currentTimeMillis()/1000;
//        String prefix = "GET&index/index/test?";
//        Log.e(TAG,"加密后：" + Signature.createSignature(Signature.HMAC_KEY, prefix, params));
//    }
}