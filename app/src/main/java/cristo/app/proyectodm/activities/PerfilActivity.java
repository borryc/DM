package cristo.app.proyectodm.activities;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cristo.app.proyectodm.R;

public class PerfilActivity extends ToolActivity {


    private String username;
    private int pasosTotales;
    private String distancia;
    private String descripcion;
    private Bitmap fotoDePerfil;

    private Button bt_cerrarSesion;
    private Button bt_copiarID;
    private Button bt_guardarCambios;
    private Button btn_perfil_cambiar_contrasena;
    private TextView tv_username;
    private TextView tv_pasos;
    private TextView tv_distancia;
    private TextView tv_descripcion;
    private ImageView iv_foto_perfil;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private String id_usuario;

    private boolean onActivityResultTrigger;

    public static final int PICK_IMAGE = 1;
    public static final String USERNAMEDB = "name";
    public static final String PASOSTOTALESDB = "pasosTotales";
    public static final String DESCRIPCIONDB = "descripcion";
    public static final String FOTOPERFILDB = "foto_perfil";

    /*Cuando se vuelve de la Actividad de Android de pedir una foto se gestiona aqui*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Si el codigo es el de imagen, ha ido bien y ha mandado algo
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null)
            try {
                if (fotoDePerfil != null) {
                    fotoDePerfil.recycle();//se recicla el bitmap
                }
                InputStream stream = getContentResolver().openInputStream(
                        data.getData()); //se coge la información
                fotoDePerfil = BitmapFactory.decodeStream(stream);//se decodifica
                stream.close();//se cierra el stream
                fotoDePerfil = Bitmap.createScaledBitmap(fotoDePerfil, iv_foto_perfil.getWidth()
                        , iv_foto_perfil.getHeight(), false);
                //se escala el bitmap para la foto de perfil.
                // Es decir no guardar una foto en 4k como 4k si no como lo que va a ocupar
                iv_foto_perfil.setImageBitmap(fotoDePerfil);//se pone en la vista
                onActivityResultTrigger = true; //se hace esto para comprabar en OnResume que viene de aqui
                //y asi no quite la vista previa al usuario, = no cargar de nuevo la imagen fireBase
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
//si se viene de leer foto no se carga el usuario
        if (!onActivityResultTrigger) {
            loadUser();
            onActivityResultTrigger = false;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tool();
        onActivityResultTrigger = false;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        id_usuario = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        tv_username = findViewById(R.id.tv_perfil_username);
        tv_pasos = findViewById(R.id.tv_perfil_pasos);
        tv_distancia = findViewById(R.id.tv_perfil_distancia);
        tv_descripcion = findViewById(R.id.tv_perfil_descripcion);
        iv_foto_perfil = findViewById(R.id.iv_perfil_foto);
        bt_cerrarSesion = findViewById(R.id.bt_perfil_cerrarSesion);
        btn_perfil_cambiar_contrasena = findViewById(R.id.btn_perfil_cambiar_contrasena);
        bt_copiarID = findViewById(R.id.bt_perfil_copiarId);
        bt_guardarCambios = findViewById(R.id.bt_perfil_guardarCambios);

        loadUser();//carga usuario

        iv_foto_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputImage();
            }
        });

        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog(tv_username.getText().toString(), 0);
            }
        });

        tv_descripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog(tv_descripcion.getText().toString(), 1);
            }
        });

        bt_copiarID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarIdPortapapeles();
            }
        });
        bt_guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatosFirebase();
            }
        });

        bt_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PerfilActivity.this, MainActivity.class));
                finish();
            }
        });

        btn_perfil_cambiar_contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilActivity.this, CambiarContrasenaActivity.class));
            }
        });
    }

    /*Se cogen los datos de firebase. Si leen ciertos datos si existen. Luego se le asginan esos datos
     * a variables globales. Da problemas si asignas directamente a variable global*/
    private void loadUser() {
        pasosTotales = 0;
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id_usuario);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tv_username.setText(snapshot.child(USERNAMEDB).getValue().toString());
                    tv_pasos.setText(snapshot.child(PASOSTOTALESDB).getValue().toString());
                    if (snapshot.hasChild(DESCRIPCIONDB)) {
                        tv_descripcion.setText(snapshot.child(DESCRIPCIONDB).getValue().toString());
                    }
                    if (snapshot.hasChild(FOTOPERFILDB)) {
                        iv_foto_perfil.setImageBitmap(StringToBitMap(snapshot.child(FOTOPERFILDB).getValue().toString()));
                    }
                    assingDataFromUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    /*Como no se actualizan en la vista los datos al momento, se ponen aqui los datos en las variables
     * para llamar en el onDataChange una vez se tengan los datos
     * por alguna razon no se guardan en variables los datos ahi entonces hay que ponerlos ya en la vista*/
    private void assingDataFromUser() {

        username = tv_username.getText().toString();
        pasosTotales = Integer.parseInt(tv_pasos.getText().toString());
        super.loadSettings();
        distancia = super.settsMan.stepsToDistance(pasosTotales);
        tv_distancia.setText(distancia);
        if (!tv_descripcion.getText().toString().equalsIgnoreCase("")) {
            descripcion = tv_descripcion.getText().toString();
        }
    }

