package com.mobile.bolly.phone.update;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.mobile.bolly.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateFragment extends Fragment implements View.OnClickListener {

    View v;
    MaterialButton btnDownload;
    ImageButton btnBack;
    ImageView ivLoading;
    String downloadUrl, versionName = "";
    TextView tvProgress, tvVersionName;
    ScrollView svInstructions;
    DownloadFileFromURL downloadFileFromURL = null;


    @Override
    public void onStop() {
        super.onStop();
        if(downloadFileFromURL != null && !downloadFileFromURL.isCancelled()){
            downloadFileFromURL.cancel(true);
            downloadFileFromURL = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_update, container, false);

        Bundle bundle = getArguments();
        if(!bundle.isEmpty()){
            downloadUrl = bundle.getString("downloadUrl");
            versionName = bundle.getString("version");

        }

        svInstructions = v.findViewById(R.id.sv_instructions);
        btnDownload = v.findViewById(R.id.btn_download_update);
        btnDownload.setOnClickListener(this);
        btnBack = v.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        ivLoading = v.findViewById(R.id.iv_loading);
        tvProgress = v.findViewById(R.id.tv_progress);
        tvVersionName = v.findViewById(R.id.tv_version_name);
        tvVersionName.setText("Follow the below instrutions after download is complete to update your app to v" + versionName);


        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_back:
                getFragmentManager().popBackStack();
                break;

            case R.id.btn_download_update:
                svInstructions.setVisibility(View.GONE);
                ivLoading.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.INVISIBLE);
                downloadFileFromURL = new DownloadFileFromURL();
                downloadFileFromURL.execute(downloadUrl);
                break;

        }


    }





    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Glide.with(getContext()).asGif().load(R.drawable.load).into(ivLoading);
            tvProgress.setVisibility(View.VISIBLE);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();
                Log.d("FILE", "file length : "+ lenghtOfFile);

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/bolly.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/bolly.apk";
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            tvProgress.setText("Downloading... " + progress[0] + "%");
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            Glide.with(getContext()).clear(ivLoading);
            Uri apkUri = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                apkUri = FileProvider.getUriForFile(getActivity(), getContext().getOpPackageName() + ".provider", new File(file_url));
            else
                apkUri = Uri.fromFile(new File(file_url));
            installAPK(apkUri);
        }

    }



    private void installAPK(Uri apkFile) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(apkFile, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
