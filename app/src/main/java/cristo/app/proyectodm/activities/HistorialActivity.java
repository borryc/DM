package cristo.app.proyectodm.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.core.PasosFecha;
import cristo.app.proyectodm.adapter.PasosFechaArrayAdapter;

public class HistorialActivity extends ToolActivity {


    private ArrayList<PasosFecha> HistorialDePasos;
    private PasosFechaArrayAdapter pasosFechaArrayAdapter;
    private ListView listView;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    ImageView iv_foto_perfil;

    /*Se carga el historial, y se vuelve a cargar el adaptador por si cambió la configuración*/
    @Override
    protected void onResume() {
        super.onResume();
        doLoadHistorialFromSharedPreferences();
        crearArrayAdapter();
        listView.setAdapter(pasosFechaArrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        tool();
        mAuth = FirebaseAuth.getInstance();
        HistorialDePasos = new ArrayList<>();

        iv_foto_perfil = findViewById(R.id.iv_historial_foto);
        loadFotoPerfilUser();

        crearArrayAdapter();
        listView = findViewById(R.id.lv_historial_lista);
        listView.setAdapter(pasosFechaArrayAdapter); //El adapter nunca se modifica, solo es lectura

        this.registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lv_historial_lista) {
            this.getMenuInflater().inflate(R.menu.historial_pasos_options_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menuHistorialBorrar):
                confirmClear();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    /*Para confirmar borrado. Si cancela no se hace nada. Si acepta se borra*/
    private void confirmClear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HistorialActivity.this);
        builder.setTitle(R.string.HistorialActivity_borrar_pasos); // TRADUCIR !!
        builder.setPositiveButton(R.string.PerfilActivity_aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doClear();
            }
        });
        builder.setNegativeButton(R.string.PerfilActivity_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    //Historial a json
    private String getHistorialJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this.HistorialDePasos);
    }

    /*Se guarda historial en SharedPreferences con el nombre que se genera en AjustesActivity*/
    private void guardarHistorialSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(AjustesActivity.getSaveName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String pasosFechaJsonString = getHistorialJsonString();
        editor.putString(InicioActivity.PASOSDIARIOSGSON, pasosFechaJsonString);
        editor.apply();
    }

    /* Se carga el array de pasos. se limpia el que esté ahora y se añade al arraylist. Se ordena con la fecha y se le da la vuelta.
     * Asi se ven desde el reciente al último*/
    private void cargarHistorialSharedPreferences(String pasosFechaJsonString) {
        Gson gson = new Gson();
        PasosFecha[] pasosFechaFromJson = gson.fromJson(pasosFechaJsonString, PasosFecha[].class);
        this.HistorialDePasos.clear();
        this.HistorialDePasos.addAll(Arrays.asList(pasosFechaFromJson));
        this.HistorialDePasos.sort(Comparator.comparing(PasosFecha::getFecha).reversed());
    }

    //Se coge el Historial guardado en ShPr, si es vacio se crea vacio sino se manda lo cargado a cargarHistorialSharedPreferences
    private void doLoadHistorialFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(AjustesActivity.getSaveName(), Context.MODE_PRIVATE);
        String pasosFechaJsonString = preferences.getString(InicioActivity.PASOSDIARIOSGSON, "");
        if (!pasosFechaJsonString.isEmpty()) {
            cargarHistorialSharedPreferences(pasosFechaJsonString);
        } else {
            HistorialDePasos = new ArrayList<>();
        }
    }

    /*Para borrar los datos. Una vez se borran, se pone el vacío también en ShPr para que no lo vuelva a cargar.
     * Se notifica al adaptador de que han cambiado datos*/
    private void doClear() {
        HistorialDePasos.clear();
        pasosFechaArrayAdapter.notifyDataSetChanged();
        guardarHistorialSharedPreferences();
    }

    /*Se carga el adaptador a partir de las opciones*/
    private void crearArrayAdapter() {
        super.loadSettings();
        pasosFechaArrayAdapter = new PasosFechaArrayAdapter(this, HistorialDePasos, super.settsMan);
    }

    /*Para cargar la imagen de perifl. Se hace con setImageBitmap de ImageView.*/
    private void loadFotoPerfilUser() {
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild(PerfilActivity.FOTOPERFILDB)) {
                        iv_foto_perfil.setImageBitmap(StringToBitMap(snapshot.child(PerfilActivity.FOTOPERFILDB).getValue().toString()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    /*El bitmap se guarda como string en base64 en la base de datos. Aquí se convierte e bitmap*/
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}