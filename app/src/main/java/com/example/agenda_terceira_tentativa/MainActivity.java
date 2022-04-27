package com.example.agenda_terceira_tentativa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;//Banco de dados
import android.database.Cursor;//Navegar nos dados
import android.widget.*;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
  private EditText nome, matricula, idade;
  private TextView nomeCadastrado1, matriculaCadastrada1, idadeCadastrada1, hint;
  private Button cadastrarButton, voltarButton, prosseguirButton;
  private ImageButton novoCadastroButton;
  //private ImageButton removerButton;
  private ImageView imagemUsuario1;

  SQLiteDatabase db = null;
  Cursor cursor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    hint = findViewById(R.id.textTip);
    nome = findViewById(R.id.editTextNome);
    matricula = findViewById(R.id.editTextMatricula);
    idade = findViewById(R.id.editTextIdade);

    nomeCadastrado1 = findViewById(R.id.textViewDadosNome1);
    matriculaCadastrada1 = findViewById(R.id.textViewDadosMatricula1);
    idadeCadastrada1 = findViewById(R.id.textViewDadosIdade1);
    imagemUsuario1 = findViewById(R.id.imagemUsuario1);

    cadastrarButton = findViewById(R.id.buttonCadastro);
    voltarButton = findViewById(R.id.buttonAnterior);
    prosseguirButton = findViewById(R.id.buttonProximo);
    novoCadastroButton = findViewById(R.id.imageButtonNovoCliente);
    //removerButton = findViewById(R.id.imageRemoverButton);

    abrirBanco();
    abrirOuCriarTabela();
    fecharDb();

    novoCadastroButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        cadastrarButton.setVisibility(View.VISIBLE);
        nome.setVisibility(View.VISIBLE);
        matricula.setVisibility(View.VISIBLE);
        idade.setVisibility(View.VISIBLE);
        hint.setVisibility(View.INVISIBLE);
      }
    });
  }

  public void abrirBanco(){
    try{
      db=openOrCreateDatabase("dadosAgenda",MODE_PRIVATE,null);
    }catch (Exception ex){
      msg("Erro ao abrir ou criar o banco de dados");
    }
  }

  public void abrirOuCriarTabela(){
    try{
      db.execSQL("CREATE TABLE IF NOT EXISTS contatos (id INTEGER PRIMARY KEY, nome TEXT, matricula INTEGER, idade INTEGER);");
    } catch (Exception ex){
      msg("Erro ao criar tabela");
    }
  }

  public void fecharDb(){
    db.close();
  }

  public void inserirRegistro(View v){
    String stNome, stMatricula, stIdade;
    stNome = nome.getText().toString();
    stMatricula = matricula.getText().toString();
    stIdade = idade.getText().toString();
    if (stNome.equals("") || stMatricula.equals("") || stIdade.equals("")){
      msg("Preencha o nome, a matrícula e a idade.");
      return;
    }
    abrirBanco();
    try {
      db.execSQL("INSERT INTO contatos (nome, matricula, idade) VALUES ('"+stNome+"','"+stMatricula+"','"+stIdade+"')");
    }catch (Exception e){
      msg("Erro ao inserir registro.");
    }finally{
      msg("Dados inseridos com sucesso.");
    }
    fecharDb();
    nome.setText(null);
    matricula.setText(null);
    idade.setText(null);
    voltarButton.setVisibility(View.VISIBLE);
    prosseguirButton.setVisibility(View.VISIBLE);
    //removerButton.setVisibility(View.VISIBLE);
    buscarDados();
  }

  /*public void removerRegistro(View v){
    String stNome, stMatricula, stIdade;
    stNome = nomeCadastrado1.getText().toString();
    stMatricula = matriculaCadastrada1.getText().toString();
    stIdade = idadeCadastrada1.getText().toString();
    abrirBanco();
    try {
      db.delete("contatos ", "id" + "=" + "id",null);
      db.execSQL("VACUUM");
      nomeCadastrado1.setText(null);
      matriculaCadastrada1.setText(null);
      idadeCadastrada1.setText(null);
      imagemUsuario1.setVisibility(View.INVISIBLE);
    }catch (Exception e){
      msg("Erro ao remover registro.");
    }finally{
      msg("Dados removidos com sucesso.");
    }
    fecharDb();
  }*/

  public void msg(String txt){
    AlertDialog.Builder adb = new AlertDialog.Builder(this);
    adb.setMessage(txt);
    adb.setNeutralButton("Ok",null);
    adb.show();
  }

  public void buscarDados() {
    abrirBanco();
    cursor=db.query(
      "contatos", new String[] {"nome", "matricula", "idade"},
    null, null, null, null, null, null
    );
    if (cursor.getCount() != 0){
      cursor.moveToFirst();
      mostrarDados();
    }else {
      msg("Nenhum registro encontrado :(");
    }
  }

  @SuppressLint("Range")
  public void mostrarDados() {
    imagemUsuario1.setVisibility(View.VISIBLE);
    nomeCadastrado1.setText(cursor.getString(cursor.getColumnIndex("nome")));
    matriculaCadastrada1.setText(cursor.getString(cursor.getColumnIndex("matricula")));
    idadeCadastrada1.setText(cursor.getString(cursor.getColumnIndex("idade")));
  }

  public void proximoButton(View v){
    try {
      cursor.moveToNext();
      mostrarDados();
    }catch (Exception e){
      if (cursor.isAfterLast()){
        msg("Último registro alcançado");
      }else {
        msg("Erro ao navegar nos registros");
      }
    }
  }

  public void anteriorButton(View v) {
    try {
      cursor.moveToPrevious();
      mostrarDados();
    } catch (Exception e) {
      if (cursor.isBeforeFirst()) {
        msg("Primeiro registro alcançado");
      } else {
        msg("Erro ao navegar nos registros");
      }
    }
  }


}