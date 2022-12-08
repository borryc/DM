package cristo.app.proyectodm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.core.SettingsManager;

//Actividad que muestra las opciones de iniciar sesion, registrarse y cambiar de idioma
public class MainActivity extends AppCompatActivity {
    private Button btn_inicio_sesion, btn_registrarse;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.tb_main));

        mAuth=FirebaseAuth.getInstance();

        btn_inicio_sesion = findViewById(R.id.btn_inicio_sesion);
        btn_inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btn_registrarse = findViewById(R.id.btn_registrarse);
        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrarActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.menu_idiomas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean toret = false;
        String language = "";
        SettingsManager settsMan;
        settsMan = new SettingsManager(getResources());
        switch( menuItem.getItemId() ) {
            case R.id.idm_english:
                language = "en";
                toret = true;
                break;
            case R.id.idm_espanol:
                language = "es";
                toret = true;
                break;
            case R.id.idm_galego:
                language = "gl";
                toret = true;
                break;
        }
        settsMan.setLanguage(language);
        settsMan.applySettings();
        return toret;
    }

    //Para mantener sesión
   protected void onStart(){
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, InicioActivity.class));
            finish();
        }
    }
}