package com.example.brady.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rekognition.adapter.FaceAdapter;
import com.rekognition.adapter.JsonResponseAdapter;
import com.rekognition.adapter.model.Face;
import com.rekognition.adapter.model.FieldNotFoundException;
import com.rekognition.api.impl.FaceCrawl;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.rekognition.api.impl.FaceRecognize;
import com.rekognition.http.model.RekognitionAPIException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cat.lafosca.facecropper.FaceCropper;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener,Camera.ShutterCallback,Camera.PictureCallback,SurfaceHolder.Callback{


    private static final String API_KEY = "cwgLS8bE16Rv6on0";
    private static final String API_SECRET = "rPtvUe3aSd1eEq2G";
    private static final String NAME_SPACE = "demo_project";
    private static final String USER_ID = "demo_user";

    /** {@link CardScrollView} to use as the main content view. */
    private CardScrollView mCardScroller;
    private SurfaceView mSurfaceView;
    private Camera cam;

    /**
     * {@link View} generated by {@link #buildView()}.
     */
    private View mView;

    private TextToSpeech mTts;
    private int  MY_DATA_CHECK_CODE = 123;

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //mView = buildView();

        /* All the camera interaction is here */
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 600000);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.surface);
        mSurfaceView = (SurfaceView) findViewById(R.id.surf);

        cam = Camera.open();
        snapPicture();

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);


//        ghettoTraining();

    }

    private void ghettoTraining() {
        // adding Facebook pictures
        List<Long> friendIDs = new ArrayList<Long>();
        friendIDs.add(100000072435606L);
        friendIDs.add(100000153917216L);
        friendIDs.add(100000313882986L);
        friendIDs.add(1456846730L);
        long myID = 100008949753855L;
        String myAccessToken = "CAATzGYu82JYBACviil31ZBoGCZCEcO3Fx3xVvXVazNn7AXFyPTsBTWvmFeF1Pjzy1IkI8ZCImbZAgO4b9kPEs5mAxQoEkBViEm5lr4wZC6MV91aXncMIKz9nRlNymVoTvm76HW2pBBsaDZAZA7RgXQaJmTnL11LOZCPoiu6gkQPbBcJvxExwls6P8r1dxxepqYnZB4jtd2X2pKrG52Aic0Qksq0l4axU2y94ZD";
//        RekoSDK.face_crawl(friendIDs, myID, myAccessToken, new RekoSDK.APICallback() {
//            @Override
//            public void gotResponse(String sResponse) {
//                Log.e(TAG, "face_crawl response: " + sResponse);
//            }
//        });

        FaceCrawl fc = new FaceCrawl(API_KEY, API_SECRET);
        try {
            String response = fc.getResponse(friendIDs, myID, myAccessToken, "PennApps", "PennApps").getJsonObject().toString();
            Log.e(TAG, response);
        } catch (RekognitionAPIException e) {
            e.printStackTrace();
        }

        // training on the Facebook pictures
//        RekoSDK.face_train("PennApps", myID, new RekoSDK.APICallback() {
//            @Override
//            public void gotResponse(String sResponse) {
//                Log.e(TAG, "Trevin: " + sResponse);
//            }
//        });
//        RekoSDK.face_train("PennApps", myID, new RekoSDK.APICallback() {
//            @Override
//            public void gotResponse(String sResponse) {
//                Log.e(TAG, "Brady: " + sResponse);
//            }
//        });
//        RekoSDK.face_train("PennApps", myID, new RekoSDK.APICallback() {
//            @Override
//            public void gotResponse(String sResponse) {
//                Log.e(TAG, "Rajat: " + sResponse);
//            }
//        });
//        RekoSDK.face_train("PennApps", myID, new RekoSDK.APICallback() {
//            @Override
//            public void gotResponse(String sResponse) {
//                Log.e(TAG, "Colin: " + sResponse);
//            }
//        });

    }

    @Override
    public void onShutter() {
        /* This is where we put shutter sound */
        return;
    }

    /**
     *  Callback interface used to supply image data from a photo capture.
     */
    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        FileOutputStream fos= null;

        /* Write byte array to jpeg file */
        try {
            File photo=new File(this.getCacheDir(), "3eyeout.jpg");
            if (photo.exists()) {
                photo.delete();
            }
            fos = new FileOutputStream(photo.getPath());
            fos.write(bytes);
            fos.close();
            processPhoto(bytes);

//            PhotoAsyncTask task = new PhotoAsyncTask();
//            task.execute(photo.getPath());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return;
    }

    private void processPhoto(byte[] photo) {
        recognize(cropAndScale(photo));
    }

    // crops image to face and scales it to 800 x 800
    private Bitmap cropAndScale(byte[] photo) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        FaceCropper mFaceCropper = new FaceCropper();
        mFaceCropper.setMaxFaces(1);
        Bitmap cropped = mFaceCropper.getCroppedImage(bitmap);
        Bitmap scaled = Bitmap.createScaledBitmap(cropped, 800, 800, false);

        ImageView iv = new ImageView(this);
        iv.setImageBitmap(scaled);
        ((FrameLayout)findViewById(R.id.layout)).addView(iv);

        return scaled;
    }

    private void recognize(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] photo = stream.toByteArray();

        // first API face_recognize failure

