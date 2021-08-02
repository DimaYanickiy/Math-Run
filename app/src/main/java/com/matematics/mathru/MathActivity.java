package com.matematics.mathru;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;

public class MathActivity extends AppCompatActivity {

    WebView math;
    ProgressBar progress;
    Saver sv;
    ValueCallback<Uri[]> call;
    String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);
        sv = new Saver(this);
        setViewSettings();
        setViewClientSettings();
        setChromeClientSettings();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.setAcceptThirdPartyCookies(math, true);
        cookieManager.flush();
        String gameUrl = sv.loadPoint();
        if (!gameUrl.isEmpty()) {
            math.loadUrl(gameUrl);
        } else {
            Intent intent = new Intent(MathActivity.this,
                    MainActivity.class);
            startActivity(intent);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setViewSettings() {
        math.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        math.requestFocus(View.FOCUS_DOWN);
        math.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        math.getSettings().setUserAgentString(math.getSettings().getUserAgentString());
        math.getSettings().setJavaScriptEnabled(true);
        math.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        math.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        math.getSettings().setAppCacheEnabled(true);
        math.getSettings().setDomStorageEnabled(true);
        math.getSettings().setDatabaseEnabled(true);
        math.getSettings().setSupportZoom(false);
        math.getSettings().setAllowFileAccess(true);
        math.getSettings().setAllowFileAccess(true);
        math.getSettings().setAllowContentAccess(true);
        math.getSettings().setLoadWithOverviewMode(true);
        math.getSettings().setUseWideViewPort(true);
        math.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        math.getSettings().setPluginState(WebSettings.PluginState.ON);
        math.getSettings().setSavePassword(true);
    }

    public void setChromeClientSettings() {
        math.setWebChromeClient(new WebChromeClient() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void checkPermission() {
                ActivityCompat.requestPermissions(
                        MathActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        1);
            }

            @SuppressLint("QueryPermissionsNeeded")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                int permissionStatus = ContextCompat.checkSelfPermission(MathActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    if (call != null) {
                        call.onReceiveValue(null);
                    }
                    call = filePathCallback;
                    Intent takePictureIntent;
                    takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", photo);
                        } catch (IOException ignored) {
                        }
                        if (photoFile != null) {
                            photo = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("image/*");
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }
                    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooser.putExtra(Intent.EXTRA_TITLE, "Photo");
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooser, 1);
                    return true;
                } else
                    checkPermission();
                return false;
            }

            private File createImageFile() throws IOException {
                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");
                if (!imageStorageDir.exists())
                    imageStorageDir.mkdirs();
                imageStorageDir = new File(imageStorageDir + File.separator + "Photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                setProgress(view, newProgress);
            }
        });
    }

    public void setViewClientSettings() {
        math.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return overrideUrl(view, url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return overrideUrl(view, request.getUrl().toString());
            }

            public boolean overrideUrl(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(i);
                    return true;
                } else if (url.startsWith("tg:") || url.startsWith("https://t.me") || url.startsWith("https://telegram.me")) {
                    try {
                        WebView.HitTestResult result = view.getHitTestResult();
                        String data = result.getExtra();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                        view.getContext().startActivity(intent);
                    } catch (Exception ignored) {
                    }
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sv.loadFirst()) {
                    sv.savePoint(url);
                    sv.saveFirst(false);
                    CookieManager.getInstance().flush();
                }
                CookieManager.getInstance().flush();
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1 || call == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                if (photo != null) {
                    results = new Uri[]{Uri.parse(photo)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        call.onReceiveValue(results);
        call = null;
    }

    public void setProgress(WebView view, int newProgress) {
        progress.setActivated(true);
        progress.setVisibility(View.VISIBLE);
        progress.setProgress(newProgress);
        if (newProgress == 100) {
            progress.setVisibility(View.GONE);
            progress.setActivated(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieManager.getInstance().flush();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CookieManager.getInstance().flush();
    }

    @Override
    public void onBackPressed() {
        if(math.canGoBack()){
            math.goBack();
        } else{
            CookieManager.getInstance().flush();
            new AlertDialog.Builder(this).setTitle("Are you sure?")
                    .setMessage("Do you really want to exit?")
                    .setPositiveButton("Yes", (arg0, arg1) -> System.exit(0))
                    .setNegativeButton("No", null).create().show();
        }
    }
}