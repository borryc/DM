package cristo.app.proyectodm.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Clase para gestionar las conexiones con la base de datos firebase
public class FirebaseManager {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private boolean exito;
    private ArrayList<String> datos;

    public static final String USERNAMEDB = "name";
    public static final String PASOSTOTALESDB = "pasosTotales";
    public static final String DESCRIPCIONDB = "descripcion";
    public static final String FOTOPERFILDB = "foto_perfil";

    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        exito = true;
        datos = new ArrayList<>();
    }

    //Metodo para obtener el UID del usuario actual
    public String getUserUid() {
        return mAuth.getUid();
    }

    //Metodo para obtener todas las tablas de la base de datos
    public DatabaseReference getUsersTables() {
        return mRef;
    }

    //Metodo para obtener la tabla del usuario que esta logeado
    public DatabaseReference getCurrentUserTable() {
        return getUsersTables().child(getUserUid());
    }

    //Metodo para guardar los datos del usuario en la base de datos
    public boolean saveUser(String username, String descripcion, Bitmap fotoDePerfil) {
        exito = true;
        DatabaseReference usuario = getCurrentUserTable();
        usuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> guardarPerfil = new HashMap<>();
                    guardarPerfil.put(USERNAMEDB, username);
                    guardarPerfil.put(DESCRIPCIONDB, descripcion);
                    if (fotoDePerfil != null) {
                        guardarPerfil.put(FOTOPERFILDB, BitMapToString(fotoDePerfil));
                    }
                    usuario.updateChildren(guardarPerfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exito = false;
                Log.d("Firebase Save: Perfil", error.toString());
            }
        });
        return exito; //si false Toast error si true Toast exito
    }

    //Metodo para obtener los datos del usuario actual
    public ArrayList<String> getUser() {
        datos.clear();
        DatabaseReference usuario = getCurrentUserTable();
        usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    datos.add(snapshot.child(USERNAMEDB).getValue().toString());
                    datos.add(snapshot.child(PASOSTOTALESDB).getValue().toString());
                    if (snapshot.hasChild(DESCRIPCIONDB)) {
                        datos.add(snapshot.child(DESCRIPCIONDB).getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return datos;
    }

    //Metodo para obtener la foto del usuario actual
    public Bitmap getProfilePhoto() {
        final Bitmap[] foto = new Bitmap[1];
        DatabaseReference usuario = getCurrentUserTable();
        usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(FOTOPERFILDB)) {
                    foto[0] = (StringToBitMap(snapshot.child(FOTOPERFILDB).getValue().toString()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return foto[0];
    }

    //Metodo para pasar de BitMap a String
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    //Metodo para pasar de String a BitMap
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