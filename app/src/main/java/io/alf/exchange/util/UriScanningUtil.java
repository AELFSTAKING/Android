package io.alf.exchange.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.tencent.bugly.crashreport.CrashReport;
import com.uuzuche.lib_zxing.camera.BitmapLuminanceSource;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import io.alf.exchange.App;

public class UriScanningUtil {

/*    public static Result scanImage(Uri uri) {
        Result result = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(App.getInstance().getContentResolver(), uri);
            bitmap = getSmallerBitmap(bitmap);
            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                Map<DecodeHintType, Object> hints = new LinkedHashMap<DecodeHintType, Object>();
                // 解码设置编码方式为：utf-8
                hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
                //优化精度
                hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                //复杂模式，开启PURE_BARCODE模式
                hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
                MultiFormatReader reader = new MultiFormatReader();
                //Reader reader = new QRCodeReader();
                try {
                    //result = reader.decode(binaryBitmap);
                    result = reader.decode(binaryBitmap, hints);
                } catch (NotFoundException *//*| ChecksumException | FormatException*//* e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public static Result scanImage(Uri uri) {
        Result result = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(App.getInstance().getContentResolver(), uri);
            bitmap = getSmallerBitmap(bitmap);
            if (bitmap != null) {
                MultiFormatReader multiFormatReader = new MultiFormatReader();
                // 解码的参数
                Hashtable<DecodeHintType, Object> hints = new Hashtable<>(4);
                // 可以解析的编码类型
                Vector<BarcodeFormat> decodeFormats = new Vector<>();
                if (decodeFormats.isEmpty()) {
                    decodeFormats = new Vector<>();

                    Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector<>(5);
                    // Universal Product Code (UPC 条形码)
                    PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
                    PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
                    // European Article Number(欧洲物品编码)
                    PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
                    PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
                    // Rich Site Summary (条形码)
                    // PRODUCT_FORMATS.add(BarcodeFormat.RSS_14);
                    Vector<BarcodeFormat> ONE_D_FORMATS = new Vector<>(PRODUCT_FORMATS.size() + 4);
                    ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
                    // (条形码)
                    ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
                    // (条形码)
                    ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
                    // (条形码)
                    ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
                    // (交叉二五条码)
                    ONE_D_FORMATS.add(BarcodeFormat.ITF);
                    Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector<>(1);
                    // QR Code (矩阵二维码)
                    QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
                    Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<>(1);
                    // Data Matrix (Data code 二维条码)
                    DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);

                    // 这里设置可扫描的类型，我这里选择了都支持
                    // decodeFormats.addAll(ONE_D_FORMATS);
                    decodeFormats.addAll(QR_CODE_FORMATS);
                    decodeFormats.addAll(DATA_MATRIX_FORMATS);
                }
                hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
                //优化精度
                //hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                //复杂模式，开启PURE_BARCODE模式
                //hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
                // 设置继续的字符编码格式为UTF8
                // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
                // 设置解析配置参数
                multiFormatReader.setHints(hints);
                // 开始对图像资源解码
                try {
                    BitmapLuminanceSource source = new BitmapLuminanceSource(bitmap);
                    HybridBinarizer hybridBinarizer = new HybridBinarizer(source);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);
                    result = multiFormatReader.decodeWithState(binaryBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
        return result;
    }

    private static Bitmap getSmallerBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int size = bitmap.getWidth() * bitmap.getHeight() / 160000;
            if (size <= 1) {
                return bitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postScale((float) (1 / Math.sqrt(size)), (float) (1 / Math.sqrt(size)));
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        }
        return null;
    }
}
