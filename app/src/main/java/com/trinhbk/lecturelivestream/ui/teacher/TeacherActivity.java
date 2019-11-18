package com.trinhbk.lecturelivestream.ui.teacher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.camerakit.CameraKitView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.pedro.rtplibrary.rtmp.RtmpDisplay;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.SpenSettingTextInfo;
import com.samsung.android.sdk.pen.SpenSettingViewInterface;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectContainer;
import com.samsung.android.sdk.pen.document.SpenObjectImage;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenObjectTextBox;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenContextMenuItemInfo;
import com.samsung.android.sdk.pen.engine.SpenControlBase;
import com.samsung.android.sdk.pen.engine.SpenControlListener;
import com.samsung.android.sdk.pen.engine.SpenFlickListener;
import com.samsung.android.sdk.pen.engine.SpenObjectRuntime;
import com.samsung.android.sdk.pen.engine.SpenObjectRuntimeInfo;
import com.samsung.android.sdk.pen.engine.SpenObjectRuntimeManager;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTextChangeListener;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.plugin.interfaces.SpenObjectRuntimeInterface;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingTextLayout;
import com.trinhbk.lecturelivestream.MainApplication;
import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.network.LiveSiteService;
import com.trinhbk.lecturelivestream.network.response.FileResponse;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.dialog.settime.SettingTimeTempBushDFragment;
import com.trinhbk.lecturelivestream.ui.dialog.settingvideo.SettingVideoDFragment;
import com.trinhbk.lecturelivestream.utils.AppPreferences;
import com.trinhbk.lecturelivestream.utils.Constants;
import com.trinhbk.lecturelivestream.utils.DeviceUtil;
import com.trinhbk.lecturelivestream.utils.RecordUtil;
import com.trinhbk.lecturelivestream.utils.ToastUtil;
import com.trinhbk.lecturelivestream.utils.Utilities;
import com.trinhbk.lecturelivestream.youtube.YouTubeApi;
import com.trinhbk.lecturelivestream.youtube.YouTubeNewSingleton;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class TeacherActivity extends BaseActivity implements SettingVideoDFragment.OnClickSettingVideo, ConnectCheckerRtmp, SettingTimeTempBushDFragment.OnClickSettingTime {

    private static final String TAG = TeacherActivity.class.getSimpleName();

    private LiveSiteService liveSiteService = MainApplication.getLiveSiteService();

    private final int MODE_PEN = 0;
    private final int MODE_IMG_OBJ = 1;
    private final int MODE_TEXT_OBJ = 2;
    private int mMode = MODE_PEN;
    private int mToolType = SpenSurfaceView.TOOL_SPEN;
    private final int CONTEXT_MENU_RUN_ID = 0;
    private long onTimeRecord = -1;

    private static final int DISPLAY_WIDTH = 1920;
    private static final int DISPLAY_HEIGHT = 1080;
    private static final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 99;
    private static final int REQUEST_CODE_SELECT_IMAGE = 98;
    private static final int REQUEST_CODE_RECORD = 94;
    private static final int REQUEST_CODE_STREAM = 95;
    static final int REQUEST_CAMERA_PERMISSION = 1009;
    private TextureView textureView;
    //Check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int CAMERA_BACK = 0;
    private static final int CAMERA_FONT = 1;
    private static final long MIN_TIME_RECORD = 6000L;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Handler mStrokeHandler;

    private ImageButton ibBrush;
    private ImageButton ibTempBrush;
    private ImageButton ibEraser;
    private ImageButton ibAddImageBackground;
    private ImageButton ibCaptureScreen;
    private ImageButton ibInsertImage;
    private ImageButton ibInsertFile;
    private ImageButton ibAddCamera;
    private ImageButton ibAddText;
    private ImageButton ibSelection;
    private ImageButton ibRecognizeShape;
    private ImageButton ibAddPage;
    private ImageButton ibUndo;
    private ImageButton ibRedo;
    private ImageButton ibRecord;
    private ImageButton ibSave;
    private TextView tvNumberPage;
    private Chronometer chronometer;

    private FrameLayout penViewContainer;
    private RelativeLayout penViewLayout;

    private SpenNoteDoc mPenNoteDoc;
    private SpenPageDoc mPenPageDoc;
    private SpenSurfaceView mPenSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    @SuppressWarnings("deprecation")
    private SpenSettingEraserLayout mEraserSettingView;
    private SpenSettingTextLayout mTextSettingView;

    private SpenObjectRuntimeManager mSpenObjectRuntimeManager;
    private List<SpenObjectRuntimeInfo> mSpenObjectRuntimeInfoList;
    private SpenObjectRuntimeInfo mObjectRuntimeInfo;
    private SpenObjectRuntime mVideoRuntime;
    private String mAccount;
    private static RtmpDisplay rtmpDisplay;
    private int timeTempBush = 2;
    private boolean isShowSetTime = true;
    private String pathVideo;
    private int frameRate;
    private int bitRate;
    private int mScreenDensity;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    CameraDevice.StateCallback stateCallback;
    TextureView.SurfaceTextureListener textureListener;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private FFmpeg fFmpeg;
    CameraCharacteristics characteristics;
    private boolean isSaveRecord = true;
    private boolean checkSessionRecord = false;
    private ArrayList<String> listRecordsPath = new ArrayList<>();
    private ArrayList<String> listRecordsName = new ArrayList<>();
    private int recordStatus = 0;
    private Boolean runningChronometer = false;
    private Long pauseOffset = 0L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        initStateCallback();
        initTextureListener();
        initViews();
        initSamSungPen();
        initMedia();
        initListener();
        loadFFmpegLibrary();
    }

    private void startCountUpTimer() {
        if (!runningChronometer) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            runningChronometer = true;
        }
    }

    private void stopCountUpTimer() {
        if (runningChronometer) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            runningChronometer = false;
        }
    }

    private void resetCountUpTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0L;
    }

    private void loadFFmpegLibrary() {
        if (fFmpeg == null) {
            fFmpeg = FFmpeg.getInstance(this);
            try {
                fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                    @Override
                    public void onFailure() {
                        Log.d(TAG, "LoadLibrary: onFailure");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "LoadLibrary: onSuccess");
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initTextureListener() {
        textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                DeviceUtil.getInstance().transformImage(TeacherActivity.this, textureView, width, height);
                openCamera(CAMERA_FONT);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };

    }

    private void initViews() {
        ibBrush = findViewById(R.id.ivPen);
        ibTempBrush = findViewById(R.id.ibTempBrush);
        ibEraser = findViewById(R.id.ivEraser);
        ibAddImageBackground = findViewById(R.id.ivAddImage);
        ibInsertImage = findViewById(R.id.ibInsertImage);
        ibInsertFile = findViewById(R.id.ibInsertFile);
        ibCaptureScreen = findViewById(R.id.ibCaptureScreen);
        ibAddCamera = findViewById(R.id.ibAddVideo);
        ibAddText = findViewById(R.id.ibText);
        ibSelection = findViewById(R.id.ibSelection);
        ibRecognizeShape = findViewById(R.id.ibBound);
        ibAddPage = findViewById(R.id.ibAddPage);
        ibUndo = findViewById(R.id.ivUndo);
        ibRedo = findViewById(R.id.ivRedo);
        ibRecord = findViewById(R.id.ibRecord);
        ibSave = findViewById(R.id.ibSave);
        tvNumberPage = findViewById(R.id.tvPageNumber);
        penViewContainer = findViewById(R.id.spenViewContainer);
        penViewLayout = findViewById(R.id.spenViewLayout);
        textureView = (TextureView) findViewById(R.id.textureView);
        //From Java 1.4 , you can use keyword 'assert' to check expression true or false
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        chronometer = findViewById(R.id.simpleChronometer);
    }

    private void initListener() {
        textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!avoidDoubleClick()) {
                    cameraDevice.close();
                    if (cameraId.equals(String.valueOf(CAMERA_BACK))) {
                        openCamera(CAMERA_FONT);
                    } else {
                        openCamera(CAMERA_BACK);
                    }
                }
            }
        });
    }

    private void initSamSungPen() {
        // Initialize Pen.
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(TeacherActivity.this);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
            finish();
        } catch (Exception ex) {
            finish();
        }

        // Create PenSettingView
        mPenSettingView = new SpenSettingPenLayout(TeacherActivity.this, "", penViewLayout);
        if (mPenSettingView == null) {
            finish();
        }
        penViewContainer.addView(mPenSettingView);

        // Create EraserSettingView
        //noinspection deprecation
        mEraserSettingView = new SpenSettingEraserLayout(TeacherActivity.this, "", penViewLayout);
        if (mEraserSettingView == null) {
            finish();
        }
        penViewContainer.addView(mEraserSettingView);

        // Create TextSettingView.
        mTextSettingView = new SpenSettingTextLayout(TeacherActivity.this, "", new HashMap<>(), penViewLayout);
        if (mTextSettingView == null) {
            finish();
        }
        penViewContainer.addView(mTextSettingView);

        // Create SurfacePenView
        mPenSurfaceView = new SpenSurfaceView(TeacherActivity.this);
        if (mPenSurfaceView == null) {
            finish();
        }
        penViewLayout.addView(mPenSurfaceView);

        mPenSettingView.setCanvasView(mPenSurfaceView);
        mEraserSettingView.setCanvasView(mPenSurfaceView);

        // Get the dimensions of the screen.
        Display display = getWindowManager().getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        // Create SpenNoteDoc.
        try {
            mPenNoteDoc = new SpenNoteDoc(TeacherActivity.this, rect.width(), rect.height());
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        // After adding a page to NoteDoc, get an instance and set it as a member variable.
        mPenPageDoc = mPenNoteDoc.appendPage();
        mPenPageDoc.setBackgroundColor(0xFFD6E6F5);
        mPenPageDoc.clearHistory();

        // Set PageDoc to View.
        mPenSurfaceView.setPageDoc(mPenPageDoc, true);

        tvNumberPage.setText(String.format(getString(R.string.tv_teacher_page_number), mPenNoteDoc.getPageIndexById(mPenPageDoc.getId())));
        initPenSettingInfo();

        // Register the listeners.
        mPenSurfaceView.setColorPickerListener((color, x, y) -> {
            if (mPenSettingView != null) {
                SpenSettingPenInfo penInfo = mPenSettingView.getInfo();
                penInfo.color = color;
                mPenSettingView.setInfo(penInfo);
            }
        });
        mPenSurfaceView.setTextChangeListener(textChangeListener());
        mPenSurfaceView.setFlickListener(flickListener());
        mPenSurfaceView.setControlListener(mControlListener);
        mPenSurfaceView.setPreTouchListener(onPreTouchSurfaceViewListener);
        //noinspection deprecation
        mEraserSettingView.setEraserListener(() -> {
            // Handle the Clear All button in EraserSettingView.
            mPenPageDoc.removeAllObject();
            mPenSurfaceView.update();
        });

        mPenPageDoc.setHistoryListener(mHistoryListener);

        ibBrush.setOnClickListener(view -> {
//            mPenSurfaceView.closeControl();
            mPenSurfaceView.stopTemporaryStroke();
            mPenSurfaceView.setTouchListener(touchListenerBrush());
            if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE) {
                ibBrush.setSelected(true);
                ibTempBrush.setSelected(false);
                if (mPenSettingView.isShown()) {
                    mPenSettingView.setVisibility(View.GONE);
                } else {
                    //noinspection deprecation
                    mPenSettingView.setViewMode(SpenSettingPenLayout.VIEW_MODE_EXTENSION);
                    mPenSettingView.setVisibility(View.VISIBLE);
                }
            } else {
                selectButton(ibBrush);
                mMode = MODE_PEN;
                mPenSurfaceView.setToolTypeAction(mToolType, SpenSettingViewInterface.ACTION_STROKE);
            }
        });

        ibTempBrush.setOnClickListener(view -> {
            mPenSurfaceView.startTemporaryStroke();
            if (!isShowSetTime) {
                SettingTimeTempBushDFragment.newInstance().show(getSupportFragmentManager(), SettingTimeTempBushDFragment.class.getSimpleName());
            } else {
                mPenSurfaceView.setTouchListener(touchListenerTemporaryBrush());
                isShowSetTime = false;
            }
            mMode = MODE_PEN;
            selectButton(ibTempBrush);
            mPenSurfaceView.setToolTypeAction(mToolType, SpenSettingViewInterface.ACTION_STROKE);
//            }
        });

        ibEraser.setOnClickListener(view -> {
            // If it is in eraser tool mode.
            if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_ERASER) {
                // If EraserSettingView is displayed, close it.
                if (mEraserSettingView.isShown()) {
                    mEraserSettingView.setVisibility(View.GONE);
                    // If EraserSettingView is not displayed, display it.
                } else {
                    //noinspection deprecation
                    mEraserSettingView.setViewMode(SpenSettingEraserLayout.VIEW_MODE_NORMAL);
                    mEraserSettingView.setVisibility(View.VISIBLE);
                }
                // If it is not in eraser tool mode, change it to eraser tool mode.
            } else {
                selectButton(ibEraser);
                mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_ERASER);
            }
        });

        ibAddImageBackground.setOnClickListener(view -> {
            closeSettingView();
            callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_BACKGROUND);
        });

        ibInsertImage.setOnClickListener(view -> {
            closeSettingView();
            callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE);
        });

        ibAddCamera.setOnClickListener(view -> {
            ibAddCamera.setClickable(false);
            mPenSurfaceView.closeControl();
            createObjectRuntime();
        });

        ibCaptureScreen.setOnClickListener(view -> {
            closeSettingView();
            capturePenSurfaceView();
        });

        ibAddText.setOnClickListener(view -> {
            mPenSurfaceView.closeControl();
            mPenSurfaceView.stopTemporaryStroke();
            mPenSurfaceView.setTouchListener(touchListenerBrush());
            // When Pen is in text mode.
            if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_TEXT) {
                // Close TextSettingView if TextSettingView is displayed.
                if (mTextSettingView.isShown()) {
                    mTextSettingView.setVisibility(View.GONE);
                    // Display TextSettingView if TextSettingView is not displayed.
                } else {
                    //noinspection deprecation
                    mTextSettingView.setViewMode(SpenSettingTextLayout.VIEW_MODE_NORMAL);
                    mTextSettingView.setVisibility(View.VISIBLE);
                }
                // Switch to text mode unless Pen is in text mode.
            } else {
                mMode = MODE_TEXT_OBJ;
                selectButton(ibAddText);
                mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_TEXT);
            }
        });

        ibSelection.setOnClickListener(view -> {
            selectButton(ibSelection);
            mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_SELECTION);
        });

        ibRecognizeShape.setOnClickListener(view -> {
            mMode = MODE_PEN;
            selectButton(ibRecognizeShape);
            mPenSurfaceView.closeControl();
            mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_RECOGNITION);
        });

        ibInsertFile.setOnClickListener(view -> {
            mPenSurfaceView.closeControl();
            closeSettingView();
            new ChooserDialog().with(TeacherActivity.this)
                    .withStartFile(Environment.getExternalStorageState())
                    .withChosenListener((path, pathFile) -> {
                        RequestBody requestFile = RequestBody.create(MediaType.parse(Utilities.getMimeType(path)), pathFile);

                        MultipartBody.Part body = MultipartBody.Part.createFormData("File", pathFile.getName(), requestFile);

                        showLoading();
                        if (Utilities.getMimeType(path).equals("application/pdf")) {
                            getFilePDF(body);
                        } else if (Utilities.getMimeType(path).equals("application/ppt")) {
                            getFilePPT(body);
                        } else if (Utilities.getMimeType(path).equals("application/pptx")) {
                            getFilePPTX(body);
                        } else {
                            showErrorDialog("Thông báo", "Định dạng không thể convert được", liveDialog -> liveDialog.dismiss());
                        }
                    })
                    .build()
                    .show();
        });

        ibAddPage.setOnClickListener(view -> {
            mPenSurfaceView.setPageEffectListener(() -> ibAddPage.setClickable(true));
            mPenSurfaceView.closeControl();
            closeSettingView();
            // Create a page next to the current page.
            mPenPageDoc = mPenNoteDoc.insertPage(mPenNoteDoc.getPageIndexById(mPenPageDoc.getId()) + 1);
            mPenPageDoc.setBackgroundColor(0xFFD6E6F5);
            mPenPageDoc.clearHistory();
            view.setClickable(false);
            mPenSurfaceView.setPageDoc(mPenPageDoc, SpenSurfaceView.PAGE_TRANSITION_EFFECT_RIGHT, SpenSurfaceView.PAGE_TRANSITION_EFFECT_TYPE_SHADOW, 0);
            tvNumberPage.setText(String.format(getString(R.string.tv_teacher_page_number), mPenNoteDoc.getPageIndexById(mPenPageDoc.getId())));
        });

        ibUndo.setOnClickListener(undoRedoOnClickListener);
        ibUndo.setEnabled(mPenPageDoc.isUndoable());

        ibRedo.setOnClickListener(undoRedoOnClickListener);
        ibRedo.setEnabled(mPenPageDoc.isRedoable());

        ibRecord.setOnClickListener(view -> {
            if (recordStatus == 1) {
                if (System.currentTimeMillis() - onTimeRecord < MIN_TIME_RECORD) {
                    showCautionDialog(getResources().getString(R.string.teacher_min_time_record_error), "", liveDialog -> {
                        liveDialog.dismiss();
                    });
                } else {
                    recordStatus = 0;
                    closeSettingView();
//                    Toast.makeText(TeacherActivity.this, "Video is saved", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Stopping Recording");
                    ToastUtil.getInstance().show(getString(R.string.teacher_save_pause));
                    stopScreenSharing();
                    stopCountUpTimer();
                }
            } else {
                checkSessionRecord = true;
                recordStatus = 1;
                if (listRecordsName.size() >= 1 && listRecordsPath.size() >= 1) {
                    onDone(RecordUtil.baseDir + "/" + listRecordsName.get(listRecordsName.size() - 1) + listRecordsName.size() + ".mp4", bitRate, frameRate, listRecordsName.get(listRecordsName.size() - 1) + listRecordsName.size());
                    ToastUtil.getInstance().show(getString(R.string.teacher_save_resume));
                } else {
                    SettingVideoDFragment dialogFragment = SettingVideoDFragment.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getSimpleName());
                }
            }
        });

        ibSave.setOnClickListener(view -> {
            if (System.currentTimeMillis() - onTimeRecord < MIN_TIME_RECORD) {
                showCautionDialog(getResources().getString(R.string.teacher_min_time_record_error), "", liveDialog -> {
                    liveDialog.dismiss();
                });
            } else {
                closeSettingView();
//                    Toast.makeText(TeacherActivity.this, "Video is saved", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Stopping Recording");
                stopScreenSharing();
                if (checkSessionRecord == true && listRecordsPath.size() >= 2) {
                    RecordUtil.getInstance().appendVideo(listRecordsPath, listRecordsName);
                }
                clearRecord();
            }
        });

        selectButton(ibBrush);

        // Set up the ObjectRuntimeManager.
        mSpenObjectRuntimeManager = new SpenObjectRuntimeManager(TeacherActivity.this);
        mSpenObjectRuntimeInfoList = new ArrayList<>();
        mSpenObjectRuntimeInfoList = mSpenObjectRuntimeManager.getObjectRuntimeInfoList();

        if (!isSpenFeatureEnabled) {
            mPenSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_STROKE);