//        RekoSDK.face_recognize(photo, new RekoSDK.APICallback() {
//            @Override
//            public void gotResponse(String sResponse) {
//                try {
//                    Log.e(TAG, "entered response callback");
//                    JSONObject response = new JSONObject(sResponse);
//                    if (response == null) {
//                        Log.e(TAG, "response was null");
//                    } else {
//                        String parsed = parseResponse(response.getString("name"));
//                        Log.e(TAG, parsed);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });

        // first API face_search failure
//        String[] tags = {"Rajat_Bhageria"};
//        RekoSDK.face_search(tags, photo, NAME_SPACE, USER_ID, 1, new RekoSDK.APICallback() {
//            @Override
//            public void gotResponse(String sResponse) {
//                try {
//                    JSONObject jObject = new JSONObject(sResponse);
//                    if (jObject == null) {
//                        Log.e(TAG, "face_search JSONObject was null :)");
//                    } else {
//                        Log.e(TAG, "about to check JSONArray for matches :)");
//                        JSONArray jArray = jObject.getJSONArray("matches");
//                        if (jArray == null) {
//                            Log.e(TAG, "JSONArray was null :)");
//                        }
//                        JSONObject firstmatch = jArray.getJSONObject(0);
//                        if (firstmatch == null) {
//                            Log.e(TAG, "first match was null :)");
//                        }
//                        String parsed = parseResponse(firstmatch.getString("tag"));
//                        Log.e(TAG, "face_search response: " + parsed);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        // second API face_recognize
//        List<String> tags = new ArrayList<String>();
//        tags.add("Rajat_Bhageria");
//        FaceRecognize fr = new FaceRecognize(API_KEY, API_SECRET);
//        if (fr == null) {
//            Log.e(TAG, "FaceRecognize is null :(");
//        }
//        try {
//            FaceAdapter fa = fr.recognizeFaceWithoutDetect(photo, NAME_SPACE, USER_ID, 1, tags);
//            // JSONObject fa_rootjson = fa.getJsonObject();
//            Log.e(TAG, "got a FaceAdapter :)");
//            List<Face> faces = fa.getFaces();
//            Log.e(TAG, "got faces :)");
//            Face firstFace = faces.get(0);
//            Log.e(TAG, "got first face :)");
//            try {
//                List<Face.Match> matches = firstFace.getMatches();
//                Log.e(TAG, "got matches :)");
//                if (matches == null) {
//                    Log.e(TAG, "matches are null :(");
//                } else {
//                    String tag = matches.get(0).getTag();
//                    Log.e(TAG, "tag received: " + tag);
//                }
//            } catch (FieldNotFoundException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        } catch (RekognitionAPIException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private String parseResponse(String response) {
        return response.substring(response.indexOf(":")).replaceAll("_", " ").toUpperCase();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    /**
     * Starts preview and takes picture.
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if(surfaceHolder.getSurface() == null || cam == null) {
            return;
        }

        //stop preview before make changes
        cam.stopPreview();

        //start preview with new setting
        try {
            cam.setPreviewDisplay(surfaceHolder);
            cam.startPreview();
            cam.takePicture(this,null,null,this);
        } catch (IOException e) {
            Log.e("MainActivity","problem setting up camera",e);
        }
    }

    /**
     * Stops preview and releases camera
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceHolder.removeCallback(this);
        cam.lock();
        cam.stopPreview();
        cam.release();
        surfaceHolder = null;
    }

    private class PhotoAsyncTask extends AsyncTask<String, Void, JSONObject>{

        /* Makes Post request */
        @Override
        protected JSONObject doInBackground(String... strings) {
            return null;
        }

//        /* Parses JSON, reads result to user */
        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
        }
    }

    /* Camera stuff */
    protected void snapPicture() {
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(this);
    }

    /**
     *  Runs after taking picture. Sets loading bar and reads "loading" out loud.
     */
    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.progressContainer).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textview)).setText(getString(R.string.loading));

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Builds a view using the {@link CardBuilder} class.
     */
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText(R.string.hello_world);
        return card.getView();
    }

    @Override
    public void onInit(int i) {
        Log.i("MainActivity", "Works: " + i);
        mTts.setLanguage(Locale.US);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if ( mTts != null)
        {
            mTts.stop();
            mTts.shutdown();
        }
    }
}