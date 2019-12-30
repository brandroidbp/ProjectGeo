package com.example.proyectodm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EntrarActivity extends AppCompatActivity {

    Button btnentrar;
    TextView txtnombre;
    public static String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar);

        txtnombre = (TextView) findViewById(R.id.txtnombre);
        btnentrar = (Button) findViewById(R.id.btnentrar);

        btnentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 nombre = txtnombre.getText().toString();
                if(!nombre.equals("")){
                    Intent intent = new Intent(EntrarActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}
