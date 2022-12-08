package cristo.app.proyectodm.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.core.SettingsManager;

public abstract class ToolActivity extends AppCompatActivity {
    ImageView inicio;
    ImageView perfil;
    ImageView ajustes;
    ImageView amigos;
    ImageView historial;
    ImageView ranking;
    ImageView consejos;

    protected void tool() {
        inicio= findViewById(R.id.iv_inicio_tb);
        perfil = findViewById(R.id.iv_perfil_tb);
        ajustes = findViewById(R.id.iv_ajustes_tb);
        amigos = findViewById(R.id.iv_amigos_tb);
        historial = findViewById(R.id.iv_historial_tb);
        ranking = findViewById(R.id.iv_ranking_tb);
        consejos = findViewById(R.id.iv_consejos_tb);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolActivity.this, InicioActivity.class));
                finish();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolActivity.this, PerfilActivity.class));
                finish();
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolActivity.this, AjustesActivity.class));
            }
        });

        amigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolActivity.this, AmigosActivity.class));
                finish();
            }
        });

        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolActivity.this, HistorialActivity.class));
                finish();
            }
        });

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolActivity.this, RankingActivity.class));
                finish();
            }
        });

        consejos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolActivity.this, ConsejosActivity.class));
                finish();
            }
        });
    }
    public SettingsManager settsMan;
    @Override
    protected void onResume() {
        super.onResume();
        //Ajustes cargar
        loadSettings();
        //Ajustes cargar
    }
    public void loadSettings(){
        boolean isMetricSys;
        String language;
        SharedPreferences preferences = getSharedPreferences(AjustesActivity.getSaveName(), Context.MODE_PRIVATE);
        isMetricSys = preferences.getBoolean(AjustesActivity.METRICSYS_CONFIG, AjustesActivity.METRIC_SYS_DEFAULT);
        language = preferences.getString(AjustesActivity.LANGUAGE_CODE, AjustesActivity.LANGUAGE_DEFAULT);
        settsMan = new SettingsManager(getResources());
        settsMan.setMetricSys(isMetricSys);
        settsMan.setLanguage(language);

        settsMan.applySettings();
    }

    public String getRString(int id){
        return getResources().getString(id);
    }
}
