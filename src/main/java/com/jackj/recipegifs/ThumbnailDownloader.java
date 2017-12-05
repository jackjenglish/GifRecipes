package com.jackj.recipegifs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by jackj on 10/12/2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG="ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD=0;

    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap= new ConcurrentHashMap<>();//A thread safe version of hashmap
    private Handler mResponseHandler;//holds a handler passed from the main thread
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T>{//Used to communicate the responses(downloaded images) with the main thread
        void onThumbnailDownloaded(T target, Bitmap thumbnail);//Eventually called when an image has been fully downloaded
        //and is ready to be added to the UI
    }
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener){
        mThumbnailDownloadListener=listener;
    }

    public ThumbnailDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler= responseHandler;  //Sets mResponseHandler to a handler passed from the main thread.
        //Used to schedule work on main thread see handleRequest()
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    //Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }
    public void queueThumbnail(T target,String url){
        //Log.i(TAG,"Got a URL: "+url);

        if(url==null){
            mRequestMap.remove(target);
        } else{
            //A link between the photoholder passed through and its url is mapped here
            mRequestMap.put(target,url);

            //Obtains a message directly from mRequestHandler,
            //which automatically sets the new message object's target field to mRequestHandler(the other fields are specified)
            //This message represents a download request for the specified
            //T target value(a photoholder from RecyclerView). queueThumbnail() is being called from
            //OnBindViewHolder(), passing along the photoholder the image is being downloaded for and the
            //URL location of the image to download.
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD,target)
                    //Send message to Handler set in the message object's
                    // TARGET field(target is on of the three relevant fields
                    // in an instance of messsage,others being WHAT(user defined int that describes the message(MESSAGE_DOWNLOAD here))
                    // and OBJ(user-specified object to be sent with the message(Photoholder in this case))
                    .sendToTarget();//When this message is pulled off the queue by the looper,
            // it is processed in the  message's target Handler's(mRequestHandler) implementation of Handler.handleMessage()
            // This is implemented above here in onLooperPrepared().

        }

    }
    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
    private void handleRequest(final T target) {
        //This is where the downloading happens.
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                Log.e(TAG,"nullll");
                return;
            }
            //Create a bitmap image using the bytes from geturlBytes()
            byte[] bitmapBytes = getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            //Log.i(TAG, "Bitmap created");
            //The request is not fully handled until you set this bitmap on the Photoholder
            //that came from PhotoAdapter.<--This is UI work and so must be done on the main thread
            //Next ThumbnailDownloader will use a Handler to do this, by posting a request to the main thread


            //Handler.post(Runnable) Causes the Runnable r to be added to the message queue
            //The runnable will be run on the thread to which this handler is attached.
            mResponseHandler.post(new Runnable() {//Because mResponseHandler is associated with the main thread’s Looper,
                // all of the code inside of run() will be executed on the main thread.
                @Override
                public void run() {
                    if(mRequestMap.get(target) !=url){//RecyclerView may have recycled the
                        return;                         // PhotoHolder and requested a different URL
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target,bitmap);
                }
            });
        } catch(IOException ioe) {
            Log.e(TAG,"Error downloading image",ioe);
        }
    }
    public byte[] getUrlBytes(String urlSpec) throws IOException {










        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
}