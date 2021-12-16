package com.ad.saj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private TextView tvResult;
    private EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");
        initialize();
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
            viewSettings();
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
                 if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                     //permiso concedido
                     search();
                 }
                 break;
        }
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
                            ,getString(R.string.message));
        requestPermission();
    }

    //inicializamos los componentes
    private void initialize() {
        Button btSearch = findViewById(R.id.btSearch);
        tvResult = findViewById(R.id.tvResult);
        etPhone = findViewById(R.id.etPhone);
        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String lastSearch = preferences.getString("last search", "");

        if(!lastSearch.isEmpty()) {
            etPhone.setText(lastSearch);
        }

        btSearch.setOnClickListener(view -> searchIfPermitted());
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
        tvResult.setText("");
        String phone = etPhone.getText().toString();
        phone = searchFormat(phone);

        String cleanSearch = phone.replace("%", "");
        SharedPreferences preferenciasActividad = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferenciasActividad.edit();
        editor.putString("last search", cleanSearch);
        editor.commit();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this );
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString("ved", "v");

        tvResult.append("Last search: "+sharedPreferences.getString("last search", "")+"\n\n");

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] proyeccion = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion = ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?";
        String[] argumentos = new String[]{ phone };//etPhone.getText().toString()};
        String orden = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        String[] columnas = cursor.getColumnNames();
        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre, numero;
        while(cursor.moveToNext()) {
            nombre = cursor.getString(columnaNombre);
            numero = cursor.getString(columnaNumero);
            tvResult.append("nombre: "+nombre+"\n numero: "+numero+"\n\n");
        }
    }

    private String searchFormat(String phone) {
        String newString = "";
        for (char ch: phone.toCharArray()) {
            newString += ch + "%";
        }
        return newString;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showRationaleDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    //no hago nada
                })
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> requestPermission());
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