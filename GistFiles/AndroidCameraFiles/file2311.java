package com.example.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Perfil{

    public Pessoa getInformacao(String end){
        String json;
        Pessoa retorno;
        json = Conn.getJSONFromAPI(end);
        Log.i("Resultado", json);
        retorno = parseJson(json);

        return retorno;
    }

    private Pessoa parseJson(String json){
        try {
            Pessoa pessoa = new Pessoa();

            JSONObject jsonObj = new JSONObject(json);
            JSONArray array = jsonObj.getJSONArray("results");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data;

            JSONObject objArray = array.getJSONObject(0);


            //Atribui os objetos que estão nas camadas mais altas
            pessoa.setEmail(objArray.getString("email"));
            pessoa.setTelefone(objArray.getString("phone"));

            //Username & Senha são um objeto
            JSONObject login = objArray.getJSONObject("login");
            pessoa.setUsername(login.getString("username"));
            pessoa.setSenha(login.getString("password"));

            pessoa.setNascimento(objArray.getString("dob"));

            //Nome da pessoa é um objeto, instancia um novo JSONObject
            JSONObject nome = objArray.getJSONObject("name");
            pessoa.setNome(nome.getString("first"));
            pessoa.setSobrenome(nome.getString("last"));

            //Endereco tambem é um Objeto
            JSONObject endereco = objArray.getJSONObject("location");
            pessoa.setEndereco(endereco.getString("street"));
            pessoa.setEstado(endereco.getString("state"));
            pessoa.setCidade(endereco.getString("city"));

            //Imagem eh um objeto
            JSONObject foto = objArray.getJSONObject("picture");
            pessoa.setFoto(baixarImagem(foto.getString("large")));

            return pessoa;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap baixarImagem(String url) {
        try{
            URL endereco;
            InputStream inputStream;
            Bitmap imagem;
            endereco = new URL(url);
            inputStream = endereco.openStream();
            imagem = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return imagem;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}