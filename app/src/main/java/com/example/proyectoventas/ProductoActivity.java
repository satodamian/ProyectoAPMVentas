package com.example.proyectoventas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoventas.model.Producto;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductoActivity extends AppCompatActivity {

    //TextView emailTextView;
    //MaterialButton logoutButton;

    private List<Producto> listaProducto = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterProducto;

    EditText nombreP, descripcionP, precioP;
    ListView datosP;

    //Subir Foto
    MaterialButton subirFoto;
    StorageReference mStorage;
    static final int GALLERY_INTENT = 1;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Producto productoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        //emailTextView = findViewById(R.id.emailTextView);
        //logoutButton = findViewById(R.id.logoutButton);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //if (user != null) {
        //    emailTextView.setText(user.getEmail());
        //}
//
        //logoutButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        FirebaseAuth.getInstance().signOut();
        //        Intent intent = new Intent(ProductoActivity.this, LoginActivity.class);
        //        startActivity(intent);
        //        finish();
        //    }
        //});
        nombreP = findViewById(R.id.nombreProducto);
        descripcionP = findViewById(R.id.descripcionProducto);
        precioP = findViewById(R.id.precioProducto);

        //Subir Foto
        subirFoto = findViewById(R.id.subirFoto);
        mStorage = FirebaseStorage.getInstance().getReference();

        
        datosP = findViewById(R.id.datosProducto);
        inicializarFirebase();
        listarDatos();

        datosP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productoSeleccionado = (Producto) parent.getItemAtPosition(position);
                nombreP.setText(productoSeleccionado.getNombreProducto());
                descripcionP.setText(productoSeleccionado.getDescripcionProducto());
                precioP.setText(productoSeleccionado.getPrecioProducto());
            }
        });


        //Subir Foto
        subirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            StorageReference filePath = mStorage.child("fotos").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ProductoActivity.this, "Foto subida", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    

    private void listarDatos() {
        databaseReference.child("Producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProducto.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Producto producto = objSnapshot.getValue(Producto.class);
                    listaProducto.add(producto);

                    arrayAdapterProducto = new ArrayAdapter<Producto>(ProductoActivity.this, android.R.layout.simple_list_item_1, listaProducto);
                    datosP.setAdapter(arrayAdapterProducto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_productos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nombre = nombreP.getText().toString();
        String descripcion = descripcionP.getText().toString();
        String precio = precioP.getText().toString();

        switch (item.getItemId()) {
            case R.id.icon_agregar:{
                if (nombre.equals("") || descripcion.equals("") || precio.equals("")) {
                    validacion();
                } else {
                    Producto producto = new Producto();
                    producto.setUid(UUID.randomUUID().toString());
                    producto.setNombreProducto(nombre);
                    producto.setDescripcionProducto(descripcion);
                    producto.setPrecioProducto(precio);
                    databaseReference.child("Producto").child(producto.getUid()).setValue(producto);
                    Toast.makeText(this, "Agregar", Toast.LENGTH_LONG).show();
                    limpiarCaja();
                }
                break;
            }
            case R.id.icon_actualizar:{
                Producto producto = new Producto();
                producto.setUid(productoSeleccionado.getUid());
                producto.setNombreProducto(nombreP.getText().toString().trim());
                producto.setDescripcionProducto(descripcionP.getText().toString().trim());
                producto.setPrecioProducto(precioP.getText().toString().trim());
                databaseReference.child("Producto").child(producto.getUid()).setValue(producto);
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_LONG).show();
                limpiarCaja();
                break;
            }
            case R.id.icon_eliminar:{
                Producto producto = new Producto();
                producto.setUid(productoSeleccionado.getUid());
                databaseReference.child("Producto").child(producto.getUid()).removeValue();
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_LONG).show();
                limpiarCaja();
                break;
            }
            default:break;
        }

        return true;
    }

    private void limpiarCaja() {
        nombreP.setText("");
        descripcionP.setText("");
        precioP.setText("");
    }

    private void validacion() {
        String nombre = nombreP.getText().toString();
        String descripcion = descripcionP.getText().toString();
        String precio = precioP.getText().toString();

        if (nombre.equals("")) {
            nombreP.setError("Dato requerido");
        } else if (descripcion.equals("")) {
            descripcionP.setError("Dato requerido");
        } else if (precio.equals("")) {
            precioP.setError("Dato requerido");
        }
    }
}