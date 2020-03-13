package io.alf.exchange.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.Intents;
import com.gyf.immersionbar.ImmersionBar;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.alf.exchange.Constant;
import io.alf.exchange.R;
import io.alf.exchange.dialog.ErrorDialog;
import io.alf.exchange.util.PhotoHelper;
import io.alf.exchange.util.UriScanningUtil;
import io.tick.base.mvp.BaseActivity;
import io.tick.base.util.RxBindingUtils;

public class ScannerActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvMenu;
    @BindView(R.id.zxing_barcode_scanner)
    DecoratedBarcodeView barcodeScannerView;
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private BeepManager beepManager;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || !isValidResult(
                    result.getText())) {//|| result.getText().equals(lastText)
                // Prevent duplicate scans
                return;
            }

            beepManager.playBeepSoundAndVibrate();

            setResult(Activity.RESULT_OK, resultIntent(result, null));
            finish();
            //Added preview of scanned barcode
            //result.getBitmapWithResultPoints(Color.YELLOW)
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    protected boolean isValidResult(String result) {
        return true;
    }


    public static final String CONTENT = "content";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scanner;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("扫描二维码");
        String tip = getIntent().getStringExtra(CONTENT);
        if (!TextUtils.isEmpty(tip)) {
            tvTip.setText(tip);
        }
        tvMenu.setText(getString(R.string.album));
        beepManager = new BeepManager(this);
        barcodeScannerView.setTorchListener(this);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE,
                BarcodeFormat.CODE_39);
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.decodeContinuous(callback);
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(aVoid -> PhotoHelper.onOpenAlbum(ScannerActivity.this), tvMenu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.RC_ACTION_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                ImageScanningTask scanningTask = new ImageScanningTask(uri,
                        result -> {
                            if (result != null) {
                                Log.i("Jockey", "Jockey result : " + result);
/*                                Intent intent = new Intent();
                                intent.putExtra("result", result.getText());
                                setResult(RESULT_OK, intent);
                                finish();*/
                                callback.barcodeResult(new BarcodeResult(result, null));
                            } else {
                                new ErrorDialog(ScannerActivity.this,
                                        getString(R.string.scan_failed)).show();
                            }
                        });
                scanningTask.execute();
            }
        }
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH);
    }

    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fullScreen(true)
                .init();
    }

    public static Intent resultIntent(BarcodeResult rawResult, String barcodeImagePath) {
        Intent intent = new Intent(Intents.Scan.ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
        intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
        byte[] rawBytes = rawResult.getRawBytes();
        if (rawBytes != null && rawBytes.length > 0) {
            intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
        }
        Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
        if (metadata != null) {
            if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
                        metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
            }
            Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
            if (orientation != null) {
                intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
            }
            String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
            if (ecLevel != null) {
                intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
            }
            @SuppressWarnings("unchecked")
            Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(
                    ResultMetadataType.BYTE_SEGMENTS);
            if (byteSegments != null) {
                int i = 0;
                for (byte[] byteSegment : byteSegments) {
                    intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                    i++;
                }
            }
        }
        if (barcodeImagePath != null) {
            intent.putExtra(Intents.Scan.RESULT_BARCODE_IMAGE_PATH, barcodeImagePath);
        }
        return intent;
    }

    public static class ImageScanningTask extends AsyncTask<Uri, Void, Result> {
        private Uri uri;
        private ImageScanningCallback callback;

        public ImageScanningTask(Uri uri, ImageScanningCallback callback) {
            this.uri = uri;
            this.callback = callback;
        }

        @Override
        protected Result doInBackground(Uri... params) {
            return UriScanningUtil.scanImage(uri);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            callback.onFinishScanning(result);
        }

        public interface ImageScanningCallback {
            void onFinishScanning(Result result);
        }
    }
}
