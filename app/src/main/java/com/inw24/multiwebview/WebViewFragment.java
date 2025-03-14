package com.inw24.multiwebview;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.inw24.multiwebview.utils.AppController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebViewFragment extends Fragment {
    String theURL;
    String theTitle;
    WebView webView;
    public Context my_context;
    public View rootView;
    public ProgressDialog pd;
    public SwipeRefreshLayout swipeContainer;
    public MediaPlayer mp;
    public NotificationManager mNotificationManager;
    private SharedPreferences preferences;
    public String loader;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final int FILECHOOSER_RESULTCODE = 2;
    private ValueCallback<Uri> mUploadMessage;

    private boolean isBackPressedOnce = false;

    public WebViewFragment() {
    }

    @Override
    public void onResume()
    {
        super.onResume();
        webView.onResume();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        webView.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        my_context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_webview, container, false);

        //Get variable from MainActivity
        theURL = this.getArguments().getString("theURL");
        theTitle = this.getArguments().getString("theTitle");

        //Start webView
        webView = (WebView) rootView.findViewById(R.id.webView);

        // --------------- SWIPE CONTAINER ---------------
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // ------------------ WEBVIEW SETTINGS  --------------------
        WebSettings webSettings = webView.getSettings();

        //HTML Cashe
        enableHTML5AppCache();

        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(my_context), "WebAppInterface");

        // -------------------- LOADER ------------------------
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getActivity().getResources().getString(R.string.txt_in_progress));
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (((AppController) getActivity().getApplication()).getContent_loader().equals("pull")) {
            swipeContainer.setRefreshing(true);
        }else if (((AppController) getActivity().getApplication()).getContent_loader().equals("dialog")) {
            pd.show();
        }else if (((AppController) getActivity().getApplication()).getContent_loader().equals("dialog")) {
            Log.d("WebView", "No Loader selected");
        }

        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                try {
                    //New DownloadListener: https://android--examples.blogspot.com/2017/08/android-webview-file-download-example.html  AND  https://devofandroid.blogspot.com/2018/02/downloading-file-from-android-webview.html
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    String fileName = URLUtil.guessFileName(url,"download",mimetype);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
                    DownloadManager dManager = (DownloadManager) getActivity().getSystemService(my_context.DOWNLOAD_SERVICE);
                    dManager.enqueue(request);
                    Toast.makeText(getActivity(), getString(R.string.txt_downloading), Toast.LENGTH_LONG).show();
                    if (pd.isShowing())
                        pd.dismiss();

                    if (swipeContainer.isRefreshing())
                        swipeContainer.setRefreshing(false);

                } catch (Exception e) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    Toast.makeText(getActivity(), getString(R.string.txt_downloading), Toast.LENGTH_LONG).show();
                    if (pd.isShowing())
                        pd.dismiss();
                    if (swipeContainer.isRefreshing())
                        swipeContainer.setRefreshing(false);
                }

            }
        });

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setAllowFileAccess(true);
        //OAuth Google & Facebook Sign-In
        String userAgent = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
        webView.getSettings().setUserAgentString(userAgent);

        webView.loadUrl(theURL);

        //Update menu item on navigation drawer when press back button
        if (getArguments().getInt("item_position", 99) != 99) {
            ((MainActivity) getActivity()).SetItemChecked(getArguments().getInt("item_position"));
        }

        webView.setWebChromeClient(new WebChromeClient() {
            /**
             * This is the method used by Android 5.0+ to upload files towards a web form in a Webview
             *
             * @param webView
             * @param filePathCallback
             * @param fileChooserParams
             * @return
             */

            //Added for fullscreen --> Start
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            public Bitmap getDefaultVideoPoster()
            {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView()
            {
                ((FrameLayout)getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                getActivity().setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (this.mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getActivity().getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(3846);
            }
            //Added for fullscreen --> End

            @Override
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {

                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray = getCameraIntent();

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Select Fuente");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                //mProgressBar.setVisibility(View.VISIBLE);
                //WebActivity.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                Log.d("LogTag", message);
                result.confirm();
                return true;
            }

            /**
             * Despite that there is not a Override annotation, this method overrides the open file
             * chooser function present in Android 3.0+
             *
             * @param uploadMsg
             * @author Tito_Leiva
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = getChooserIntent(getCameraIntent(), getGalleryIntent("image/*"), false);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(i, "Image selection"), FILECHOOSER_RESULTCODE);

            }

            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = getChooserIntent(getCameraIntent(), getGalleryIntent("*/*"), false);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(i, "Image selection"), FILECHOOSER_RESULTCODE);
            }

            /**
             * Despite that there is not a Override annotation, this method overrides the open file
             * chooser function present in Android 4.1+
             *
             * @param uploadMsg
             * @author Tito_Leiva
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = getChooserIntent(getCameraIntent(), getGalleryIntent("image/*"), false);
                startActivityForResult(Intent.createChooser(i, "Image selection"), FILECHOOSER_RESULTCODE);

            }

            private Intent[] getCameraIntent() {

                // Determine Uri of camera image to save.
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e("FragmentWeb", "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                return intentArray;

            }

            private Intent getGalleryIntent(String type) {

                // Filesystem.
                final Intent galleryIntent = new Intent();
                galleryIntent.setType(type);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                return galleryIntent;
            }

            private Intent getChooserIntent(Intent[] cameraIntents, Intent galleryIntent, Boolean lollipop) {

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select source");

                if (lollipop) {
                    // Add the camera options.
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents);
                }

                return chooserIntent;
            }

        });


        // Register OnBackPressedCallback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    if (isBackPressedOnce) {
                        requireActivity().finish(); // Exit the activity
                    } else {
                        isBackPressedOnce = true;
                        Toast.makeText(requireContext(), R.string.txt_click_back_again_to_exit, Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> isBackPressedOnce = false, 2000); // Reset after 2 seconds
                    }
                }
            }
        });

        webView.setWebViewClient(new MyWebViewClient());
        return rootView;
    }

    //===============================================//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == Activity.RESULT_OK) {

            // This is for Android 4.4.4- (JellyBean & KitKat)
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (mUploadMessage == null) return;
                Uri result = intent != null ? intent.getData() : null;
                if (result != null) {
                    Uri selectedImageUri;

                    String path = UploadTools.getPath(getActivity(), result);
                    selectedImageUri = Uri.fromFile(new File(path));
                    mUploadMessage.onReceiveValue(selectedImageUri);
                } else {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = null;

                // And this is for Android 5.0+ (Lollipop)
            } else if (requestCode == INPUT_FILE_REQUEST_CODE) {

                Uri[] results = null;

                // Check that the response is a good one
                if (resultCode == Activity.RESULT_OK) {
                    if (intent == null) {
                        // If there is no data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(results);
                    mFilePathCallback = null; // Reset the callback after use
                }
            }
        } else {
            // Reset both callbacks if the result is not OK
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }


    //===============================================//
    public static Uri savePicture(Context context, Bitmap bitmap, int maxSize) {

        int cropWidth = bitmap.getWidth();
        int cropHeight = bitmap.getHeight();

        if (cropWidth > maxSize) {
            cropHeight = cropHeight * maxSize / cropWidth;
            cropWidth = maxSize;

        }

        if (cropHeight > maxSize) {
            cropWidth = cropWidth * maxSize / cropHeight;
            cropHeight = maxSize;

        }

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, cropWidth, cropHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name)
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(
                mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg"
        );

        // Saving the bitmap
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            FileOutputStream stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
            stream.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Mediascanner need to scan for the image saved
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri fileContentUri = Uri.fromFile(mediaFile);
        mediaScannerIntent.setData(fileContentUri);
        context.sendBroadcast(mediaScannerIntent);

        return fileContentUri;
    }

    //===============================================//
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public Boolean canGoBack() {
        return webView.canGoBack();
    }

    public void GoBack() {
        webView.goBack();
    }

    private void enableHTML5AppCache() {
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    //===============================================//
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (((AppController) getActivity().getApplication()).getContent_loader().equals("pull")) {
                swipeContainer.setRefreshing(true);
            } else if (loader.equals("dialog")) {
                if (!pd.isShowing()) {
                    pd.show();
                }
            } else if (((AppController) getActivity().getApplication()).getContent_loader().equals("hide")) {
                Log.d("WebView", "No Loader selected");
            }

            // Start intent for "tel:" links
            if (url != null && url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(my_context, R.string.txt_this_function_is_not_support_here, Toast.LENGTH_SHORT).show();
                }
                view.reload();
                return true;
            }

            // Start intent for "sms:" links
            if (url != null && url.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(my_context, R.string.txt_this_function_is_not_support_here, Toast.LENGTH_SHORT).show();
                }
                view.reload();
                return true;
            }

            // Start intent for "mailto:" links
            if (url != null && url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(my_context, R.string.txt_this_function_is_not_support_here, Toast.LENGTH_SHORT).show();
                }
                view.reload();
                return true;
            }

            // Start intent for "https://www.instagram.com/YourUsername" links
            if (url != null && url.contains("instagram.com")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.instagram.android");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
                view.reload();
                return true;
            }

            // Start intent for "https://www.facebook.com/YourUsername" links
            if (url != null && url.contains("facebook.com")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                // Check if the main Facebook app is installed
                intent.setPackage("com.facebook.katana");
                if (intent.resolveActivity(my_context.getPackageManager()) == null) {
                    // Fallback to Facebook Lite if the main app isn't installed
                    intent.setPackage("com.facebook.lite");
                }

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Open in the browser if neither Facebook app is installed
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }

                view.reload();
                return true;
            }


            // Start intent for "https://www.twitter.com/YourUsername" or "https://x.com/YourUsername" links
            if (url != null && (url.contains("twitter.com") || url.contains("x.com"))) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.x.android"); // Package name for X app
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
                view.reload();
                return true;
            }


            // Start intent for "whatsapp" link
            if (url != null && url.startsWith("whatsapp://")) {
                Uri uri = Uri.parse(url);
                try {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(my_context, R.string.txt_this_function_is_not_support_here, Toast.LENGTH_SHORT).show();
                }
                view.reload();
                return true;
            }

            // Start intent for "https://www.t.me/YourUsername" links
            if (url != null && url.contains("t.me")) {
                Uri uri = Uri.parse(url);
                Intent telegramIntent = new Intent(Intent.ACTION_VIEW, uri);
                telegramIntent.setPackage("org.telegram.messenger");

                // Check if Telegram is installed
                if (telegramIntent.resolveActivity(my_context.getPackageManager()) != null) {
                    try {
                        my_context.startActivity(telegramIntent);
                    } catch (ActivityNotFoundException e) {
                        // If Telegram is not installed, open the link in a web browser
                        my_context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                } else {
                    // If Telegram is not installed, open the link in a web browser
                    my_context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }

                view.reload();
                return true;
            }

            if (url != null && url.startsWith("external:http")) {
                url = url.replace("external:", "");
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                if (pd.isShowing())
                    pd.dismiss();

                if (swipeContainer.isRefreshing())
                    swipeContainer.setRefreshing(false);
                return true;
            }

            if (url != null && url.startsWith("file:///android_asset/external:http")) {
                url = url.replace("file:///android_asset/external:", "");
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                view.loadUrl(url);
            }

            return true;
        }

        // Added for fullscreen video
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (swipeContainer.isRefreshing()) {
                swipeContainer.setRefreshing(false);
            }
            //Added for fullscreen video
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            try {
                webView.loadUrl("file:///android_asset/" + getString(R.string.error_page));
                //Toast.makeText(getActivity(),"errorCode: "+errorCode+" description: "+description+" failingUrl: "+failingUrl,Toast.LENGTH_SHORT).show();

            }catch (Exception e) {
                webView.loadUrl("file:///android_asset/" + getString(R.string.error_page));
            }
        }
    }

    //===============================================//
    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        // -------------------------------- SHOW TOAST ---------------------------------------
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        // -------------------------------- START VIBRATE MP3 ---------------------------------------
        @JavascriptInterface
        public void vibrate(int milliseconds) {
            Vibrator v = (Vibrator) my_context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(milliseconds);
        }

        // -------------------------------- START PLAY MP3 ---------------------------------------
        @JavascriptInterface
        public void playSound() {
            mp = MediaPlayer.create(my_context, R.raw.demo);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.release();
                }

            });
            mp.start();
        }

        // -------------------------------- STOP PLAY MP3 ---------------------------------------
        @JavascriptInterface
        public void stopSound() {
            if (mp.isPlaying()) {
                mp.stop();
            }
        }

        // -------------------------------- CREATE NOTIFICATION ---------------------------------------
        @JavascriptInterface
        public void newNotification(String title, String message) {

            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            mNotificationManager = (NotificationManager) my_context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Grant POST_NOTIFICATIONS permission
            if (Build.VERSION.SDK_INT >= 33) {
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS},1);
                }
                else {
                    //
                }
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(my_context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message))
                    .setContentText(message);

            //PendingIntent contentIntent = PendingIntent.getActivity(my_context, 0, new Intent(my_context, MainActivity.class), 0);
            PendingIntent contentIntent = PendingIntent.getActivity(my_context, 0, new Intent(my_context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(notificationId, mBuilder.build());
        }

        // -------------------------------- GET DATA ACCOUNT FROM DEVICE ---------------------------------------
        @JavascriptInterface
        public void snakBar(String message) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}