    /*se guardan los datos en firebase preguntando por confirmación.
     Los datos guardados son los de las variables globales*/
    private void guardarDatosFirebase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(super.getRString(R.string.PerfilActivity_guardar_estas_seguro));
        builder.setPositiveButton(super.getRString(R.string.PerfilActivity_aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id_usuario);
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Map<String, Object> guardarPerfil = new HashMap<>();
                            guardarPerfil.put(USERNAMEDB, username);
                            guardarPerfil.put(DESCRIPCIONDB, descripcion);
                            if (fotoDePerfil != null) {
                                guardarPerfil.put(FOTOPERFILDB, BitMapToString(fotoDePerfil));
                            }
                            mRef.updateChildren(guardarPerfil);

                            Toast.makeText(PerfilActivity.this, PerfilActivity.super.getRString(R.string.PerfilActivity_guardar_cambios), Toast.LENGTH_LONG).show(); //Traducir
                        } else {
                            // Toast.makeText(AmigosActivity.this, "Ese usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PerfilActivity.this, PerfilActivity.super.getRString(R.string.PerfilActivity_guardar_cambios_error), Toast.LENGTH_LONG).show(); //Traducir
                        Log.d("Firebase Save: Perfil", error.toString());
                    }
                });


            }
        });
        builder.setNegativeButton(R.string.PerfilActivity_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    /*Para guardar el id de usuario en el portapapeles. Se usa para añadir un amigo*/
    private void guardarIdPortapapeles() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("id", id_usuario);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, super.getRString(R.string.PerfilActivity_id_copiado), Toast.LENGTH_LONG).show();
    }

    /*Para pedir una imagen. Se avisa que se abrirá la galería. Se le pasa el código de pedir imagen PICK_IMAGE*/
    private void inputImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(super.getRString(R.string.PerfilActivity_abrir_galeria));
        builder.setPositiveButton(super.getRString(R.string.PerfilActivity_aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, PerfilActivity.super.getRString(R.string.PerfilActivity_elige_imagen)), PICK_IMAGE); //!!Traducir
            }
        });
        builder.setNegativeButton(super.getRString(R.string.PerfilActivity_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();

    }

    /*Para editar descripción o nombre de usuario. Con guardar y cancelar*/
    private void inputDialog(String anteriorValor, int variableParaGuardar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(super.getRString(R.string.PerfilActivity_editar_perfil));
        EditText editText = new EditText(this);
        editText.setText(anteriorValor);
        builder.setView(editText);
        builder.setPositiveButton(super.getRString(R.string.PerfilActivity_guardar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                guardarVariable(editText.getText().toString(), variableParaGuardar);
            }
        });
        builder.setNegativeButton(super.getRString(R.string.PerfilActivity_cancelar), new DialogInterface.OnClickListener() { //!!Traducir
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
    /*Guarda en una variable global los datos y actualiza la vista.*/
    private void guardarVariable(String valor, int variableParaGuardar) {
        /*
        0: Username
        1:Descripcion
         */

        switch (variableParaGuardar) {
            case 0:
                username = valor;
                tv_username.setText(username);
                break;
            case 1:
                descripcion = valor;
                tv_descripcion.setText(descripcion);
                break;
            default:
                Toast.makeText(this, super.getRString(R.string.PerfilActivity_guardar_variable_error), Toast.LENGTH_LONG).show();
                break;
        }
    }
    /*Convierte un bitmap (imagen) a string base64 para guardarlo en firebase*/
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    /* convierte string base64 a bitmap*/
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