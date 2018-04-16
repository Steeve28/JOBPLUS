package com.example.steeve.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.steeve.myapplication.R;
import com.example.steeve.myapplication.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Button btn_enregistrer;
    private EditText nom,email, password,telephone , adresse , domaineD,description;
    private String Strnom, Stremail, Strpassword, Strtel , Stradresse, Strdomaine, Strdescription, Image;
    private ImageView Flyer;
    private Bitmap bitmap;
    int PICK_IMAGE_REQUEST = 1;
    public static final String PatternMail = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    private RequestQueue queue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_enregistrer = (Button) findViewById(R.id.btn_enregistrer);
        Flyer = (ImageView) findViewById(R.id.ImgProfil);
        nom = (EditText) findViewById(R.id.Edt_nomEntreprise);
        email = (EditText) findViewById(R.id.Edt_emailEntreprise);
        password = (EditText) findViewById(R.id.Edt_passwordEntreprise);
        telephone = (EditText) findViewById(R.id.Edt_tel);
        adresse = (EditText) findViewById(R.id.Edt_adresse);
        domaineD = (EditText) findViewById(R.id.Edt_domaine);
        description = (EditText) findViewById(R.id.Edt_description);


        Flyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFileChooser();
            }
        });


        btn_enregistrer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){Image = getStringImage(bitmap);
                Strnom = nom.getText().toString();
                Stremail = email.getText().toString();
                Strpassword = password.getText().toString();
                Strtel = telephone.getText().toString();
                Stradresse = adresse.getText().toString();
                Strdomaine = domaineD.getText().toString();
                Strdescription = description.getText().toString();
                Pattern p = Pattern.compile(PatternMail);
                Matcher m = p.matcher(Stremail);
                if  (Strnom.equalsIgnoreCase("") || Stremail.equalsIgnoreCase("") || Strpassword.equalsIgnoreCase("")){
                    Toast.makeText(RegisterActivity.this, "Veuillez remplir tout les champs ", Toast.LENGTH_SHORT).show();
                }
                else  if  (Strnom.trim().length() <3) {
                    Toast.makeText(RegisterActivity.this, "Username trop court", Toast.LENGTH_SHORT).show();
                }
                else  if(Stremail.equalsIgnoreCase("")||!m.find()){
                    Toast.makeText(RegisterActivity.this, " Format Mail Invalide", Toast.LENGTH_SHORT).show();
                }
                else if (Strpassword.trim().length() < 3) {
                    Toast.makeText(RegisterActivity.this, "Password Trop Court", Toast.LENGTH_SHORT).show();
                }
                else {
                    RegisterMember();
                }
            }
        });

    }
    // Methode pour choisir la photo
    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Fin
    // Methode Bitmap de la photo
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    //Fin

    // onActivityResult de l image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                Flyer.setVisibility(View.VISIBLE);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                Flyer.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void RegisterMember() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.regEnt_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                    displayAlert(code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("Nom", Strnom);
                param.put("Email", Stremail);
                param.put("Password", Strpassword);
                param.put("Telephone", Strtel);
                param.put("Adresse", Stradresse);
                param.put("Domaine", Strdomaine);
                param.put("Description", Strdescription);
                param.put("Image", Image);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public void displayAlert(final String code) {
        if (code.equals("Req_Success")) {
            // Toast.makeText(Register.this, "SuccessFull", Toast.LENGTH_LONG).show();
            finish();
        } else if (code.equals("ReqFound")) {
        }
    }
}