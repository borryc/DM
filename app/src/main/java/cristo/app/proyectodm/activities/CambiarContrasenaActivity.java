package cristo.app.proyectodm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cristo.app.proyectodm.R;

//Metodo para cambiar la contraseña del usuario actual
public class CambiarContrasenaActivity extends AppCompatActivity {
    Toolbar tb_cambiar_contrasena;
    Button btn_cambian_contrasena;
    EditText et_cambiar_contrasena_actual, et_cambiar_contrasena_nueva, et_cambiar_contrasena_nueva2;
    String contrasena_actual = "", nueva_contrasena = "", nueva_contrasena_repetida = "";
    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);
        setUpActionBar();

        btn_cambian_contrasena = findViewById(R.id.btn_cambiar_contrasena);
        et_cambiar_contrasena_actual = findViewById(R.id.et_cambiar_contrasena_actual);
        et_cambiar_contrasena_nueva = findViewById(R.id.et_cambiar_contrasena_nueva);
        et_cambiar_contrasena_nueva2 = findViewById(R.id.et_cambiar_contrasena_nueva2);
        mAuth = FirebaseAuth.getInstance();

        btn_cambian_contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarContrasena();
                mAuth.signOut();
                startActivity(new Intent(CambiarContrasenaActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    //Metodo que actualiza la contraseña del usuario logeado
    private void actualizarContrasena() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        contrasena_actual = et_cambiar_contrasena_actual.getText().toString().trim();
        nueva_contrasena = et_cambiar_contrasena_nueva.getText().toString().trim();
        nueva_contrasena_repetida = et_cambiar_contrasena_nueva2.getText().toString().trim();

        if(contrasena_actual.isEmpty() || nueva_contrasena.isEmpty() || nueva_contrasena_repetida.isEmpty()) {
            Toast.makeText(CambiarContrasenaActivity.this, R.string.CamContrasenhaActivity_rellena_campos, Toast.LENGTH_SHORT).show();
        }else if(!nueva_contrasena.equals(nueva_contrasena_repetida)) {
                Toast.makeText(CambiarContrasenaActivity.this, R.string.CamContrasenhaActivity_contrasenhas_no_coinciden, Toast.LENGTH_SHORT).show();
            }else {
                user.updatePassword(nueva_contrasena).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(CambiarContrasenaActivity.this, R.string.CamContrasenhaActivity_contrasenha_actualizada, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CambiarContrasenaActivity.this, R.string.CamContrasenhaActivity_contrasenha_no_actualizada, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
    }

    //Metodo para volver atras
    private void setUpActionBar() {
        tb_cambiar_contrasena = findViewById(R.id.tb_cambiar_contrasena);
        setSupportActionBar(tb_cambiar_contrasena);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }
}