package cristo.app.proyectodm.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cristo.app.proyectodm.R;

//Actividad que muestra el perfil de un amigo del usuario conectado
public class AmigoIndivActivity extends ToolActivity {
    Button btnAtrasAmigo;
    Bundle bundle;
    DatabaseReference mDatabase;
    TextView pasos_totales_amigo;
    int pasosTotales = 0;
    TextView distancia_total_amigo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_ind);

        bundle=getIntent().getExtras();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView descripcion_amigo;
        TextView nombre_amigo;

        ImageView imagen_amigo;

        descripcion_amigo = findViewById(R.id.descripcion_amigo);
        pasos_totales_amigo = findViewById(R.id.pasos_totales_amigo);
        distancia_total_amigo=findViewById(R.id.distancia_total_amigo);
        nombre_amigo=findViewById(R.id.nombre_amigo);
        imagen_amigo=findViewById(R.id.imagen_amigo);

        String descripcion = bundle.getString("descripcion");
        String pasos = bundle.getString("pasos");
        String nombre = bundle.getString("name");
        String idfriend= bundle.getString("idfriend");

        //Si el amigo tiene, se pone su foto guardada en la base de datos, si no se pone una por defecto
        mDatabase.child("Users").child(idfriend).addListenerForSingleValueEvent(new ValueEventListener() {
            String foto="";
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(PerfilActivity.FOTOPERFILDB)) {
                    foto = snapshot.child(PerfilActivity.FOTOPERFILDB).getValue().toString();
                    imagen_amigo.setImageBitmap(StringToBitMap(foto));
                }else {
                    Drawable image= ContextCompat.getDrawable(AmigoIndivActivity.this,R.drawable.logo);

                    imagen_amigo.setImageDrawable(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Se muestran los datos del amigo
        descripcion_amigo.setText(descripcion);
        pasos_totales_amigo.setText(pasos);
        nombre_amigo.setText(nombre);

        btnAtrasAmigo = findViewById(R.id.btnAtras);

        //Boton para volver a la actividad de amigos
        btnAtrasAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AmigoIndivActivity.this, AmigosActivity.class));
                finish();
            }
        });

    }

    //Convierte string a bitmap para la imagen
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

    @Override
    protected void onResume() {
        super.onResume();
        actDistancia();
    }

    //Metodo para hacer la conversion de pasos a km/millas
    private void actDistancia() {
        super.loadSettings();
        pasosTotales = Integer.parseInt(pasos_totales_amigo.getText().toString());
        distancia_total_amigo.setText(super.settsMan.stepsToDistance(pasosTotales));
    }
}