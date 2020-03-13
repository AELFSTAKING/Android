package io.alf.exchange.util;

import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

import io.alf.exchange.App;
import io.alf.exchange.Constant;


public class FileProviderUtils {

    public static Uri getUriForFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(App.getContext(), Constant.FILEPROVIDER_AUTHORITIES_VALUE, file);
        } else {
            return Uri.fromFile(file);
        }
    }

}
