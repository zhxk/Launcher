package com.changren.android.launcher.database.network;

import android.text.TextUtils;

import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.Signature;
import com.changren.android.launcher.util.UrlUtils;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class AddCommonParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //添加公共签名参数
        request = handlerRequest(request);
        //这里进行网络请求，所以这一步必须要有
        Response response = chain.proceed(request);
        //可以对response进行解析，并重定向比如再次请求

        return response;
    }

    private Request handlerRequest(Request request) {
        //解析参数
        Map<String, String> params = parseParams(request);
        if (params == null) {
            params = new HashMap<>();
        }

        //这里为公共的参数
        //添加签名参数
        params.put("source", "robot");
        params.put("timestamp", System.currentTimeMillis()/1000 + "");

        String method = request.method();
        String[] str = request.url().toString().split("\\?");
        String url_suffix = str[0].substring(HttpConfig.BASE_URL.length());
//        String prefix = "GET&index/index/test?";
        String prefix = method + "&"+ url_suffix + "?";
        String sign = Signature.createSignature(Signature.HMAC_KEY, prefix, params);
        params.put("sign", sign);

        if (HttpConfig.REQUEST_METHOD_GET.equals(method)) {
            String sb = str[0] + "?" + UrlUtils.map2QueryStr(params);
            //重构Request
            return request.newBuilder().url(sb).build();
        } else if (HttpConfig.REQUEST_METHOD_POST.equals(method)
                || HttpConfig.REQUEST_METHOD_DELETE.equals(method)
                || HttpConfig.REQUEST_METHOD_PUT.equals(method)) {
            if (request.body() == null || !(request.body() instanceof MultipartBody)) {
                FormBody.Builder bodyBuilder = new FormBody.Builder();
//                Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
//                while (entryIterator.hasNext()) {
//                    Map.Entry<String, String> entry = entryIterator.next();
//                    String key = entry.getKey();
//                    String value = entry.getValue();
//                    bodyBuilder.add(key, value);
//                }
                for (Map.Entry<String, String> entry: params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    bodyBuilder.add(key, value);
                }
                return request.newBuilder().method(method, bodyBuilder.build()).build();
            }
        }
        return request;
    }

    /**
     * 解析请求参数
     * @param request 网络请求对象
     * @return 请求参数map集合
     */
    private Map<String, String> parseParams(Request request) {
        //获取网络请类型：GET POST DELETE PUT PATCH
        String method = request.method();
        Map<String, String> params = null;
        if (HttpConfig.REQUEST_METHOD_GET.equals(method)) {
            params = doGet(request);
        } else if (HttpConfig.REQUEST_METHOD_POST.equals(method)
                || HttpConfig.REQUEST_METHOD_DELETE.equals(method)
                || HttpConfig.REQUEST_METHOD_PUT.equals(method)) {
            params = doForm(request);
        }
        return params;
    }

    /**
     * get请求参数解析
     * @param request 请求对象
     * @return 参数集合
     */
    private Map<String, String> doGet(Request request) {
        HttpUrl url = request.url();
        //查询所有请求参数的name
        Set<String> strings = url.queryParameterNames();
        if (strings.isEmpty()) {
            return null;
        }

        Iterator<String> iterator = strings.iterator();
        Map<String, String> params = new HashMap<>();
        int i = 0;
        //name和value一一对应
        while (iterator.hasNext()) {
            String name = iterator.next();
            String value = url.queryParameterValue(i);
            LogUtils.i("name=" + name, "value=" + value);
            params.put(name, value);
            i++;
        }

        return params;
    }

    /**
     * post等请求的body表单内请求参数解析
     * TODO 可以上传文件的表单请求暂时没有考虑进来
     * @param request 请求对象
     * @return 参数集合
     */
    private Map<String,String> doForm(Request request) {
        RequestBody body = request.body();
        if (body == null) return null;

        if (!(body instanceof FormBody)) {
            return getRequestBodyParameter(body);
        }
//        if (body == null || !(body instanceof FormBody)) {
//            doTest(body);
//            return null;
//        }

//        if (body instanceof MultipartBody) {
//            return doForm((MultipartBody) body);
//        }

        Map<String, String> params = null;
        FormBody formBody = (FormBody) body;
        int size = formBody.size();
        if (size > 0) {
            params = new HashMap<>();
            for (int i = 0; i < size; i++) {
                params.put(formBody.name(i), formBody.value(i));
            }
        }
        return params;
    }

//    private Map<String,String> doForm(@NonNull MultipartBody body) {
//        Map<String, String> params = null;
//        List<MultipartBody.Part> parts = body.parts();
//        for (MultipartBody.Part part: parts) {
//            RequestBody requestBody = part.body();
//            LogUtils.i(requestBody);
//        }
//        return params;
//    }

    /**
     * TODO MultipartBody获得的是String内容，需要进一步获取Part之后获取RequestBody，组成参数列表，这里暂时未处理
     * @param body @Body注解的RequestBody,单个参数的内容为String,实体类或者map集合的参数内容为json
     * @return  参数集合
     */
    private Map<String,String> getRequestBodyParameter(RequestBody body) {
        Buffer buffer = new Buffer();// 创建缓存
        try {
            body.writeTo(buffer);//将请求体内容,写入缓存
            String parameterStr = buffer.readUtf8();// 读取参数字符串
            //如果是json串就解析 从新加参数 如果是字符串就进行修改 具体业务逻辑自己加
            LogUtils.eTag("OkHttp网络请求未加表单注解", parameterStr);

            if (TextUtils.isEmpty(parameterStr)) {
                return null;
            }

            try {
                return UrlUtils.JsonStr2Map(parameterStr);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }

//            //对应请求头大伙按照自己的传输方式 定义
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), parameterStr);
//            return requestBody;
//            request = request.newBuilder().patch(requestBody).build();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
