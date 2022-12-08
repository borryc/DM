package cristo.app.proyectodm.activities;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.core.PasosFecha;

public class InicioActivity extends ToolActivity implements SensorEventListener {
    private TextView tv_pasos;
    private TextView tv_distancia;
    private CardView cv_pasos_counter;


    private int pasosTotales;
    private ArrayList<PasosFecha> HistorialDePasos;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private SensorManager sensorManager;
    public final static String PASOSDIARIOSGSON = "HistorialpasosDiarios";
    private final static String PASOSTOTALESINICIO = "pasosTotalesIncioDia";
    private final static String PASOSFECHAHOY = "pasosfechahoy";
    private int maxTam; //tam maximo del Historial
    private PasosFecha pasosDeHoy;
    private int pasosTotalesInicioDia;
    private long delayTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        tool();

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Preguntar por el permiso
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) { //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        tv_pasos = findViewById(R.id.tv_pasos_totales);
        cv_pasos_counter = findViewById(R.id.cv_pasos);
        tv_distancia = findViewById(R.id.tv_distancia_total);

        HistorialDePasos = new ArrayList<>();
        maxTam = 3;// Tam reducido para probar funcionalidades. En la de verdad sería más grande
        delayTask = 5000;//en ms

        //Si no hay sensor(no hay ni momiviento ni pasos) se configura un clicker (para probar en el emulador que cuenta los pasos)
        if (sensorManager == null) {
            cv_pasos_counter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    darPaso();
                }
            });
        } else {
            Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (accel != null) {
                delayTask = 15000;//en ms
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                cv_pasos_counter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        darPaso();
                    }
                });
            }
        }
    }
    /*Calcula el tiempo en ms que queda para medianoche. Coge el tiempo actual y el "mañana" de la variable pasosDeHoy.
     * En pasosDeHoy hay una fecha de cuando se abrió ese día la app
     * Si, el tiempo actual ha superado a ese mañana se hace que la tarea se ejecute ya (delay = 0)
     * Si no, el delay es lo que quede hasta las 00:00*/
    private void setTimeToMidnight() {
        Calendar now = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(pasosDeHoy.getFechaDate());
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        if (now.after(tomorrow)) {
            delayTask = 0;
        }
        {
            delayTask = (tomorrow.getTimeInMillis() - now.getTimeInMillis());
        }
    }
    /*Se leen los pasosTotales de firebase. Se cargan los datos que serían de hoy
     * Se carga el historial de sharedPreferences para poder guardar los pasos
     * setTimeToMidnight comentado porque para pruebas es muy lento
     * se manda activar la tarea*/
    @Override
    protected void onResume() {
        super.onResume();
        leerPasosTotales();
        cargarDatosDeHoy();
        doLoadHistorialFromSharedPreferences();
        //setTimeToMidnight();
        setTimerTask();
    }
    /*Aqui se guardan los pasosTotales, los datos de hoy y el historial*/
    @Override
    protected void onPause() {
        super.onPause();
        guardarPasosTotales();
        guardarDatosDeHoy();
        guardarHistorialSharedPreferences();
    }
    /*El sensor ha detectado algo y si es un paso llama a dar paso*/
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            darPaso();
        }
    }

    //Dejar aunque este vacio. Es del sensor y tiene que estar pero no se usa
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    /*Se llama a la tarea. Se le pasa el método que tiene que ejecutar que es contarPasoDiario
    y un delay, que es cuando se ejecuta*/
    private void setTimerTask() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        contarPasoDiario();
                    }
                },
                delayTask
        );//delayTask en milisegs
    }
    /*Guardar pasos totales en firebase.*/
    private void guardarPasosTotales() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> guardarPerfil = new HashMap<>();

                    guardarPerfil.put(PerfilActivity.PASOSTOTALESDB, pasosTotales);
                    mRef.updateChildren(guardarPerfil);
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InicioActivity.this, R.string.InicioActivity_error_pasos_totales, Toast.LENGTH_LONG).show(); //Traducir
                Log.d("Firebase Error Save: Perfil", error.toString());
            }
        });

    }
    /*Se leen los pasos totales de firebase. Si hay pone los que recibe sino, 0 y se actualiza la distancia*/
    private void leerPasosTotales() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild(PerfilActivity.PASOSTOTALESDB)) {
                        tv_pasos.setText(snapshot.child(PerfilActivity.PASOSTOTALESDB).getValue().toString());
                        pasosTotales = Integer.parseInt(tv_pasos.getText().toString());
                    } else {
                        tv_pasos.setText("0");
                        pasosTotales = 0;
                    }
                    actDistancia();
                    //Si cuando responda firebase, se cargaron los datos de hoy y es -1 (no había nada en ShPr) se asigna ahora
                    if (pasosTotalesInicioDia == -1) {
                        pasosTotalesInicioDia = pasosTotales;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    /*suma uno a pasos totales y actualiza la vista de pasos y distancia*/
    private void darPaso() {
        pasosTotales++;
        tv_pasos.setText(String.valueOf(pasosTotales));
        actDistancia();
    }
    /*Se añade el pasoDeHoy al Historial.
     * Se ordena el arrayList por fecha (Recientes).
     * Si el tamaño es mayor al permitido sobreescribe el ultimo elemento sino inserta normal*/
    private void addPasoDiario() {
        this.HistorialDePasos.sort(Comparator.comparing(PasosFecha::getFecha).reversed());
        int tam = HistorialDePasos.size();
        pasosDeHoy.setPasos(pasosTotales - pasosTotalesInicioDia);// aqui se cambia el paso -1 (vacío) a los que se hayan hecho
        if (tam >= maxTam) {
            HistorialDePasos.set(maxTam - 1, pasosDeHoy); //sobreescribe el ultimo
            this.HistorialDePasos.sort(Comparator.comparing(PasosFecha::getFecha).reversed());
            //reordena para que el penultimo ya sea el ultimo. Metes un nuevo posicion 0 o un mas reciente.
        } else {
            HistorialDePasos.add(pasosDeHoy);
        }
    }
    /*Se añade el pasoDeHoy al historial.
     * Se crea un nuevo pasoDeHoy y se reasigna pasosTotalesInicio dia.
     * Ya que los pasos que ha dado en un día sería la resta de los que tiene cuando acaba y los que tenía
     * el empezar ese día.
     * Se pone una tarea.
     */
    private void contarPasoDiario() {
        addPasoDiario();
        pasosDeHoy = new PasosFecha(-1);//
        pasosTotalesInicioDia = pasosTotales;
        setTimerTask();
    }
    /*Vuelve a cargar las opciones (para si poner km o mi) y actualiza la vista*/
    private void actDistancia() {
        super.loadSettings();
        tv_distancia.setText(super.settsMan.stepsToDistance(pasosTotales));
    }
    /*convierte el historial a gson*/
    private String getHistorialJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this.HistorialDePasos);
    }
    /*A partir del nombre de AjustesActivity, se guarda en SharedPrefences los datos del Historail*/
    private void guardarHistorialSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(AjustesActivity.getSaveName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String pasosFechaJsonString = getHistorialJsonString();
        editor.putString(PASOSDIARIOSGSON, pasosFechaJsonString);
        editor.apply();
    }
    /*Se carga el historial. Se ordena para mostrar del más reciente al antiguo*/
    private void cargarHistorialSharedPreferences(String pasosFechaJsonString) {
        Gson gson = new Gson();
        PasosFecha[] pasosFechaFromJson = gson.fromJson(pasosFechaJsonString, PasosFecha[].class);
        this.HistorialDePasos.clear();
        this.HistorialDePasos.addAll(Arrays.asList(pasosFechaFromJson));
        this.HistorialDePasos.sort(Comparator.comparing(PasosFecha::getFecha).reversed());
    }
    /*A partir del nombre de AjustesActivity se coge el historial. Si no hay, uno vacío y si hay se carga a partir del json*/
    private void doLoadHistorialFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(AjustesActivity.getSaveName(), Context.MODE_PRIVATE);
        String pasosFechaJsonString = preferences.getString(PASOSDIARIOSGSON, "");
        if (!pasosFechaJsonString.isEmpty()) {
            cargarHistorialSharedPreferences(pasosFechaJsonString);
        } else {
            HistorialDePasos = new ArrayList<>();
        }
    }
    /*Se guardan ShPr los datos de hoy con los nombres de los strings que hay arriba.*/
    private void guardarDatosDeHoy() {
        SharedPreferences preferences = getSharedPreferences(AjustesActivity.getSaveName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PASOSTOTALESINICIO, pasosTotalesInicioDia);
        Gson gson = new Gson();
        String pasosFechaJsonString = gson.toJson(this.pasosDeHoy);
        editor.putString(PASOSFECHAHOY, pasosFechaJsonString);
        editor.apply();
    }
    /*Se cargan los datos de hoy de ShPr. Si no hay se crean unos vacíos.*/
    private void cargarDatosDeHoy() {
        SharedPreferences preferences = getSharedPreferences(AjustesActivity.getSaveName(), Context.MODE_PRIVATE);
        pasosTotalesInicioDia = preferences.getInt(PASOSTOTALESINICIO, -1);
        if (pasosTotalesInicioDia == -1) {
            pasosTotalesInicioDia = pasosTotales;
        }
        Gson gson = new Gson();
        String pasosFechaJsonString = preferences.getString(PASOSFECHAHOY, "");
        if (!pasosFechaJsonString.isEmpty()) {
            pasosDeHoy = gson.fromJson(pasosFechaJsonString, PasosFecha.class);
        } else {
            pasosDeHoy = new PasosFecha(-1);
        }
    }
}