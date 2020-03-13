package com.uuzuche.lib_zxing.activity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.uuzuche.lib_zxing.QRCodeAnalyzeCallback;
import com.uuzuche.lib_zxing.R;
import com.uuzuche.lib_zxing.camera.CameraManager;
import com.uuzuche.lib_zxing.decoding.CaptureActivityHandler;
import com.uuzuche.lib_zxing.decoding.DecodeFormatManager;
import com.uuzuche.lib_zxing.decoding.InactivityTimer;
import com.uuzuche.lib_zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * 自定义实现的扫描Fragment
 */
public class CaptureFragment extends Fragment implements SurfaceHolder.Callback {

    /**
     * 自定义实现扫描 Fragment 的界面。注意必须包含 viewfinder_view 和 preview_view。
     */
    public static final String ARG_LAYOUT_ID = "layoutId";
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = mediaPlayer -> mediaPlayer.seekTo(0);
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private SurfaceHolder surfaceHolder;
    private QRCodeAnalyzeCallback analyzeCallback;
    private Camera camera;

    public static CaptureFragment newInstance(int layoutId) {
        Bundle argument = new Bundle();
        argument.putInt(ARG_LAYOUT_ID, layoutId);
        CaptureFragment contentFragment = new CaptureFragment();
        contentFragment.setArguments(argument);
        return contentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.init(getActivity().getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this.getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QRCodeAnalyzeCallback) {
            analyzeCallback = (QRCodeAnalyzeCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        analyzeCallback = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View view = null;
        if (bundle != null) {
            int layoutId = bundle.getInt(ARG_LAYOUT_ID);
            if (layoutId != -1) {
                view = inflater.inflate(layoutId, null);
            }
        }

        if (view != null) {
            viewfinderView = view.findViewById(R.id.zxing_viewfinder_view);
            SurfaceView surfaceView = view.findViewById(R.id.zxing_preview_view);
            surfaceHolder = surfaceView.getHolder();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        //        decodeFormats = null;设置扫描格式只是二维码
        if (decodeFormats == null) {
            decodeFormats = DecodeFormatManager.QR_CODE_FORMATS;
        }
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(FragmentActivity.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
    }

    /**
     * Handler scan result
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        //成功提示音并震动
        playBeepSoundAndVibrate();

        if (result == null || TextUtils.isEmpty(result.getText())) {
            if (analyzeCallback != null) {
                analyzeCallback.onQRCodeAnalyzeFailed();
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onQRCodeAnalyzeSuccess(barcode, result.getText());
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            camera = CameraManager.get().getCamera();
        } catch (Exception e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet, viewfinderView);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        System.out.println("surfaceChanged" + width + "," + height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        if (camera != null) {
            if (CameraManager.get().isPreviewing()) {
                if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                    camera.setPreviewCallback(null);
                }
                camera.stopPreview();
                CameraManager.get().getPreviewCallback().setHandler(null, 0);
                CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
                CameraManager.get().setPreviewing(false);
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VIBRATE_DURATION);
            }
        }
    }

}
