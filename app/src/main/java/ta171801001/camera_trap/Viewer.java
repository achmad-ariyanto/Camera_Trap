package ta171801001.camera_trap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Viewer extends AppCompatActivity {

    private MyFTPClientFunction ftpclient = null;
    private static final String TAG = "Status";
    private Context ctx;
    public List FTPImagepath = new ArrayList<String>();
    public List imagePath = new ArrayList<String>();
    public List MatchImagePath = new ArrayList<String>();

    private boolean status = false;
    private boolean downloadStatus = false;

    private TextView dateTxt, WelcomeTxt;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Button changeDateBtn;
    public GridView img_view;

    public Thread connect;
    public Thread refresh;

    public Thread display;

    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        ftpclient = new MyFTPClientFunction();
        ctx = this;

        final File dir = new File(String.valueOf(ctx.getExternalFilesDir(null)));
        connect = new Thread((new Runnable() {
            public void run() {
                synchronized (this) {
                    status = ftpclient.ftpConnect(getString(R.string.FTP_Host), getString(R.string.FTP_User), getString(R.string.FTP_Pwd), 21);
                    System.out.println(status);
                    //status = ftpclient.ftpConnect(getString(R.string.SFTP_Host), getString(R.string.SFTP_User), getString(R.string.SFTP_Pwd), 22);
                    notifyAll();
                }
            }
        }));
        //connect.start();

        display = new Thread(new Runnable() {
            @Override
            public void run() {
                if ((imagePath.size() != 0)) {
                    /*
                    for (int i = 0; i < imagePath.size(); i++){
                        if(String.valueOf(imagePath.get(i)).contains(search)){
                            MatchImagePath.add(String.valueOf(imagePath.get(i)));
                            Log.d("Match", String.valueOf(imagePath.get(i)));
                        }
                    }
                    */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img_view.setAdapter(new imgAdapter(ctx, imagePath));
                        }
                    });
                }
            }
        });

        refresh = new Thread(new Runnable() {
            public void run() {
                synchronized (connect){
                    connect.start();
                    while(!status){
                        try{
                            System.out.println("Connecting...");
                            connect.wait();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    if (status) {
                        if (imagePath.size() != 0) {
                            for (int i = 0; i < imagePath.size(); i++) {
                                File dest = new File((String) imagePath.get(i));
                                if (dest.exists()) {
                                    dest.delete();
                                    Log.d("Status", "Delete Success");
                                }
                            }
                            imagePath.clear();
                        }
                        Log.d(TAG, "Connection Success");
                        String workdir;
                        workdir = ftpclient.ftpGetCurrentWorkingDirectory();
                        ftpclient.ftpChangeDirectory("TA_Kamera_Trap/Image");

                        workdir = ftpclient.ftpGetCurrentWorkingDirectory();
                        Log.d("Dir", workdir);
                        ftpclient.ftpPrintFilesList(workdir, FTPImagepath, ctx);
                        for (int i = 0 ; i < FTPImagepath.size(); i++){
                            boolean stat = ftpclient.ftpDownload(String.valueOf(FTPImagepath.get(i)),ctx, search);
                            if (i == FTPImagepath.size()){
                                downloadStatus = stat;
                            }
                        }
                        File[] imgs = dir.listFiles();
                        if (imgs.length != 0){
                            for (int i = 0; i<imgs.length; i++) {
                                if(imgs[i].isFile()){
                                    imagePath.add(imgs[i].getAbsolutePath());
                                    Log.d("path", String.valueOf(imagePath.get(i)));
                                }
                            }
                        }
                        display.start();
                    } else {
                        Log.d(TAG, "Connection failed");
                    }
                }
            }
        });



        refresh.start();

        WelcomeTxt = (TextView) findViewById(R.id.WelcomeTxt);
        dateTxt = (TextView) findViewById(R.id.dateTxt);
        changeDateBtn = (Button) findViewById(R.id.changeDateBtn);
        img_view = findViewById(R.id.grid_view);

        if (getIntent().hasExtra("ta171801001.camera_trap.LogIn.user")) {
            String text = "welcome, " + getIntent().getExtras().getString("ta171801001.camera_trap.LogIn.user");
            WelcomeTxt.setText(text);
        }

        if (getIntent().hasExtra("ta171801001.camera_trap.LogIn.date")) {
            String text = "Selected Date: " + getIntent().getExtras().getString("ta171801001.camera_trap.LogIn.date");
            dateTxt.setText(text);
        }

        if (getIntent().hasExtra("ta171801001.camera_trap.LogIn.search")) {
            search = getIntent().getExtras().getString("ta171801001.camera_trap.LogIn.search");
            Log.d("search", search);
        }

        //while (!downloadStatus) {
          //  Toast.makeText(getBaseContext(), "Downloading Data... Please wait", Toast.LENGTH_SHORT).show();
        //}



        img_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext(), showDetail.class);
                in.putExtra("ta171801001.camera_trap.Viewer.Position", position);
                startActivity(in);

            }
        });

        changeDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                DatePickerDialog datePicker = new DatePickerDialog(
                        Viewer.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day
                        );
                datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePicker.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String text = DateFormat.getDateInstance().format(c.getTime());
                search = String.format("%02d", (c.get(Calendar.DAY_OF_MONTH)))+String.format("%02d", (c.get(Calendar.MONTH)+1))+Integer.toString(c.get(Calendar.YEAR));
                dateTxt.setText("Selected Date : " + text);
                Log.d("search2", search);
                refresh.start();
            }
        };

    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (imagePath.size() != 0) {
                            for (int i = 0; i < imagePath.size(); i++) {
                                File dest = new File((String) imagePath.get(i));
                                if (dest.exists()) {
                                    dest.delete();
                                    Log.d("Status", "Delete Success");
                                }
                            }
                        }
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}


