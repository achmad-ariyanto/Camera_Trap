package ta171801001.camera_trap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class showDetail extends AppCompatActivity {

    Context ctx;
    List imagePath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        ctx = this;

        Intent in = getIntent();
        int i = in.getIntExtra("ta171801001.camera_trap.Viewer.Position", -1);

        if (i > -1) {
            String path = getImg(i);
            ImageView v = findViewById(R.id.detailView);
            scaleImg(v,path);
        }
    }

    private String getImg(int idx){
        File dir = new File(String.valueOf(ctx.getExternalFilesDir(null)));
        File[] imgs = dir.listFiles();
        if (imgs.length != 0){
            for (int i = 0; i<imgs.length; i++) {
                if(imgs[i].isFile()){
                    imagePath.add(imgs[i].getPath());
                    Log.d("path2", String.valueOf(imagePath.get(i)));
                }
            }
        }

        return String.valueOf(imagePath.get(idx));
    }

    private void scaleImg(ImageView img, String path){
        Display screen = getWindowManager().getDefaultDisplay();
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, opt);

        int imgWidth = opt.outWidth;
        int screenWidth = screen.getWidth();

        //if (imgWidth > screenWidth){
        int r = Math.round((float) imgWidth/(float) screenWidth);
        opt.inSampleSize = r;
        //}
        opt.inJustDecodeBounds = false;

        Bitmap scaledImg = BitmapFactory.decodeFile(path,opt);
        img.setImageBitmap(scaledImg);
    }
}
