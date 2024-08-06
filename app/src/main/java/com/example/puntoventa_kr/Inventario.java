package com.example.puntoventa_kr;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Inventario extends AppCompatActivity {

    Button NCat, OpcP, Venta;
    Spinner Spin;
    TextView Prod;
    List<String> ListaProd;
    ArrayAdapter<String> adapter;
    String Code, categ;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        NCat = findViewById(R.id.NCat);
        OpcP = findViewById(R.id.OPcProd);
        Venta = findViewById(R.id.Venta);
        Spin = findViewById(R.id.SpinnerP);
        Prod = findViewById(R.id.Prod);

        ListaProd = leerListaProd();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ListaProd);
        Spin.setAdapter(adapter);



        Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categ = Spin.getSelectedItem().toString();
                String response = Leer();
                String[] res = response.split("&");
                if(res[0].equals("success"))
                    Prod.setText(res[1]);
                else
                    Prod.setText(res[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        OpcP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoProd();
            }
        });

        NCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoCat();
            }
        });

        Venta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Venta.class);
                startActivity(i);
            }
        });

    }

    private void DialogoCat(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.categoria, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button Acep = view.findViewById(R.id.Aceptar);
        Button Can = view.findViewById(R.id.Cancelar);
        EditText Categor = view.findViewById(R.id.Categor);

        Acep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NP = Categor.getText().toString();
                if (!NP.isEmpty()) {
                    ListaProd.add(NP);
                    adapter.notifyDataSetChanged();
                    guardarListaProd(ListaProd);
                } else {
                    Toast.makeText(Inventario.this, "Por favor, ingrese un dato", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(Inventario.this, "Categoría registrada", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        Can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Inventario.this, "Cancelado", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

    }

    private void DialogoProd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.producto, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText Cod = view.findViewById(R.id.Cod);
        EditText Nom = view.findViewById(R.id.Nom);
        EditText Exis = view.findViewById(R.id.Exis);
        EditText Prec = view.findViewById(R.id.Prec);
        Spinner Cat = view.findViewById(R.id.Cat);

        Cat.setAdapter(adapter);

        Button Consulta = view.findViewById(R.id.Consulta);
        Button Guardar = view.findViewById(R.id.Guardar);
        Button Actualizar = view.findViewById(R.id.Actualizar);
        Button Eliminar = view.findViewById(R.id.Eliminar);
        Button Regresar = view.findViewById(R.id.Regresar);

        Consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Cod = findViewById(R.id.Cod);
                Code = String.valueOf(Integer.parseInt(Cod.getText().toString()));
                String response = Consultar();
                String[] res = response.split("&");
                if(res[0].equals("success"))
                    Prod.setText(res[1]);
                else
                    Prod.setText(res[1]);
            }
        });

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer codig = Integer.valueOf(Cod.getText().toString());
                String nom = Nom.getText().toString();
                String cat = Cat.getSelectedItem().toString();
                Integer exis = Integer.valueOf(Exis.getText().toString());
                Integer prec = Integer.valueOf(Prec.getText().toString());

                if(codig.equals("")||nom.equals("")||cat.equals("")||exis.equals("")||prec.equals("")){
                    Toast.makeText(getApplicationContext(),"Debes llenar todos los campos",Toast.LENGTH_LONG).show();
                }else{
                    String response = insertar(codig, nom, cat, exis, prec);
                    String[] res = response.split("-");
                    if(res[0].equals("Exitoso"))
                        Toast.makeText(getApplicationContext(),res[1],Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(),res[1],Toast.LENGTH_LONG).show();
                }
                Toast.makeText(Inventario.this, "Producto nuevo guardado", Toast.LENGTH_LONG).show();
            }
        });

        Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Inventario.this, "Producto actualizado", Toast.LENGTH_LONG).show();
            }
        });

        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoComp();

                Toast.makeText(Inventario.this, "Eliminado con éxito", Toast.LENGTH_LONG).show();
            }
        });

        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private String insertar(Integer codig, String nom, String cat, Integer exis, Integer prec) {
        ClaseBD admin = new ClaseBD(getApplicationContext(),"PuntoVenta",null,1);
        ContentValues registro =new ContentValues();
        SQLiteDatabase reader = admin.getReadableDatabase();
        try {
            registro.put("Codigo",codig);
            registro.put("Nombre",nom);
            registro.put("Categoria", cat);
            registro.put("Existencias", exis);
            registro.put("Precio", prec);
            reader.insert("Productos",null, registro);
            reader.close();
            return "Exitoso-Registro insertado con exito!!!!";

        }catch (Exception e){
            return "Error-"+e.getMessage();
        }

    }

    private void DialogoComp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Seguro que desea eliminar este producto?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Inventario.this, "Presionó aceptar", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Inventario.this, "Presionó cancelar", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();

    }

    private String Consultar() {
        ClaseBD admin = new ClaseBD(getApplicationContext(),"PuntoVenta",null,1);
        SQLiteDatabase reader = admin.getReadableDatabase();
        try {
            Cursor cursor = reader.rawQuery("Select * from Productos where Codigo = ?",new String[]{Code});
            String response = "";

            while(cursor.moveToNext()) {
                response +=  cursor.getInt(0)+"        "+cursor.getString(1)+"       "+cursor.getString(2)+"     "+cursor.getString(3)+"     "+cursor.getString(4)+"     "+cursor.getString(5)+"\n";
            }
            cursor.close();
            return "success&"+response;
        }catch (Exception e){
            return "error&"+e.getMessage();
        }
    }

    private String Leer() {
        ClaseBD admin = new ClaseBD(getApplicationContext(),"PuntoVenta",null,1);
        SQLiteDatabase reader = admin.getReadableDatabase();
        try {
            Cursor cursor = reader.rawQuery("Select * from Productos where Categoria = ?",new String[]{categ});
            String response = "Código          Nombre           Categoría             Existencias          Precio               Fecha_reg\n";

            while(cursor.moveToNext()) {
                response +=  "    "+cursor.getInt(0)+"               "+cursor.getString(1)+"               "+cursor.getString(2)+"                   "+cursor.getString(3)+"                     "+cursor.getString(4)+"              "+cursor.getString(5)+"\n";
            }
            cursor.close();
            return "success&"+response;
        }catch (Exception e){
            return "error&"+e.getMessage();
        }
    }

    private void guardarListaProd(List<String> ListaProd) {
        SharedPreferences preferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Convertir la lista en una cadena separada por comas
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : ListaProd) {
            stringBuilder.append(item).append(",");
        }

        // Eliminar el último delimitador si la lista no está vacía
        if (!ListaProd.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }

        editor.putString("Lista_prod", stringBuilder.toString());
        editor.commit();
    }

    private List<String> leerListaProd() {
        SharedPreferences preferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String listaProdString = preferences.getString("Lista_prod", "");
        List<String> ListaProd = new ArrayList<>();

        if (!listaProdString.isEmpty()) {
            String[] items = listaProdString.split(",");
            ListaProd.addAll(Arrays.asList(items));
        }

        return ListaProd;
    }


}