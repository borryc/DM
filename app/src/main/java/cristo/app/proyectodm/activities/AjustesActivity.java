package cristo.app.proyectodm.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import cristo.app.proyectodm.R;

//Actividad para mostrar los ajustes de la aplicacion
public class AjustesActivity extends AppCompatActivity {
    String CONFIG_SAVER;
    static final String METRICSYS_CONFIG = "unitsystem";
    static final String LANGUAGE_CODE = "language_code";
    static final boolean METRIC_SYS_DEFAULT = true;
    static final String LANGUAGE_DEFAULT = "en";
    private String[] languagues_code;

    SharedPreferences preferences;

    Toolbar tb_ajustes;
    private boolean isMetricSys;
    private String language;

    //Para guardar el nombre de la configuracion de cada usuario en SharedPreferences
    public static String getSaveName(){
        return "WaR_Config" +  FirebaseAuth.getInstance().getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        setUpActionBar();
        CONFIG_SAVER = getSaveName();
        preferences = getSharedPreferences(CONFIG_SAVER, Context.MODE_PRIVATE);
        doLoadDataFromSharedPreferences();
        languagues_code = new String[]{"es", "en", "gl"};

        Switch unidades = findViewById(R.id.sw_ajustes_unidades);
        Spinner idiomas = findViewById(R.id.sp_ajustes_idioma);

        unidades.setChecked(isMetricSys);
        idiomas.setSelection(getLanguageNumInSpinner(language));

        SharedPreferences.Editor editor = preferences.edit();

        //Para seleccionar la unidad metrica deseada
        unidades.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(METRICSYS_CONFIG,b);
                editor.apply();
            }
        });

        //Para seleccionar el idioma de la app
        idiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String langCode = languagues_code[i];
                editor.putString(LANGUAGE_CODE, langCode);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Metodo que devuelve la posicion de cada idioma en el spinner
    private int getLanguageNumInSpinner(String langCode) {
        int code;
        switch (langCode) {
            case "es":
                code = 0;
                break;
            case "en":
                code = 1;
                break;
            case "gl":
                code = 2;
                break;
            default:
                code = 0;
                break;
        }
        return code;
    }

    //Metodo para cargar los datos de cada usuario
    private void doLoadDataFromSharedPreferences() {
        isMetricSys = preferences.getBoolean(METRICSYS_CONFIG, METRIC_SYS_DEFAULT);
        language = preferences.getString(LANGUAGE_CODE, LANGUAGE_DEFAULT);
    }

    //Metodo para volver atras
    private void setUpActionBar() {
        tb_ajustes = findViewById(R.id.tb_ajustes);
        setSupportActionBar(tb_ajustes);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }
}