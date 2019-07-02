package com.sdcode.videoplayer;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.sdcode.videoplayer.kxUtil.kxUtils;

public class KxApp extends Application {
    private static KxApp ourInstance = new KxApp();

    public static KxApp getInstance() {
        return ourInstance;
    }

    private String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmuyWFytTgNJdLhqAKHJv/6GVFNtR0aIURBrXSsjcC8fMJuy5hs4hozEBcqRyJOJzjX5VQCyP8+ir5BM7bies6JFl4HY7yUJrXvrxfjBf27wpmee1O1rGfGhQDkFbFuoBHGsA02OrJEr296PuxW85nZN5eJ4VSOkHebW+AEk7auJ5h8Cu1OW1ng39+w2cXGvRbN5AklElYIEpkWcIoU+XAKmT/S4RCZkSDVBf3CGCPdfPoODL069/ToVISoY/BscnoPnzUaivg0BdYR3M+yH0qYyf6gpc9U+5uW+fK/Hm8/TFSY2kY7C/dgDgl8eKshtKn5pUG2JrK20Kusvkr8Y2OwIDAQAB";

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;

        kxUtils.init(this);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
