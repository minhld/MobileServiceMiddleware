package com.usu.mobileservice.annotationchecker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    String LOG = "MainActivity-Test";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Button exeClassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exeClassBtn = (Button) findViewById(R.id.exeClassBtn);
        exeClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                compile();
            }
        });

        grandWritePermission(this);
    }

    public static void grandWritePermission(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
    }

//    void compile() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("public class HelloWorld implements com.usu.servicemiddleware.processors.DoStuff {\n");
//        sb.append("    @Override \n");
//        sb.append("    public void doStuff() {\n");
//        sb.append("        System.out.println(\"Hello world!!!\");\n");
//        sb.append("    }\n");
//        sb.append("}\n");
//
//        try {
//            String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//            String javaFile = downloadPath + "/HelloWorld.java";
//            CompileTest.compile(javaFile, "HelloWorld", sb.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
