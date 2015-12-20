package com.zhuchao.http;

import android.content.Context;
import android.util.Log;

import com.zhuchao.Const.HttpConst;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by zhuchao on 7/14/15.
 */
public class NetworkFunction {
    public static String TAG="NetworkFunction";
    /**
     * provide function to connect server and get return value from server.
     * @param keys
     * @param parameters
     * @param url
     * @return
     * Created by LMZ 7/14/15
     */
    public static String ConnectServer(String url,String keys[],String parameters[]){
        String result="";
        //利用HttpClient实现向url发送Post请求
        //建立一个NameValuePair数组，用于存储欲传送的参数
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        for (int i = 0; i < keys.length; i++) {
            params.add(new BasicNameValuePair(keys[i],parameters[i]));//添加参数
        }
        try{
            HttpClient httpClient = new DefaultHttpClient();//创建HttpClient对象
            HttpPost httpPost = new HttpPost(url);//创建HttpPost对象
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));//使用UTF-8格式对数据进行编码
            HttpResponse httpResponse = httpClient.execute(httpPost);//执行HttpClient请求
            StringBuilder builder = new StringBuilder();
            //获取已获得读取内容的输入流对象
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            //通过循环逐步读取输入流中的内容
            for (String s = reader.readLine(); s != null; s = reader.readLine()){
                builder.append(s);//添加数据流
            }
            result = builder.toString();
            Log.d(TAG, result);
            result = result.replace("\ufeff","");//去掉UTF-8中的BOM(Byte Order Mark)的不可见字符
            if (result.startsWith("<html")) throw new Exception("Post fail for error webPage");//
            return result;
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.toString() + "ConnectServer UnsupportedEncoding error");
        } catch (ClientProtocolException e) {
            Log.d(TAG, e.toString() + "ConnectServer ClientProtocol error");
        } catch (IOException e) {
            Log.d(TAG, e.toString() + "ConnectServer IO error");
        } catch (Exception e) {
            Log.d(TAG, e.toString() + "ConnectServer error");
        }
        return null;
    }

    /**
     * download image from server.
     * @param url
     * @return
     * Created by LMZ on 7/14/15
     */
    public static InputStream DownloadImage(String url){
        try{
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url1.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            if (inputStream != null){
                return inputStream;
            }else {
                Log.d(TAG, "inputStream is null");
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, e.toString() + "DownloadImage MalformedURL error");
        } catch (IOException e) {
            Log.d(TAG, e.toString() + "DownloadImage IO  error");
        }
        return null;
    }

    /**
     * You shouldn't have to write this function before I created socket structure.
     * @param fileName
     */
    public static void UploadFile(String fileName){

    }
    /**
     *   get net file
     * @param uri
     * @return
     * @throws IOException
     */
    public static FileInfo GetFile(String uri)throws IOException {
        FileInfo fileInfo=new NetworkFunction().new FileInfo();
        URL url=new URL(uri);
        URLConnection urlConnection=url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.connect();
        fileInfo.inputStream=urlConnection.getInputStream();
        fileInfo.fileSize=urlConnection.getContentLength();
        return fileInfo;
    }

    /**
     * upload head to server
     * @param context
     * @param fileName
     * @param imageUri
     * @return
     */
    public static String UploadHeadImage(Context context,String fileName,String imageUri){
        String end="\r\n";
        String twoHyphen="--";
        String boundary="*****";
        if(Network.checkNetWorkState(context)){
            try{
                URL url=new URL(HttpConst.BASE_URL+"/api/common/uploadImg");
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Charset","UTF-8");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                DataOutputStream dataOutputStream=new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes(twoHyphen+boundary+end);
                dataOutputStream.writeBytes("Content-Disposition: form-data; " +
                        "name=\"file\"; filename=\"" +
                        fileName + "\"" + end);
                dataOutputStream.writeBytes(end);
                FileInputStream fileInputStream=new FileInputStream(imageUri);
                int bufferSize=8196;
                byte[]buffer=new byte[bufferSize];
                int length=-1;
                while((length=fileInputStream.read(buffer))!=-1){
                    //Log.d("fff",String.valueOf(length));
                    dataOutputStream.write(buffer,0,length);
                }
                dataOutputStream.writeBytes(end);
                dataOutputStream.writeBytes(twoHyphen+ boundary + twoHyphen + end);
                fileInputStream.close();
                dataOutputStream.close();
                InputStream inputStream=httpURLConnection.getInputStream();
                int ch;
                StringBuffer b=new StringBuffer();
                while((ch=inputStream.read())!=-1){
                    b.append((char)ch);
                }
                String result=b.toString();
                result= result.replace("\ufeff","");
                if(!result.equals("1")&&!result.equals("2")){
                    return result;
                }else {
                    return null;
                }
            }catch(Exception e){
                Log.d(TAG, "网络连接错误:"+e.toString());
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * fill info
     */
    public class FileInfo{
        public InputStream inputStream;
        public int fileSize;
    }

}
