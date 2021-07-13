package com.example.proyectoventas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    TextView nuevoUsuario, bienvenidoLabel, continuarLabel;
    ImageView registroImageView;
    TextInputLayout emailTextField, contrasenaTextField;
    MaterialButton inicioSesion;
    TextInputEditText emailEditText, contrasenaEditText, confirmarEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        registroImageView = findViewById(R.id.registroImageView);
        bienvenidoLabel = findViewById(R.id.bienvenidoLabel);
        continuarLabel = findViewById(R.id.continuarlabel);
        emailTextField = findViewById(R.id.emailTextField);
        contrasenaTextField = findViewById(R.id.contrasenaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);
        emailEditText = findViewById(R.id.emailEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        confirmarEditText = findViewById(R.id.confirmarEditText);

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionBack();
            }
        });

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void validate() {
        String email = emailEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();
        String confirmar = confirmarEditText.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Correo inválido");
            return;
        } else {
            emailEditText.setError(null);
        }

        if (contrasena.isEmpty() || contrasena.length() < 8) {
            contrasenaEditText.setError("Se necesitan más de 8 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(contrasena).find()) {
            contrasenaEditText.setError("Al menos un número");
            return;
        } else {
            contrasenaEditText.setError(null);
        }

        if (!confirmar.equals(contrasena)) {
            confirmarEditText.setError("Deben ser iguales");
            return;
        } else {
            registrar(email, contrasena);
        }
    }

    public void registrar(String email, String contrasena) {
        mAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegistroActivity.this, UsuarioActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistroActivity.this, "Fallo al registrarse", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        transitionBack();
    }

    public void transitionBack() {
        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);

        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(registroImageView, "logoImageTrans");
        pairs[1] = new Pair<View, String>(bienvenidoLabel, "textTrans");
        pairs[2] = new Pair<View, String>(continuarLabel, "iniciaSesionTextTrans");
        pairs[3] = new Pair<View, String>(emailTextField, "emailImputTextTrans");
        pairs[4] = new Pair<View, String>(contrasenaTextField, "passwordInputTextTrans");
        pairs[5] = new Pair<View, String>(inicioSesion, "buttonSignInTrans");
        pairs[6] = new Pair<View, String>(nuevoUsuario, "logoImageTrans");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistroActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
            finish();
        }
    }
}