package cristo.app.proyectodm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import cristo.app.proyectodm.R;

//Actividad para registra a un nuevo usuario
public class RegistrarActivity extends AppCompatActivity {
    Toolbar tb_registro;
    EditText et_usuario_registro, et_email_registro, et_password_registro, et_password2_registro;
    Button btn_sign_up;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        setUpActionBar();

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        et_usuario_registro = findViewById(R.id.et_usuario_registro);
        et_email_registro = findViewById(R.id.et_email_registro);
        et_password_registro = findViewById(R.id.et_password_registro);
        et_password2_registro = findViewById(R.id.et_password2_registro);
        btn_sign_up = findViewById(R.id.btn_sign_up);

        //Boton para registrar al usuario
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUser= et_usuario_registro.getText().toString().trim();
                String emailUser= et_email_registro.getText().toString().trim();
                String pwdUser= et_password_registro.getText().toString().trim();
                String pwd2User= et_password2_registro.getText().toString().trim();


                if(nameUser.isEmpty() || emailUser.isEmpty() || pwdUser.isEmpty() || pwd2User.isEmpty()){
                    Toast.makeText(RegistrarActivity.this, getRString(R.string.CamContrasenhaActivity_rellena_campos), Toast.LENGTH_SHORT).show();

                }
                else if(!pwdUser.equals(pwd2User)){
                    Toast.makeText(RegistrarActivity.this, getRString(R.string.CamContrasenhaActivity_contrasenhas_no_coinciden), Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(nameUser,emailUser,pwdUser);
                    Toast.makeText(RegistrarActivity.this, getRString(R.string.RegistrarActivity_usuario_registrado), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrarActivity.this, LoginActivity.class));
                }

            }
        });
    }

    //Metodo que registra al usuario y guarda sus datos en la base de datos
    private void registerUser(String nameUser, String emailUser, String pwdUser) {
        mAuth.createUserWithEmailAndPassword(emailUser,pwdUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("email",emailUser);
                    map.put("name",nameUser);
                    map.put("amigos",null);

                    String id= mAuth.getCurrentUser().getUid();

                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task.isSuccessful()) {
                                startActivity(new Intent(RegistrarActivity.this, LoginActivity.class));
                                finish();
                            }else{
                                Toast.makeText(RegistrarActivity.this, getRString(R.string.RegistrarActivity_datos_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegistrarActivity.this, getRString(R.string.RegistrarActivity_usuario_registrado_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Metodo para volver atras
    private void setUpActionBar() {
        tb_registro = findViewById(R.id.tb_registro);
        setSupportActionBar(tb_registro);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }
    public String getRString(int id){
        return getResources().getString(id);
    }
}