package com.zero.customview.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.zero.customview.R;
import com.zero.customview.view.HorizontalProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pb_downloading)
    HorizontalProgressBar pbDownloading;

    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    @BindView(R.id.bt_download_1)
    Button btDownload1;
    @BindView(R.id.bt_download_2)
    Button btDownload2;
    private Context mContext;
    private DownloadManager downloadManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        mContext = this;
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {
        setSupportActionBar(toolbar);


    }

    private long startDownloading(String url, String file) {
        if (downloadManager == null) {
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        }

        DownloadManager.Request req = null;
        Uri uri = Uri.parse(encodeUTF8(url));
        Log.d(TAG, "PATH -> " + uri.getPath());
        req = new DownloadManager.Request(uri);

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //req.setAllowedOverRoaming(false);

        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        req.setDestinationInExternalPublicDir("abc", file);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        req.setMimeType(mimeString);

        req.setTitle(getString(R.string.download_title));
        req.setDescription(getString(R.string.download_title));

        Log.d(TAG, "Start downloading");

        return downloadManager.enqueue(req);
    }

    public String encodeUTF8(String string) {
        String result = string;
        int lastIndex = string.lastIndexOf("/");
        if (lastIndex == -1) {
            return result;
        }
        String str = string.substring(lastIndex+1);
        Log.d(TAG, "STR ->  " + str);
        try {
            result = URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return string.substring(0, lastIndex+1) + result;
    }

    public String encodeGB(String string) {
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");
        return split[0];
    }

    public String toUtf8(String str) {
         String result = null;
         try {
                 result = new String(str.getBytes("GBK"), "UTF-8");
         } catch (UnsupportedEncodingException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
         }
         return result;
     }

    @OnClick({R.id.bt_download_1, R.id.bt_download_2})
    public void onClick(View view) {
        String url = "";
        String file = "";
        switch (view.getId()) {
            case R.id.bt_download_1:
                url = "http://192.168.0.108:8080/field_operation_debug/majiaping.db";
                file = "马家坪.db";
                break;
            case R.id.bt_download_2:
                url = "http://192.168.0.108:8080/field_operation_debug/高台南.db";
                file = "高台南.db";
                break;
        }
        startDownloading(url, file);
    }
}
