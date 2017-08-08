package com.albb.mycompressor.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by wyb on 2017/8/1.
 */

public class CheckManifestUtils {
    public static final int REQUEST_CODE_CONTACT = 99;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static String[] PERMISSIONS_CAMERA= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public static String[] PERMISSIONS_LOACATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION};
    //九组危险权限共二十四个需要动态申请
    public static String[] PERMISSIONS_USERALL = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.BROADCAST_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.RECORD_AUDIO
    };


    public CheckManifestUtils(){

    }
    //相机权限
    public static void checkCameraManifest(Activity activity){
        if (Build.VERSION.SDK_INT >= 23) {
            //验证是否许可权限
            for (String permissionstr : PERMISSIONS_CAMERA) {
                if (PackageManager.PERMISSION_GRANTED !=
                        ContextCompat.checkSelfPermission(activity,permissionstr)){
                    ActivityCompat.requestPermissions(activity,PERMISSIONS_CAMERA,REQUEST_CODE_CONTACT);
                }
            }
        }
    }
    //所有危险权限
    public static void checkAllManifest(Activity activity){
        if (Build.VERSION.SDK_INT >= 23) {
            //验证是否许可权限
            for (String permissionstr : PERMISSIONS_USERALL) {
                if (PackageManager.PERMISSION_GRANTED !=
                        ContextCompat.checkSelfPermission(activity,permissionstr)){
                    ActivityCompat.requestPermissions(activity,PERMISSIONS_USERALL, REQUEST_CODE_CONTACT);
                }
            }
        }
    }

    //所有危险权限
    public static String checkCustomManifest(Activity activity,String[]strings){
        if (null==strings && strings.length<=0){
            return "权限不能为空！";
        }
        if (Build.VERSION.SDK_INT >= 23) {
            //验证是否许可权限
            for (String permissionstr : strings) {
                if (PackageManager.PERMISSION_GRANTED !=
                        ContextCompat.checkSelfPermission(activity,permissionstr)){
                    ActivityCompat.requestPermissions(activity,strings, REQUEST_CODE_CONTACT);
                }
            }
        }
        return "sucess";
    }

}
