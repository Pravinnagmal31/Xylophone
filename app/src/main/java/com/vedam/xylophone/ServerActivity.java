package com.vedam.xylophone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ServerActivity extends AppCompatActivity {

    EditText etHost;
    Button btHost;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);

        etHost = findViewById(R.id.etHost);
        btHost = findViewById(R.id.btHost);
        sharedpreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);

        btHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etHost.getText().toString().trim().isEmpty()){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("host1", etHost.getText().toString().trim());
                    editor.commit();
                    etHost.setText("");
                }
            }
        });
    }
}
