package com.ad.saj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ad.saj.myapplication.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private final int CONTACTS_PERMISSION = 1;
    private final String TAG = "sdfbdsi";

    private Button btSearch; // = findViewById(); eso no se hace aqui aun no existe el layout
    private TextView tvResult;
    private EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");//verbose
        initialize();
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_ajustes){
            viewSettings();// aqui habia algo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "onRequestPermissions");
        switch (requestCode){
            case CONTACTS_PERMISSION:
                 if (grantResults[0] > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                     //permiso concedido
                     search();
                 } else {
                     //sin permiso
                 }
                 break;
        }
        //requestCode
        //permissions
        //grantResults
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    //EXPLICA AL USUARIO POR QUE NECESITA LOS PERMISOS.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void explain() {
        showRationaleDialog(getString(R.string.title)
                            ,getString(R.string.message)
                            ,Manifest.permission.READ_CONTACTS
                            , CONTACTS_PERMISSION);
        requestPermission();
    }

    //inicializamos los componentes
    private void initialize() {
        btSearch = findViewById(R.id.btSearch);
        tvResult = findViewById(R.id.tvResult);
        etPhone = findViewById(R.id.etPhone);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String phoneNumber = String.valueOf(etPhone.getText());
                searchIfPermitted();


            }
        });
    }

    //PETICION DE PERMISO.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION);
    }

    //BUSQUEDA DE CONTACTOS.
    //ContentProvider Proveedor de contenidos
    //ContentResolver Consultor de contenidos
    private void search() {
        // Queries the user dictionary and returns results
        /*
        Cursor  cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,                                               // The content URI of the words table
                new String[] {"projection"},                                                    // The columns to return for each row (las columnas que queremos ver)
                "campo 1 = ?and campo2 > ? and campo 3 = ?",                                     // Selection criteria (where) CONSULTA PREPARADA
                new String[] {"pepe","4","23"},                                               // Selection criteria TANTOS COMO CAMPOS PREPARADOS HALLA
                "campo5,campo3,campo4");                                                        // The sort order for the returned rows


        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        seleccion = null;
        argumentos = null;
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        String[] columnas = cursor.getColumnNames();
        for (String c : columnas){
            Log.v(TAG, c);// saca las cosas por el logcat
        }

        String displayName;
        int columna = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        while (cursor.moveToNext()){
            displayName = cursor.getString(columna);
            Log.v(TAG, displayName);
        }

*/


        Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion2[] = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.DISPLAY_NAME};
        String seleccion2 =ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?";                    // ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos2[] = new String[]{etPhone.getText().toString()};                //new String[]{id+""};
        String orden2 = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor2 = getContentResolver().query(uri2, proyeccion2, seleccion2, argumentos2, orden2);
        String[] columnas2 = cursor2.getColumnNames();

        for (String c : columnas2){
            Log.v(TAG, c);// saca las cosas por el logcat
        }

        int columnaNombre = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre,numero;

        while (cursor2.moveToNext()){
            nombre = cursor2.getString(columnaNombre);
            numero = cursor2.getString(columnaNumero);
            //Log.v(TAG, c)


            for (String c : columnas2){
                int pos = cursor2.getColumnIndex(c);
                String valor = cursor2.getString(pos);
                Log.v(TAG ,pos + " " + c +" " + valor);
                tvResult.append("nombre: "+nombre+" numero: "+numero+"\n");
            }
        }

    }

    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //SI LA VERSION DE ANDROID ES POSTERIOR A 5.0
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_CONTACTS) ==
                    PackageManager  .PERMISSION_GRANTED) {
                //YA TENGO EL PERMISO
                search();

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) { //tengo que darle la explicacio (2ª vaz que se pide el permiso)
                explain();
            } else { //pido permiso por 1ª vez
                requestPermission();

            }
        } else {
            //LA VERSION DE ANDROID ES ANTERIOR A LA 6.0
            //YA TENGO EL PERMISO
            search();
        }
    }

    private void showRationaleDialog(String title, String message, String permission, int requestCode){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //no hago nada
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermission();
                    }
                });
        builder.create().show();

    }
    //abre la actividad de settings
    private  void viewSettings(){
        /*
        * intent - intencion NO INTENTO
        * intenciones explicitas o implicitas
        *
        * esta es explicita, lo que hace es definir que quiere ir del contexto actual a otro que se crea con
        * la clase SettingsActivity
        *
        *
        * */

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);//Abre una actividad nueva a partir del intent
    }

}