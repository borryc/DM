package cristo.app.proyectodm.activities;

import android.app.ProgressDialog;
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

import cristo.app.proyectodm.R;

//Actividad para recuperar la contraseña en caso de que uno se olvide de ella
public class RecuperarContrasenaActivity extends AppCompatActivity {
    private EditText et_email_recuperar_contrasena;
    private Button btn_recuperar_contrasena;
    private Toolbar tb_recuperar_contrasena;
    private String email = "";
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);
        setUpActionBar();

        et_email_recuperar_contrasena = findViewById(R.id.et_email_recuperar_contrasena);
        btn_recuperar_contrasena = findViewById(R.id.btn_recuperar_contrasena);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(RecuperarContrasenaActivity.this);

        btn_recuperar_contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email_recuperar_contrasena.getText().toString();

                if(!email.isEmpty()) {
                    dialog.setMessage(getRString(R.string.RecuContrasenhaActivity_esperar));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    recuperarContrasena();
                    startActivity(new Intent(RecuperarContrasenaActivity.this, LoginActivity.class));
                    finish();
                }else {
                    Toast.makeText(RecuperarContrasenaActivity.this, getRString(R.string.RecuContrasenhaActivity_intro_mail), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    /*Metodo que envia un correo con un enlace, el cual nos mostrara una pestaña para cambiar la contraseña.
    * El correo se envia a un email que el usuario le pasa*/
    private void recuperarContrasena() {
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RecuperarContrasenaActivity.this, getRString(R.string.RecuContrasenhaActivity_correo_enviado), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RecuperarContrasenaActivity.this, getRString(R.string.RecuContrasenhaActivity_correo_enviado_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Metodo para volver atras
    private void setUpActionBar() {
        tb_recuperar_contrasena = findViewById(R.id.tb_recuperar_contrasena);
        setSupportActionBar(tb_recuperar_contrasena);
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