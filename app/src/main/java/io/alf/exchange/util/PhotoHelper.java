package io.alf.exchange.util;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.alf.exchange.App;
import io.alf.exchange.Constant;
import io.alf.exchange.util.ucrop.Crop;
import io.tick.base.util.SpUtil;


public class PhotoHelper {
    private static final String KEY_TAKE_PHOTOS_RESULT = "take_photos_result";
    private static ExecutorService mCompressExecutor = Executors.newFixedThreadPool(3);
    private static Map<String, String> mCompressedMap = Collections.synchronizedMap(new HashMap<String, String>());

    public static Crop.Options getDefaultUCropOptions() {
        Crop.Options options = new Crop.Options();
        //设置裁剪图片的保存格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);//  JPEG,PNG,WEBP;
        //设置裁剪图片的图片质量
        options.setCompressionQuality(100);//[0-100]
        //设置你想要指定的可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setFreeStyleCropEnabled(true);
        // 设置图片压缩质量
        options.setCompressionQuality(100);
//        options.setMaxScaleMultiplier(5);
//        options.setImageToCropBoundsAnimDuration(666);
//        options.setCircleDimmedLayer(true);
//        options.setDimmedLayerColor(Color.DKGRAY);
//        options.setShowCropFrame(false);
//        options.setCropGridStrokeWidth(1);
//        options.setCropGridColor(Color.WHITE);
//        options.setCropGridColumnCount(3);
//        options.setCropGridRowCount(3);
//        options.setHideBottomControls(true);
        return options;
    }

