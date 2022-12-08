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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cristo.app.proyectodm.R;

//Actividad para que se inicie sesion, se recupere la contraseña o se registre si uno no tiene cuenta
public class LoginActivity extends AppCompatActivity {
    Toolbar tb_login;
    EditText et_email_login, et_password_login;
    Button btn_sign_in;
    TextView tv_registrarse_ahora, tv_recuperar_contrasena_login;
    String email= "";
    String password= "";

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpActionBar();

        mAuth=FirebaseAuth.getInstance();

        et_email_login = findViewById(R.id.et_email_login);
        et_password_login = findViewById(R.id.et_password_login);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        tv_registrarse_ahora = findViewById(R.id.tv_registrarse_ahora);
        tv_recuperar_contrasena_login = findViewById(R.id.tv_recuperar_contrasena_login);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email= et_email_login.getText().toString();
                password= et_password_login.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    loginUser();
                }else{
                    Toast.makeText(LoginActivity.this, R.string.CamContrasenhaActivity_rellena_campos, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_recuperar_contrasena_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RecuperarContrasenaActivity.class));
            }
        });

        tv_registrarse_ahora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrarActivity.class));
                finish();
            }
        });
    }

    private void loginUser() {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    startActivity(new Intent(LoginActivity.this, InicioActivity.class));
                    finish();

                }else{
                    Toast.makeText(LoginActivity.this, R.string.LoginActivity_error_iniciar_sesion, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpActionBar() {
        tb_login = findViewById(R.id.tb_login);
        setSupportActionBar(tb_login);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }
}