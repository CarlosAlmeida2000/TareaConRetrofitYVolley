package com.example.tareaconretrofityvolley;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tareaconretrofityvolley.Interface.interfaceRetrofit;
import com.example.tareaconretrofityvolley.Model.KushkiR;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Spinner spListaBancos;
    List<String> ListBancos;
    ArrayAdapter comboAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void listarConRetrofit(View view) {
        final ProgressDialog sms = ProgressDialog.show(this, "Por favor espere....","Actualizando", false, false    );
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api-uat.kushkipagos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        interfaceRetrofit api = retrofit.create(interfaceRetrofit.class);

        Call<List<KushkiR>> call = api.getDatos();

        call.enqueue(new Callback<List<KushkiR>>() {
            @Override
            public void onResponse(Call<List<KushkiR>> call, Response<List<KushkiR>> response) {
                if(!response.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), response.code(),Toast.LENGTH_LONG).show();
                    return;
                }
                List<KushkiR> kushkiList = response.body();
                ListBancos = new ArrayList<>();
                for (KushkiR data: kushkiList)
                {
                    ListBancos.add(data.getCode()+".- "+ data.getName());
                }
                spListaBancos = (Spinner) findViewById(R.id.spListarConRetrofit);
                sms.dismiss();
                LlenarCombo(spListaBancos);
            }

            @Override
            public void onFailure(Call<List<KushkiR>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public  void listarConVolley(View view){

        final ProgressDialog sms = ProgressDialog.show(this, "Por favor espere....","Actualizando", false, false    );
        JsonArrayRequest jsonArrObjReq = new JsonArrayRequest(Request.Method.GET, "https://api-uat.kushkipagos.com/transfer/v1/bankList",
        null, response -> {
            try
            {
                sms.dismiss();
                ListBancos = new ArrayList<>();
                for(int i=0; i < response.length(); i++){
                    JSONObject js = new JSONObject(response.get(i).toString());
                    ListBancos.add(js.getString("code")+".- "+ js.getString("name"));
                }
                spListaBancos = (Spinner) findViewById(R.id.spListarConVolley);
                LlenarCombo(spListaBancos);
            }
            catch (JSONException ex){
                Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
            }
        ){
            public Map getHeaders(){
                HashMap hd = new HashMap();
                hd.put("Public-Merchant-Id","8376ea5f58f44f2fb3304faddcfd9660");
                return hd;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(jsonArrObjReq);
    }
    public  void LlenarCombo(Spinner spListaBancos){
        comboAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ListBancos);
        spListaBancos.setAdapter(comboAdapter);
    }
}