    /**
     * @param ucrop 如果为null返回原图
     * @param l     图片地址回传
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data, IUCrop ucrop, boolean needCompress, OnPhotoGetListener l) {
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Constant.RC_ACTION_PICK://data.getData().toString()
                   /* if ("content".equals(data.getScheme())) {
                    } else if ("file".equals(data.getScheme())) {
                    }*/
                        if (null != ucrop) {
                            try {
                                ucrop.onStartUCrop(data.getData(), getOutputPhoto(true));
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (null != l) {// 异常返回原图
                                    String path = queryPathByUri(data.getData());
                                    if (needCompress) {
                                        mCompressExecutor.execute(new CompressThered(path, l));
                                    } else {
                                        l.onGetPhotoPath(path, path);
                                    }
                                }
                            }
                        } else {
                            if (null != l) {
                                String path = queryPathByUri(data.getData());
                                if (needCompress) {
                                    mCompressExecutor.execute(new CompressThered(path, l));
                                } else {
                                    l.onGetPhotoPath(path, path);
                                }
                            }
                        }
                        break;
                    case Constant.RC_ACTION_IMAGE_CAPTURE://SharedPreferencesHelper.getObj("take_photos_result", String.class)
                        if (null != ucrop) {
                            try {
                                ucrop.onStartUCrop(getUri(new File(getPhotosPath())), getOutputPhoto(true));
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (null != l) {// 异常返回原图
                                    String path = getPhotosPath();
                                    if (needCompress) {
                                        mCompressExecutor.execute(new CompressThered(path, l));
                                    } else {
                                        l.onGetPhotoPath(path, path);
                                    }
                                }
                            }
                        } else {
                            if (null != l) {
                                String path = getPhotosPath();
                                if (needCompress) {
                                    mCompressExecutor.execute(new CompressThered(path, l));
                                } else {
                                    l.onGetPhotoPath(path, path);
                                }
                            }
                        }
                        break;
                    case Crop.REQUEST_CROP://成功crop
                        if (null != l) {
                            String path = queryPathByUri(Crop.getOutput(data));
                            if (needCompress) {
                                mCompressExecutor.execute(new CompressThered(path, l));
                            } else {
                                l.onGetPhotoPath(path, path);
                            }
                        }
                        break;
                }
            } else if (resultCode == Crop.RESULT_ERROR) {
                final Throwable cropError = Crop.getError(data);
                if (null != cropError) cropError.printStackTrace();
                if (null != l) {
                    String path = queryPathByUri(data.getData());
                    if (needCompress) {
                        mCompressExecutor.execute(new CompressThered(path, l));
                    } else {
                        l.onGetPhotoPath(path, path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getUri(File file) {
        return FileProviderUtils.getUriForFile(file);
    }

    /**
     * 打开相机
     */
    public static void onTakePhotos(Activity activity) {
        Intent intent = new Intent(ACTION_IMAGE_CAPTURE);
        File file = getOutputPhoto(false);
        if (null == file) {
            LogUtils.e("File can not be created!");
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(file));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, Constant.RC_ACTION_IMAGE_CAPTURE);
    }

    public static void onTakePhotos(Fragment fragment) {
        Intent intent = new Intent(ACTION_IMAGE_CAPTURE);
        File file = getOutputPhoto(false);
        if (null == file) {
            LogUtils.e("File can not be created!");
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(file));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        fragment.startActivityForResult(intent, Constant.RC_ACTION_IMAGE_CAPTURE);
    }

    /**
     * 打开相册
     */
    public static void onOpenAlbum(Activity activity) {
     /*   Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), RC_ACTION_PICK);*/

       /* Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");
        activity.startActivityForResult(intent, RC_ACTION_PICK);*/

        Intent intent = new Intent(Intent.ACTION_PICK);// 激活系统图库，选择一张图片
        intent.setType("image/*");
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
        activity.startActivityForResult(intent, Constant.RC_ACTION_PICK);// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
    }

    public static void onOpenAlbum(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);// 激活系统图库，选择一张图片
        intent.setType("image/*");
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
        fragment.startActivityForResult(intent, Constant.RC_ACTION_PICK);// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
    }

    public static File getOutputPhoto(boolean isCrop) {
        File photosDir = new File(PathUtils.getAppCacheDir(), PathUtils.IMAGE_DIR);
        if (!photosDir.exists()) {
            photosDir.mkdirs();
        }
        long timestamp = System.currentTimeMillis();
        File photo;
        String photoPath;
        if (isCrop) {
            photoPath = photosDir.getPath() + File.separator + "IMG_" + timestamp + "_CROP" + ".jpg";
            photo = new File(photoPath);
        } else {
            photoPath = photosDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg";
            photo = new File(photoPath);
        }
        recordPhotosPath(photo);
        return photo;
    }

    private static void recordPhotosPath(File photo) {
        SpUtil.putObj(KEY_TAKE_PHOTOS_RESULT, photo.toString());
    }

    public static String getPhotosPath() {
        return SpUtil.getObj(KEY_TAKE_PHOTOS_RESULT, String.class);
    }

    public static String queryPathByUri(Uri uri) {
        String path = null;
        if ("content".equals(uri.getScheme())) {
            ContentResolver mCr = App.getContext().getContentResolver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                    DocumentsContract.isDocumentUri(App.getContext(), uri)) {
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "= ?";
                Cursor mCursor = mCr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id},
                        null);
                if (null != mCursor) {
                    if (mCursor.moveToFirst()) {
                        int _idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                        int image_column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        int _id = mCursor.getInt(_idColumn);
                        String img_path = mCursor.getString(image_column_index);
                        path = img_path;
                    }
                    mCursor.close();
                }

            } else {
                String[] proj = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
                Cursor mCursor = mCr.query(uri, proj, null, null, null);
                if (null != mCursor) {
                    if (mCursor.moveToFirst()) {
                        int _idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                        int image_column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        int _id = mCursor.getInt(_idColumn);
                        String img_path = mCursor.getString(image_column_index);
                        path = img_path;
                    }
                    mCursor.close();
                }
            }
        } else if ("file".equals(uri.getScheme())) {
            path = uri.getPath();
        }
        LogUtils.i("photo helper query path by uri: " + path);
        return path;
    }

    public interface OnPhotoGetListener {
        void onGetPhotoPath(String sourcePath, String processedPath);
    }

    public interface IUCrop {
        void onStartUCrop(@NonNull Uri source, @NonNull File file);
    }

    private static final class CompressThered implements Runnable {
        String sourcePath;
        OnPhotoGetListener l;

        public CompressThered(String sourcePath, OnPhotoGetListener l) {
            this.sourcePath = sourcePath;
            this.l = l;
        }

        @Override
        public void run() {
            try {
                if (null == mCompressedMap.get(sourcePath)) {
                    mCompressedMap.put(sourcePath, ImageCompressUtils.compressImage(sourcePath));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                HandlerHelper.handler().post(new Runnable() {
                    @Override
                    public void run() {
                        l.onGetPhotoPath(sourcePath, mCompressedMap.get(sourcePath));
                    }
                });
            }
        }
    }
}
