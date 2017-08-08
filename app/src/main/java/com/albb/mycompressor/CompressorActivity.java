package com.albb.mycompressor;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.albb.mycompressor.utils.ClearEditText;
import com.albb.mycompressor.utils.FileUtil;
import com.albb.mycompressor.utils.ImageUtilBitmap;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CompressorActivity extends BaseCompressActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTOS_REQUEST = 2;

    private ImageView actualImageView;
    private ImageView compressedImageView;
    private TextView actualSizeTextView;
    private TextView compressedSizeTextView;
    private File actualImage;
    private File compressedImage;
    private Bitmap waterBitmap;
    private ClearEditText waterName;
    private String waterNameStr;
    /**
     * 图片目录
     */
    private static final String IMAGE_PATH = Environment.getExternalStorageDirectory().toString()
            + "/MyCompressor/pic/image/";
    /**
     * 默认图片名称
     */
    private static final String IMAGE_NAME = new SimpleDateFormat("yyyyMMddHHmmssms", Locale.CHINA)
            .format(new Date());
    /**
     * 图片后缀名
     */
    private static final String IMAGE_TYPE = ".jpg";
    /*默认水印名称*/
    private static final String WATER_NAME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(new Date());

    private boolean sdCardState = false;
    private String strImgPath = "";
    private String cameraImgPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compressor);
        actualImageView = (ImageView) findViewById(R.id.actual_image);
        compressedImageView = (ImageView) findViewById(R.id.compressed_image);
        actualSizeTextView = (TextView) findViewById(R.id.actual_size);
        compressedSizeTextView = (TextView) findViewById(R.id.compressed_size);
        waterName = (ClearEditText)findViewById(R.id.compressor_edittext);
        actualImageView.setBackgroundColor(getRandomColor());
        clearImage();
        compressedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaxImage(CompressorActivity.this,waterBitmap);
            }
        });

    }
    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    public void takePhotos(View view) {
        strImgPath = IMAGE_PATH;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)
                ||state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            sdCardState = true;
        }else {
            sdCardState = false;
            File filesDir = getFilesDir();
            strImgPath = filesDir.getPath()
                    + "/MyCompressor/pic/image/";
        }

        File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        }
        cameraImgPath = strImgPath + IMAGE_NAME + IMAGE_TYPE;

        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(cameraImgPath);
        Uri imageFileUri = Uri.fromFile(imageFile);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(imageCaptureIntent, TAKE_PHOTOS_REQUEST);
    }

    public void compressImage(View view) {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {

            // Compress image in main thread
            //compressedImage = new Compressor(this).compressToFile(actualImage);
            //setCompressedImage();

            // Compress image to bitmap in main thread
            //compressedImageView.setImageBitmap(new Compressor(this).compressToBitmap(actualImage));

            // Compress image using RxJava in background thread
            new Compressor(this)
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            compressedImage = file;
                            setCompressedImage();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            showError(throwable.getMessage());
                        }
                    });
        }
    }

    public void customCompressImage(View view) {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {
            //Compress image in main thread using custom Compressor
            try {
                compressedImage = new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(actualImage);
                setCompressedImage();
            } catch (IOException e) {
                e.printStackTrace();
                showError(e.getMessage());
            }

            // Compress image using RxJava in background thread with custom Compressor
//            new Compressor(this)
//                    .setMaxWidth(640)
//                    .setMaxHeight(480)
//                    .setQuality(75)
//                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
//                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
//                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
//                    .compressToFileAsFlowable(actualImage)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<File>() {
//                        @Override
//                        public void accept(File file) {
//                            compressedImage = file;
//                            setCompressedImage();
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) {
//                            throwable.printStackTrace();
//                            showError(throwable.getMessage());
//                        }
//                    });
        }
    }
    public void waterCompressImage(View view){
        if (null==actualImage){
            showError("actualImage is null!");
            return;
        }
        setWaterBitmapByFile(actualImage);
    }
    /*显示大图*/
    private void showMaxImage(Context context,Bitmap mBitmap){
        int bitmapSize = getBitmapSize(mBitmap);
        Log.d(TAG,"---bitmapSize--"+(double)bitmapSize/1024/1024+"M"+"---"+bitmapSize);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_max_image,null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ((ImageView)view.findViewById(R.id.show_image_imageview)).setImageBitmap(mBitmap);
        view.findViewById(R.id.show_image_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void setCompressedImage() {
        waterBitmap = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
        compressedImageView.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        compressedSizeTextView.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));

        Toast.makeText(this, "Compressed image save in " + compressedImage.getPath(), Toast.LENGTH_LONG).show();
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
    }

    private void clearImage() {
        actualImageView.setBackgroundColor(getRandomColor());
        compressedImageView.setImageDrawable(null);
        compressedImageView.setBackgroundColor(getRandomColor());
        compressedSizeTextView.setText("Size : -");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(this,data.getData());
                actualImageView.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
                clearImage();
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }else if (requestCode == TAKE_PHOTOS_REQUEST && resultCode == RESULT_OK){
            showError("拍照成功");
            if (!sdCardState){
                if (data!=null){
                }
            }
            actualImage = new File(cameraImgPath);
            actualImageView.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
            actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
            clearImage();
        }
    }
    /*打水印*/
    private void setWaterBitmapByFile(File file){
        waterNameStr = waterName.getText().toString();
        if ("".equals(waterNameStr) || null == waterNameStr){
            waterNameStr = WATER_NAME;
        }
        try {
            Bitmap compressToBitmap = new Compressor(CompressorActivity.this)
                    .setMaxHeight(960)
                    .setMaxWidth(720)
                    .setQuality(80)
                    .setDestinationDirectoryPath(IMAGE_PATH)
                    .compressToBitmap(file);
            Bitmap waterBitmap = ImageUtilBitmap.drawTextToLeftTop(CompressorActivity.this, compressToBitmap,
                    waterNameStr,16,Color.RED,10,10);

            File newFile = FileUtil.saveBitmapToFile(waterBitmap,IMAGE_PATH,"bitmap_"+IMAGE_NAME);
            compressedImage = new Compressor(CompressorActivity.this)
                    .setMaxHeight(780)
                    .setMaxWidth(640)
                    .setQuality(75)
                    .setDestinationDirectoryPath(IMAGE_PATH)
                    .compressToFile(newFile,"last_"+IMAGE_NAME+IMAGE_TYPE);
            setCompressedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private int getRandomColor() {
        Random rand = new Random();
        return Color.argb(100, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    /*获取路径图片大小*/
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /*获取bitmap所占用内存大小*/
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=waterBitmap && !waterBitmap.isRecycled()){
            waterBitmap.recycle();
            waterBitmap = null;
        }
    }
}
