package com.albb.mycompressor;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.albb.mycompressor.utils.CheckManifestUtils;

/**
 * Created by wyb on 2017/8/1.
 */

public class BaseCompressActivity extends Activity {
    protected static final String TAG = "CompressActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*安卓6.0及以上动态申请权限时,清单文件也必须配置(兼容低版本)*/
//        CheckManifestUtils.checkAllManifest(BaseCompressActivity.this);
        CheckManifestUtils.checkCameraManifest(BaseCompressActivity.this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CheckManifestUtils.REQUEST_CODE_CONTACT){
            for (String ss:permissions){
                if (ss.equals(Manifest.permission.CAMERA)){
                    Log.d(TAG,"已经申请相机权限");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
