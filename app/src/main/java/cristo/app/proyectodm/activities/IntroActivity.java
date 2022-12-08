package cristo.app.proyectodm.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cristo.app.proyectodm.R;

//Actividad que muestra un splashActivity de 1 segundo de duracion
public class IntroActivity extends AppCompatActivity {
    TextView tv_app_name_intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        tv_app_name_intro = findViewById(R.id.tv_app_name_intro);

        //Se pone un tipo de letra descargado
        Typeface tipoLetra = Typeface.createFromAsset(getAssets(), "CMONNEAR.TTF");
        tv_app_name_intro.setTypeface(tipoLetra);

        //Se ajusta la duracion del splashScreen
        Thread hilo = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        hilo.start();
    }
}