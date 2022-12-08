package cristo.app.proyectodm.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.adapter.Adaptador;
import cristo.app.proyectodm.adapter.AdaptadorAmigos;
import cristo.app.proyectodm.model.Entidad;
import cristo.app.proyectodm.model.EntidadAmigos;

//Actividad que muestras los amigos y peticiones de amistad del usuario conectado
public class AmigosActivity extends ToolActivity {
    //Conexion con la bd
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mRef;

    //Para peticiones
    Button btnAnadirAmigo;

    ListView lvPeticiones;
    ArrayList<Entidad> myArrayList = new ArrayList<>();
    Adaptador myArrayAdapter;

    //Para amigos
    ListView lvAmigos;
    ArrayList<EntidadAmigos> myArrayListAmigos = new ArrayList<>();
    AdaptadorAmigos myArrayAdapterAmigos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);

        //Nos permite cambiar de actividad
        tool();

        //Conexion con la bd
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Muestra los amigos del usuario en la listview
        myArrayAdapterAmigos = new AdaptadorAmigos(this, myArrayListAmigos);

        lvAmigos = findViewById(R.id.list_amigos);
        lvAmigos.setAdapter(myArrayAdapterAmigos);

        //Registra menu para eliminar usuario en cada elemento de la listview
        registerForContextMenu(lvAmigos);

        String id_user_actual = mAuth.getCurrentUser().getUid();

        //Coge los datos de los amigos del usuario y los muestra en la listview
        mDatabase.child("Amigos").child(id_user_actual).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String correo = snapshot.getValue().toString();
                if (snapshot.getValue().toString().equals(correo)) {
                    String UIDAmigo = snapshot.getKey().toString();

                    mDatabase.child("Users").child(UIDAmigo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String pasos = snapshot.child(PerfilActivity.PASOSTOTALESDB).getValue().toString();
                            String foto="";
                            if (snapshot.hasChild(PerfilActivity.FOTOPERFILDB)) {
                                foto= snapshot.child(PerfilActivity.FOTOPERFILDB).getValue().toString();
                            }

                            myArrayListAmigos.add(new EntidadAmigos(correo, pasos, UIDAmigo,foto));
                            myArrayAdapterAmigos.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                myArrayAdapterAmigos.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Muestra las peticiones de amistad del usuario en la listview
        mRef = FirebaseDatabase.getInstance().getReference().child("Peticiones").child(id_user_actual);

        myArrayAdapter = new Adaptador(this, myArrayList);

        lvPeticiones = findViewById(R.id.list_peticiones);
        lvPeticiones.setAdapter(myArrayAdapter);

        String userEmail = mAuth.getCurrentUser().getEmail();

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String correoEnvia = snapshot.getValue(String.class);
                if (snapshot.getValue().toString().equals(correoEnvia)) {
                    String UIDEnvia = snapshot.getKey().toString();
                    myArrayList.add(new Entidad(correoEnvia, UIDEnvia, id_user_actual, userEmail));
                }
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Boton para enviar peticion de amistad
        btnAnadirAmigo = findViewById(R.id.btnAnadirAmigo);

        btnAnadirAmigo.setOnClickListener(new View.OnClickListener() {
            String usr;

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AmigosActivity.this);
                final EditText edUsr = new EditText(AmigosActivity.this);
                usr = "";
                builder.setTitle(getRString(R.string.AmigosActivity_anhadir_amigo));
                builder.setMessage(getRString(R.string.AmigosActivity_escribir_id));
                builder.setView(edUsr);
                builder.setPositiveButton(getRString(R.string.AmigosActivity_enviar_solicitud), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        usr = edUsr.getText().toString();
                        anadirPeticion(usr);
                    }
                });
                builder.setNegativeButton(getRString(R.string.PerfilActivity_cancelar), null);
                builder.create().show();
            }
        });
    }


    //Configuracion del menu de eliminar amigo y ver perfil del amigo
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId() == R.id.list_amigos){
            getMenuInflater().inflate(R.menu.menu_amigos, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //Metodo para "saber" que opcion se ha seleccionado (eliminar o ver perfil) y actuar en consecuencia
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo item_pos = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getItemId() == R.id.delete_amigo){
            EntidadAmigos Item = myArrayListAmigos.get(item_pos.position);

            String idfriend= Item.getUIDAmigo();

            String correofriend=Item.getCorreo();

            confirmClear(idfriend, item_pos);
        }

        if(item.getItemId()==R.id.perfil_amigo){
            Bundle datos = new Bundle();
            EntidadAmigos Item = myArrayListAmigos.get(item_pos.position);
            String idfriend= Item.getUIDAmigo();
            datos.putString("idfriend",idfriend);

            mDatabase.child("Users").child(idfriend).addListenerForSingleValueEvent(new ValueEventListener() {
                String pasos="",nombre="",descripcion="",foto="";
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        pasos=snapshot.child("pasosTotales").getValue().toString();
                        datos.putString("pasos",pasos);
                        nombre=snapshot.child("name").getValue().toString();
                        datos.putString("name",nombre);
                        if (snapshot.hasChild("descripcion")) {
                            descripcion=snapshot.child("descripcion").getValue().toString();
                            datos.putString("descripcion", descripcion);

                        }
                        Intent intent = new Intent(AmigosActivity.this, AmigoIndivActivity.class);
                        intent.putExtras(datos);
                        startActivity(intent);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return true;
}

    //Crear peticion de amistad en la bd
        private void anadirPeticion (String usr){
            String userUID = mAuth.getCurrentUser().getUid();
            String userEmail = mAuth.getCurrentUser().getEmail();

        if(usr.equals("")){
            Toast.makeText(AmigosActivity.this, R.string.AmigosActivity_solicitud_vacia, Toast.LENGTH_SHORT).show();
        }
        else {
            if (usr.equals(userUID)) {    //Se comprueba que el usuario no se envía a si mismo una solicitud
                Toast.makeText(AmigosActivity.this, R.string.AmigosActivity_id_igual, Toast.LENGTH_LONG).show();
            } else {
                mDatabase.child("Users").child(usr).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {    //Comprueba si existe el usuario al que se le envía la solicitud
                            mDatabase.child("Amigos").child(usr).child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        if (snapshot.getValue().toString().equals(userEmail)) {     //Se comprueba si ya tienes a ese usuario como amigo
                                            Toast.makeText(AmigosActivity.this, R.string.AmigosActivity_ya_es_amigo, Toast.LENGTH_SHORT).show();
                                        } else {        //Si no es tu amigo, se crea la solicitud de amistad
                                            Map<String, Object> map = new HashMap<>();
                                            map.put(userUID, userEmail);


                                            mDatabase.child("Peticiones").child(usr).updateChildren(map);

                                            Toast.makeText(AmigosActivity.this, R.string.AmigosActivity_solicitud_enviada, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {    //Si no tienes a ningún amigo, también se crea la solicitud de amistad
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(userUID, userEmail);


                                        mDatabase.child("Peticiones").child(usr).updateChildren(map);

                                        Toast.makeText(AmigosActivity.this, R.string.AmigosActivity_solicitud_enviada, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Toast.makeText(AmigosActivity.this, getRString(R.string.AmigosActivity_usuario_no_existe), Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    /*Para confirmar borrado. Si cancela no se hace nada. Si acepta se borra*/
    private void confirmClear(String idfriend, AdapterView.AdapterContextMenuInfo item_pos) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AmigosActivity.this);
        builder.setTitle(R.string.HistorialActivity_borrar_pasos); // TRADUCIR !!
        builder.setPositiveButton(R.string.PerfilActivity_aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarAmigo(idfriend, item_pos);
            }
        });
        builder.setNegativeButton(R.string.PerfilActivity_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    //Eliminar amigo en la bd
    private void eliminarAmigo(String idfriend, AdapterView.AdapterContextMenuInfo item_pos) {
        String id2 = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(AmigosActivity.this, getRString(R.string.AmigosActivity_eliminar_amigo), Toast.LENGTH_SHORT).show();

        myArrayListAmigos.remove(item_pos.position);
        myArrayAdapterAmigos.notifyDataSetChanged();

        mRef.child("Amigos").child(idfriend).child(id2).removeValue();
        mRef.child("Amigos").child(id2).child(idfriend).removeValue();
    }
}