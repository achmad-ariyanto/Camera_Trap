package ta171801001.camera_trap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

public class LogIn extends AppCompatActivity implements Serializable{

    private EditText UsrInTxt, PwdInTxt;
    private Button LogInBtn;
    public Thread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        final Calendar d = Calendar.getInstance();






        LogInBtn = (Button) findViewById(R.id.LogInBtn);
        UsrInTxt = (EditText) findViewById(R.id.UsrInTxt);
        PwdInTxt = (EditText) findViewById(R.id.PwdInTxt);


        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat fd = new SimpleDateFormat("MMM dd, yyyy");
                fd.setTimeZone(d.getTimeZone());
                final String formatted_date = fd.format(d.getTime());
                final String search = String.format("%02d", (d.get(Calendar.DAY_OF_MONTH)))+String.format("%02d", (d.get(Calendar.MONTH)+1))+Integer.toString(d.get(Calendar.YEAR));
                /*
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                */
                TextView TestTxt = (TextView) findViewById(R.id.TestTxt);

                // store username and password value
                String usr = UsrInTxt.getText().toString();
                String pwd = PwdInTxt.getText().toString();

                PwdInTxt.setText("");

                // verification
                if ((usr.equals(getString(R.string.user))) && (pwd.equals(getString(R.string.pwd)) )){
                    Intent LoggedIn = new Intent(getApplicationContext(),Viewer.class);
                    LoggedIn.putExtra("ta171801001.camera_trap.LogIn.user",UsrInTxt.getText().toString());
                    LoggedIn.putExtra("ta171801001.camera_trap.LogIn.date",formatted_date);
                    LoggedIn.putExtra("ta171801001.camera_trap.LogIn.search",search);
                    UsrInTxt.setText("");
                    startActivity(LoggedIn);

                }
                else {
                    Toast.makeText(getBaseContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    TestTxt.setText(search);
                }
            }
        });
    }
}
