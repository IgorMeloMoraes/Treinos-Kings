package com.example.kingstreinos.Activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kingstreinos.R;
import com.example.kingstreinos.databinding.ActivityCadastroBinding;

public class CadastroActivity extends BaseActivity {

    ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();

    }

    @Override
    protected int getNavigationDrawerID() {
        return 0;
    }

    private void setVariable() {
        binding.CadastroBtn.setOnClickListener(v -> {
            String nome = binding.nomeEdt.getText().toString();
            String email = binding.userEdt.getText().toString();
            String senha = binding.passEdt.getText().toString();

            if (senha.length() < 6) {
                Toast.makeText(CadastroActivity.this, "Sua senha precisa ser maior que 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(CadastroActivity.this, task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Completo: ");
                    startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                } else {
                    Log.i(TAG, "Erro: " + task.getException());
                    Toast.makeText(CadastroActivity.this, "Falha na Autenticação", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}