//                    Toast.makeText(TeacherActivity.this, "Device does not support Spen. \n You can draw stroke by finger.", Toast.LENGTH_SHORT).show();
        }

    }

    private void initStateCallback() {
        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraDevice = camera;
                createCameraPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                cameraDevice.close();
            }

            @Override
            public void onError(@NonNull CameraDevice cameraDevice, int i) {
                cameraDevice.close();
                cameraDevice = null;
            }
        };
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(TeacherActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(int rotate) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[rotate];
            characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //Check realtime permission if run higher API 23
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDoneSetTime(String time) {
        isShowSetTime = true;
        mPenSurfaceView.setTouchListener(touchListenerTemporaryBrush());
    }

    private class SlowAsyncTask extends AsyncTask<Response<FileResponse>, Void, Void> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(Response<FileResponse>[] responses) {
            for (int i = 0; i < responses[0].body().getFiles().size(); i++) {
                File file = Utilities.saveImage(responses[0].body().getFiles().get(i).getFileName(), responses[0].body().getFiles().get(i).getFileData());
                mPenPageDoc.setBackgroundImage(file.getAbsolutePath());
                mPenPageDoc.setBackgroundImageMode(SpenPageDoc.BACKGROUND_IMAGE_MODE_FIT);
                mPenPageDoc = mPenNoteDoc.insertPage(mPenNoteDoc.getPageIndexById(mPenPageDoc.getId()) + 1);
                mPenPageDoc.setBackgroundColor(0xFFD6E6F5);
                mPenPageDoc.clearHistory();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            hideLoading();
            mPenSurfaceView.update();
        }
    }

    private void getFilePDF(MultipartBody.Part body) {
        liveSiteService.uploadFilePDF(body).enqueue(new Callback<FileResponse>() {
            @Override
            public void onResponse(Call<FileResponse> call, Response<FileResponse> response) {
                Log.d(TAG, "onResponse: ");
                hideLoading();
                SlowAsyncTask slowAsynTask = new SlowAsyncTask();
                slowAsynTask.execute(response);
                Log.d(TAG, "onResponse: save");
            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {
                hideLoading();
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void getFilePPT(MultipartBody.Part body) {
        liveSiteService.uploadFilePPT(body).enqueue(new Callback<FileResponse>() {
            @Override
            public void onResponse(Call<FileResponse> call, Response<FileResponse> response) {
                Log.d(TAG, "onResponse: ");
                hideLoading();
                SlowAsyncTask slowAsynTask = new SlowAsyncTask();
                slowAsynTask.execute(response);
                Log.d(TAG, "onResponse: save");
            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {
                hideLoading();
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void getFilePPTX(MultipartBody.Part body) {
        liveSiteService.uploadFilePPTX(body).enqueue(new Callback<FileResponse>() {
            @Override
            public void onResponse(Call<FileResponse> call, Response<FileResponse> response) {
                Log.d(TAG, "onResponse: ");
                hideLoading();
                SlowAsyncTask slowAsynTask = new SlowAsyncTask();
                slowAsynTask.execute(response);
                Log.d(TAG, "onResponse: save");
            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {
                hideLoading();
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @Override
    public void onDone(String pathVideo, int bitRate, int frameRate, String originName) {
        this.pathVideo = pathVideo;
        this.bitRate = bitRate;
        this.frameRate = frameRate;
        listRecordsName.add(originName);
        listRecordsPath.add(RecordUtil.baseDir + "/" + originName + ".mp4");
        showLoading();
        if (AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.IS_LOGINED)) {
            startActivityForResult(rtmpDisplay.sendIntent(), REQUEST_CODE_STREAM);
        } else {
            startActivityForResult(rtmpDisplay.sendIntent(), REQUEST_CODE_RECORD);
        }
    }

    private void stopScreenSharing() {
        Log.i(TAG, "MediaProjection Stopped");
        isSaveRecord = true;
        if (rtmpDisplay.isRecording()) {
            rtmpDisplay.stopRecord();
            ibRecord.setImageResource(R.drawable.ic_record);
            ibRecord.setEnabled(true);
        }
        if (rtmpDisplay.isStreaming()) rtmpDisplay.stopStream();
        if (AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.LOGIN_FROM_FACEBOOK)) {
            //  TODO STOP LIVE Facebook
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.USER_ID) + "?end_live_video=true",
                    null,
                    HttpMethod.POST,
                    responseStream -> {
                        Log.d(TAG, "stopScreenSharing: " + responseStream.toString());
                    }
            ).executeAsync();
            AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.RTMP_FACEBOOK, "");
        } else {
            //  TODO STOP LIVE YouTube
            if (AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.IS_LIVESTREAMED)) {
                EndEventTask endEventTask = new EndEventTask();
                String broadcastID = AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.BROADCAST_ID);
                endEventTask.execute(broadcastID);
                AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.IS_LIVESTREAMED, false);
                AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.RTMP_GOOGLE, "");
                AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.BROADCAST_ID, "");
            }

        }
    }

    private void showListVideo() {
        ToastUtil.getInstance().show(getString(R.string.teacher_save_success));
        finish();
    }

    private void initMedia() {
        rtmpDisplay = getInstance();
    }

    private RtmpDisplay getInstance() {
        if (rtmpDisplay == null) {
            return new RtmpDisplay(this, false, this);
        } else {
            return rtmpDisplay;
        }
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(() -> {
            hideLoading();
            Toast.makeText(TeacherActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
            try {
                rtmpDisplay.startRecord(pathVideo);
                ibRecord.setImageResource(R.drawable.ic_stop);
            } catch (IOException e) {
                rtmpDisplay.stopRecord();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(String reason) {
        runOnUiThread(() -> {
            hideLoading();
            Toast.makeText(TeacherActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(() -> {
            hideLoading();
            Toast.makeText(TeacherActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(() -> {
            hideLoading();
//            Toast.makeText(TeacherActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(() -> {
            hideLoading();
//            Toast.makeText(TeacherActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
        });
    }

    SpenControlListener mControlListener = new SpenControlListener() {
        @Override
        public boolean onCreated(ArrayList<SpenObjectBase> objectList, ArrayList<Rect> relativeRectList, ArrayList<SpenContextMenuItemInfo> menu, ArrayList<Integer> styleList, int pressType, PointF point) {
            if (objectList == null) {
                return false;
            }
            // Display the context menu if any SOR information is found.
            if (objectList.get(0).getSorInfo() != null) {
                menu.add(new SpenContextMenuItemInfo(CONTEXT_MENU_RUN_ID, "Run", true));
                return true;
            }
            return true;
        }

        @Override
        public boolean onMenuSelected(
                ArrayList<SpenObjectBase> objectList, int itemId) {
            if (objectList == null) {
                return true;
            }
            if (itemId == CONTEXT_MENU_RUN_ID) {
                SpenObjectBase object = objectList.get(0);
                mPenSurfaceView.getControl().setContextMenuVisible(false);
                mPenSurfaceView.getControl().setStyle(SpenControlBase.STYLE_BORDER_STATIC);
                // Set up listener and make it play.
                mVideoRuntime.setListener(objectRuntimeListener);
                mVideoRuntime.start(object, getRealRect(object.getRect()),
                        mPenSurfaceView.getPan(), mPenSurfaceView.getZoomRatio(),
                        mPenSurfaceView.getFrameStartPosition(), penViewLayout);
                mPenSurfaceView.update();
            }
            return false;
        }

        @Override
        public void onObjectChanged(ArrayList<SpenObjectBase> object) {
        }

        @Override
        public void onRectChanged(RectF rect, SpenObjectBase object) {
        }

        @Override
        public void onRotationChanged(float angle, SpenObjectBase objectBase) {
        }

        @Override
        public boolean onClosed(ArrayList<SpenObjectBase> objectList) {
            if (mVideoRuntime != null)
                mVideoRuntime.stop(true);
            return false;
        }
    };

    private SpenTouchListener onPreTouchSurfaceViewListener = (view, event) -> {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                enableButton(false);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                enableButton(true);
                break;
        }
        return false;
    };

    private void enableButton(boolean isEnable) {
        ibSelection.setEnabled(isEnable);
        ibBrush.setEnabled(isEnable);
        ibAddCamera.setEnabled(isEnable);
    }

    private RectF getRealRect(RectF rect) {
        float panX = mPenSurfaceView.getPan().x;
        float panY = mPenSurfaceView.getPan().y;
        float zoom = mPenSurfaceView.getZoomRatio();
        PointF startPoint = mPenSurfaceView.getFrameStartPosition();
        RectF realRect = new RectF();
        realRect.set(
                (rect.left - panX) * zoom + startPoint.x,
                (rect.top - panY) * zoom + startPoint.y,
                (rect.right - panX) * zoom + startPoint.x,
                (rect.bottom - panY) * zoom + startPoint.y
        );
        return realRect;
    }

    private void createObjectRuntime() {
        if (mSpenObjectRuntimeInfoList == null || mSpenObjectRuntimeInfoList.size() == 0) {
            return;
        }
        try {
            for (SpenObjectRuntimeInfo info : mSpenObjectRuntimeInfoList) {
                if (info.name.equalsIgnoreCase("Video")) {
                    mVideoRuntime = mSpenObjectRuntimeManager.createObjectRuntime(info);
                    mObjectRuntimeInfo = info;
                    startObjectRuntime();
                    return;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startObjectRuntime() {
        if (mVideoRuntime == null) {
            return;
        }
        SpenObjectBase objectBase = null;
        switch (mVideoRuntime.getType()) {
            case SpenObjectRuntimeInterface.TYPE_NONE:
                return;
            case SpenObjectRuntimeInterface.TYPE_IMAGE:
                objectBase = new SpenObjectImage();
                break;
            case SpenObjectRuntimeInterface.TYPE_STROKE:
                objectBase = new SpenObjectStroke();
                break;
            case SpenObjectRuntimeInterface.TYPE_CONTAINER:
                objectBase = new SpenObjectContainer();
                break;
            default:
                break;
        }
        if (objectBase == null) {
//            Toast.makeText(this, "Has no selected object.", Toast.LENGTH_SHORT).show();
            return;
        }
        objectBase.setSorInfo(mObjectRuntimeInfo.className);
        objectBase.setOutOfViewEnabled(false);
        mVideoRuntime.setListener(objectRuntimeListener);
        mPenPageDoc.appendObject(objectBase);
        mPenPageDoc.selectObject(objectBase);
        mPenSurfaceView.update();
        mPenSurfaceView.getControl().setContextMenuVisible(false);
        mVideoRuntime.start(objectBase,
                new RectF(0, 0, mPenPageDoc.getWidth(), mPenPageDoc.getHeight()),
                mPenSurfaceView.getPan(), mPenSurfaceView.getZoomRatio(),
                mPenSurfaceView.getFrameStartPosition(), penViewLayout);
    }

    SpenObjectRuntime.UpdateListener objectRuntimeListener = new SpenObjectRuntime.UpdateListener() {

        @Override
        public void onCompleted(Object objectBase) {
            if (mPenSurfaceView != null) {
                SpenControlBase control = mPenSurfaceView.getControl();
                if (control != null) {
                    control.setContextMenuVisible(true);
                    //noinspection deprecation
                    mPenSurfaceView.updateScreenFrameBuffer();
                    mPenSurfaceView.update();
                }
            }
            ibAddCamera.setClickable(true);
        }

        @Override
        public void onObjectUpdated(RectF rect, Object objectBase) {
            if (mPenSurfaceView != null) {
                SpenControlBase control = mPenSurfaceView.getControl();
                if (control != null) {
                    control.fit();
                    control.invalidate();
                    mPenSurfaceView.update();
                }
            }
        }

        @Override
        public void onCanceled(int state, Object objectBase) {
            if (state == SpenObjectRuntimeInterface.CANCEL_STATE_INSERT) {
                mPenPageDoc.removeObject((SpenObjectBase) objectBase);
                mPenPageDoc.removeSelectedObject();
                mPenSurfaceView.closeControl();
                mPenSurfaceView.update();
            } else if (state == SpenObjectRuntimeInterface.CANCEL_STATE_RUN) {
                mPenSurfaceView.closeControl();
                mPenSurfaceView.update();
            }
            ibAddCamera.setClickable(true);
        }
    };

    @NonNull
    private SpenFlickListener flickListener() {
        return direction -> {
            int pageIndex = mPenNoteDoc.getPageIndexById(mPenPageDoc.getId());
            int pageCount = mPenNoteDoc.getPageCount();
            boolean checkSetPageDoc = false;
            if (pageCount > 1) {
                // Flick left and turn to the previous page.
                if (direction == SpenFlickListener.DIRECTION_LEFT) {
                    mPenPageDoc = mPenNoteDoc.getPage((pageIndex + pageCount - 1) % pageCount);
                    if (mPenSurfaceView.setPageDoc(mPenPageDoc, SpenSurfaceView.PAGE_TRANSITION_EFFECT_LEFT, SpenSurfaceView.PAGE_TRANSITION_EFFECT_TYPE_SHADOW, 0)) {
                        checkSetPageDoc = true;
                    } else {
                        checkSetPageDoc = false;
                        mPenPageDoc = mPenNoteDoc.getPage(pageIndex);
                    }
                    // Flick right and turn to the next page.
                } else if (direction == SpenFlickListener.DIRECTION_RIGHT) {
                    mPenPageDoc = mPenNoteDoc.getPage((pageIndex + 1) % pageCount);
                    if (mPenSurfaceView.setPageDoc(mPenPageDoc, SpenSurfaceView.PAGE_TRANSITION_EFFECT_RIGHT, SpenSurfaceView.PAGE_TRANSITION_EFFECT_TYPE_SHADOW, 0)) {
                        checkSetPageDoc = true;
                    } else {
                        checkSetPageDoc = false;
                        mPenPageDoc = mPenNoteDoc.getPage(pageIndex);
                    }
                }
                if (checkSetPageDoc) {
                    tvNumberPage.setText(String.format(getString(R.string.tv_teacher_page_number), mPenNoteDoc.getPageIndexById(mPenPageDoc.getId())));
                }
                return true;
            }
            return false;
        };
    }

    @NonNull
    private SpenTextChangeListener textChangeListener() {
        return new SpenTextChangeListener() {
            @Override
            public void onChanged(SpenSettingTextInfo spenSettingTextInfo, int state) {
                if (mTextSettingView != null) {
                    if (state == CONTROL_STATE_SELECTED) {
                        mTextSettingView.setInfo(spenSettingTextInfo);
                    }
                }
            }

            @Override
            public boolean onSelectionChanged(int i, int i1) {
                return false;
            }

            @Override
            public void onMoreButtonDown(SpenObjectTextBox spenObjectTextBox) {

            }

            @Override
            public void onFocusChanged(boolean b) {

            }
        };
    }

    @NonNull
    private SpenTouchListener touchListenerBrush() {
        return (view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && event.getToolType(0) == mToolType) {
                // Checks whether the control is generated or not.
                SpenControlBase control = mPenSurfaceView.getControl();
                if (control == null) {
                    // When touching the screen in Insert ObjectImage mode.
                    if (mMode == MODE_IMG_OBJ) {
//                     Set the Bitmap file to ObjectImage.
                        SpenObjectImage imgObj = new SpenObjectImage();
                        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_shape);
                        imgObj.setImage(imageBitmap);
//                     Specify the location where ObjectImage is inserted and add PageDoc.
                        float imgWidth = imageBitmap.getWidth();
                        float imgHeight = imageBitmap.getHeight();
                        RectF rect1 = getRealPoint(event.getX(), event.getY(), imgWidth, imgHeight);
                        imgObj.setRect(rect1, true);
                        mPenPageDoc.appendObject(imgObj);
                        mPenSurfaceView.update();
                        imageBitmap.recycle();
                        return true;
//                     When touching the screen in Insert ObjectTextBox mode.
                    } else if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_TEXT) {
                        // Specify the location where ObjectTextBox is inserted and add PageDoc.
                        SpenObjectTextBox textObj = new SpenObjectTextBox();
                        RectF rect1 = getRealPoint(event.getX(), event.getY(), 0, 0);
                        rect1.right += 200;
                        rect1.bottom += 50;
                        textObj.setRect(rect1, true);
                        mPenPageDoc.appendObject(textObj);
                        mPenPageDoc.selectObject(textObj);
                        mPenSurfaceView.update();
                    }
                }
            }
            return false;
        };
    }

    @NonNull
    private SpenTouchListener touchListenerTemporaryBrush() {
        return (v, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // When ACTION_DOWN occurs before mStrokeRunnable is set in a queue, the mStrokeRunnable that waits is removed.
                    if (mStrokeHandler != null) {
                        mStrokeHandler.removeCallbacks(mStrokeRunnable);
                        mStrokeHandler = null;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Generate Handler to put mStrokeRunnable in a queue when it takes 1000 milliseconds after ACTION_UP occurred.
                    mStrokeHandler = new Handler();
                    mStrokeHandler.postDelayed(mStrokeRunnable, timeTempBush * 1000);
                    break;
            }
            return true;
        };
    }

    private final Runnable mStrokeRunnable = new Runnable() {
        @Override
        public void run() {
            // Get TemporaryStroke to resize the object by 1/2.
            mPenSurfaceView.stopTemporaryStroke();
            mPenSurfaceView.startTemporaryStroke();
            mPenSurfaceView.update();
        }
    };

    private RectF getRealPoint(float x, float y, float width, float height) {
        float panX = mPenSurfaceView.getPan().x;
        float panY = mPenSurfaceView.getPan().y;
        float zoom = mPenSurfaceView.getZoomRatio();
        width *= zoom;
        height *= zoom;
        RectF realRect = new RectF();
        realRect.set(
                (x - width / 2) / zoom + panX, (y - height / 2) / zoom + panY,
                (x + width / 2) / zoom + panX, (y + height / 2) / zoom + panY);
        return realRect;
    }

    private void callGalleryForInputImage(int nRequestCode) {
        // Get an image from the gallery.
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(galleryIntent, nRequestCode);
        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "Cannot find gallery.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void capturePenSurfaceView() {
        // Select the location to save the image.
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SPen/images";
        File fileCacheItem = new File(filePath);
        if (!fileCacheItem.exists()) {
            if (!fileCacheItem.mkdirs()) {
//                Toast.makeText(this, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        filePath = fileCacheItem.getPath() + "/CaptureImg.png";
        // Save the screen shot as a Bitmap.

//        SpenCapturePage  spenCapturePage = null;
//        spenCapturePage.setPageDoc(mPenPageDoc);
//        spenCapturePage.capturePage(1.0f);
        Bitmap imgBitmap = mPenSurfaceView.captureCurrentView(true);
        OutputStream out = null;
        try {
            // Save the Bitmap in the selected location.
            out = new FileOutputStream(filePath);
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
//            Toast.makeText(this, "Captured images were stored in the file \'CaptureImg.png\'.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            Toast.makeText(this, "Capture failed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgBitmap.recycle();
    }

    private View.OnClickListener undoRedoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPenPageDoc == null) {
                return;
            }
            // Undo button is clicked.
            if (v.equals(ibUndo)) {
                if (mPenPageDoc.isUndoable()) {
                    SpenPageDoc.HistoryUpdateInfo[] userData = mPenPageDoc.undo();
                    mPenSurfaceView.updateUndo(userData);
                }
                // Redo button is clicked.
            } else if (v.equals(ibRedo)) {
                if (mPenPageDoc.isRedoable()) {
                    SpenPageDoc.HistoryUpdateInfo[] userData = mPenPageDoc.redo();
                    mPenSurfaceView.updateRedo(userData);
                }
            }
        }
    };

    private SpenPageDoc.HistoryListener mHistoryListener = new SpenPageDoc.HistoryListener() {
        @Override
        public void onCommit(SpenPageDoc page) {
        }

        @Override
        public void onUndoable(SpenPageDoc page, boolean undoable) {
            // Enable or disable Undo button depending on its availability.
            ibUndo.setEnabled(undoable);
        }

        @Override
        public void onRedoable(SpenPageDoc page, boolean redoable) {
            // Enable or disable Redo button depending on its availability.
            ibRedo.setEnabled(redoable);
        }
    };

    private void selectButton(ImageView ivSelected) {
        ibBrush.setSelected(false);
        ibTempBrush.setSelected(false);
        ibEraser.setSelected(false);
        ibSelection.setSelected(false);
        ibAddText.setSelected(false);
        ibSelection.setSelected(false);
        ibRecognizeShape.setSelected(false);
        ivSelected.setSelected(true);
        closeSettingView();
    }

    private void closeSettingView() {
        // Close all the setting views.
        mEraserSettingView.setVisibility(SpenSurfaceView.GONE);
        mPenSettingView.setVisibility(SpenSurfaceView.GONE);
        mTextSettingView.setVisibility(SpenSurfaceView.GONE);
    }

    private void initPenSettingInfo() {
        // Initialize pen settings.
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.BLUE;
        penInfo.size = 10;
        mPenSurfaceView.setPenSettingInfo(penInfo);
        mPenSettingView.setInfo(penInfo);

        SpenSettingTextInfo textInfo = mTextSettingView.getInfo();
        textInfo.color = Color.BLACK;
        mTextSettingView.setInfo(textInfo);

        // Initialize eraser settings.
        SpenSettingEraserInfo eraserInfo = new SpenSettingEraserInfo();
        eraserInfo.size = 30;
        mPenSurfaceView.setEraserSettingInfo(eraserInfo);
        mEraserSettingView.setInfo(eraserInfo);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        // Get the cursor
        Cursor cursor = getContentResolver().query(contentURI, filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();
        //Get the column index of MediaStore.Images.Media.DATA
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        //Gets the String value in the column
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        return imgDecodableString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mAccount = AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.ACCOUNT_NAME);
            if (data == null) {
//                Toast.makeText(this, "Cannot find the image", Toast.LENGTH_SHORT).show();
                return;
            }
            // Process image request for the background.
            if (requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {
                // Get the image's URI and use the location for background image.
                Uri imageFileUri = data.getData();
                @SuppressLint("Recycle")
                Cursor cursor = getContentResolver().query(Uri.parse(imageFileUri != null ? imageFileUri.toString() : null), null, null, null, null);
                if (cursor != null) {
                    cursor.moveToNext();
                }
                String imageRealPath = getRealPathFromURI(imageFileUri);
                mPenPageDoc.setBackgroundImage(imageRealPath);
                mPenPageDoc.setBackgroundImageMode(SpenPageDoc.BACKGROUND_IMAGE_MODE_FIT);
                mPenSurfaceView.update();
            }
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                Uri imageFileUri = data.getData();
                String imageRealPath = getRealPathFromURI(imageFileUri);
                SpenObjectImage imgObj = new SpenObjectImage();
                imgObj.setImage(imageRealPath);
                RectF rect1 = new RectF(mPenPageDoc.getWidth() / 4, mPenPageDoc.getWidth() / 4, mPenPageDoc.getWidth() / 2, mPenPageDoc.getHeight() / 2);
                imgObj.setRect(rect1, true);
                mPenPageDoc.appendObject(imgObj);
                mPenPageDoc.selectObject(imgObj);
                mPenSurfaceView.update();
            }

            if (requestCode == REQUEST_CODE_STREAM) {
                hideLoading();
                if (!AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.RTMP_FACEBOOK).isEmpty() || !AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.RTMP_GOOGLE).isEmpty()) {
                    SlowInitVideoTask slowInitVideoTask = new SlowInitVideoTask(resultCode, data);
                    slowInitVideoTask.execute();
                } else {
                    SlowInitRecordVideoTask slowInitRecordVideoTask = new SlowInitRecordVideoTask(resultCode, data);
                    slowInitRecordVideoTask.execute();
                }
            }

            if (requestCode == REQUEST_CODE_RECORD) {
                hideLoading();
                SlowInitRecordVideoTask slowInitRecordVideoTask = new SlowInitRecordVideoTask(resultCode, data);
                slowInitRecordVideoTask.execute();
            }
        }
    }

    private class SlowInitVideoTask extends AsyncTask<String, Void, Void> {

        private int resultCodeTask;
        private Intent dataTask;

        SlowInitVideoTask(int resultCode, Intent data) {
            resultCodeTask = resultCode;
            dataTask = data;
        }

        @Override
        protected void onPreExecute() {
            isSaveRecord = false;
            onTimeRecord = System.currentTimeMillis();
//            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(String... strings) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
//            DisplayMetrics metrics = new DisplayMetrics();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScreenDensity = (int) (metrics.density * 160f);
            if (AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.LOGIN_FROM_FACEBOOK)) {
                // TODO RTMP FACEBOOK
                if (rtmpDisplay.prepareAudio() && rtmpDisplay.prepareVideo(DISPLAY_WIDTH, DISPLAY_HEIGHT, frameRate, bitRate, orientation, mScreenDensity)) {
                    rtmpDisplay.setIntentResult(resultCodeTask, dataTask);
                    rtmpDisplay.startStream(AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.RTMP_FACEBOOK));
                }
            } else {
                // TODO RTMP YOUTUBE
                if (rtmpDisplay.prepareAudio() && rtmpDisplay.prepareVideo(DISPLAY_WIDTH, DISPLAY_HEIGHT, frameRate, bitRate, orientation, mScreenDensity)) {
                    rtmpDisplay.setIntentResult(resultCodeTask, dataTask);
                    rtmpDisplay.startStream(AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.RTMP_GOOGLE));

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            hideLoading();
            if(checkSessionRecord){
                ToastUtil.getInstance().show(getString(R.string.teacher_save_resume));
            }else{
                ToastUtil.getInstance().show(getString(R.string.teacher_record_start));
            }
            startCountUpTimer();
        }
    }

    private class SlowInitRecordVideoTask extends AsyncTask<String, Void, Void> {

        private int resultCodeTask;
        private Intent dataTask;

        SlowInitRecordVideoTask(int resultCode, Intent data) {
            resultCodeTask = resultCode;
            dataTask = data;
        }

        @Override
        protected void onPreExecute() {
            isSaveRecord = false;
            onTimeRecord = System.currentTimeMillis();
//            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(String... strings) {

            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            //            DisplayMetrics metrics = new DisplayMetrics();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScreenDensity = (int) (metrics.density * 160f);
            if (rtmpDisplay.prepareAudio() && rtmpDisplay.prepareVideo(DISPLAY_WIDTH, DISPLAY_HEIGHT, frameRate, bitRate, orientation, mScreenDensity)) {
                rtmpDisplay.setIntentResult(resultCodeTask, dataTask);
                try {
                    rtmpDisplay.startRecord(pathVideo);
                } catch (IOException e) {
                    rtmpDisplay.stopRecord();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            hideLoading();
            ibRecord.setImageResource(R.drawable.ic_stop);
            if(checkSessionRecord){
                ToastUtil.getInstance().show(getString(R.string.teacher_save_resume));
            }else{
                ToastUtil.getInstance().show(getString(R.string.teacher_record_start));
            }
            startCountUpTimer();
        }
    }

    private class EndEventTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (params.length >= 1) {
                    YouTubeApi.endEvent(YouTubeNewSingleton.newInstance(mAccount, TeacherActivity.this).getYoutube(), params[0]);
                }
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                Log.w(TAG, "getSubscription:recoverable exception", userRecoverableException);
                startActivityForResult(userRecoverableException.getIntent(), Constants.RequestCode.REQUEST_RECOVERY_ACCOUNT);
            } catch (IOException e) {
                Log.w(TAG, "getSubscription:exception", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            hideLoading();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //   prevent memory leaks when you application closes.

        if (mPenSettingView != null) {
            mPenSettingView.close();
        }

        if (mStrokeHandler != null) {
            mStrokeHandler.removeCallbacks(mStrokeRunnable);
            mStrokeHandler = null;
        }

        if (mEraserSettingView != null) {
            mEraserSettingView.close();
        }

        if (mSpenObjectRuntimeManager != null) {
            if (mVideoRuntime != null) {
                mVideoRuntime.stop(true);
                mSpenObjectRuntimeManager.unload(mVideoRuntime);
            }
            mSpenObjectRuntimeManager.close();
        }

        if (mPenPageDoc.isRecording()) {
            mPenPageDoc.stopRecord();
        }

        if (mPenSurfaceView.getReplayState() == SpenSurfaceView.REPLAY_STATE_PLAYING) {
            mPenSurfaceView.stopReplay();
        }

        if (mTextSettingView != null) {
            mTextSettingView.close();
        }
        //  Close the text control
        mPenSurfaceView.closeControl();


        if (mPenSurfaceView != null) {
            mPenSurfaceView.close();
            mPenSurfaceView = null;
        }
        if (mPenNoteDoc != null) {
            try {
                mPenNoteDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPenNoteDoc = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()) {
            DeviceUtil.getInstance().transformImage(TeacherActivity.this, textureView, textureView.getWidth(), textureView.getHeight());
            openCamera(CAMERA_FONT);
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - onTimeRecord < MIN_TIME_RECORD) {
            showCautionDialog(getResources().getString(R.string.teacher_min_time_record_error), "", liveDialog -> {
                liveDialog.dismiss();
            });
        } else {
            if (isSaveRecord) {
                super.onBackPressed();
            } else {
                showCautionDialog(getResources().getString(R.string.teacher_no_save_error), "", liveDialog -> {
                    liveDialog.dismiss();
                });
            }
        }
    }

    private void clearRecord() {
        recordStatus = 0;
        checkSessionRecord = false;
        listRecordsName.clear();
        listRecordsPath.clear();
        showListVideo();
    }
}
