package basico.android.cftic.edu.cajascolores;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegistroActivity extends AppCompatActivity {

    private boolean esCambioUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        esCambioUsuario = getIntent().getBooleanExtra(GlobalConst.PE_CAMBIAR_USUARIO,false);

        if (esCambioUsuario){
            this.setTitle("Cambiar Usuario");
            View vboton = (Button)findViewById(R.id.btnRegistrar);
            ((Button) vboton).setText("Cambiar");
        } else{
            this.setTitle("Registro Usuario");
        }

    }

    public String getEditText (View v) {
        EditText editText = (EditText)v;
        return editText.getText().toString();
    }

    public void accionRegistrar(View view) {

        View vEdit = findViewById(R.id.editUsuario);
        //Obtener el usuario introducido en el editor
        String usuario = getEditText(vEdit);

        if (!usuario.isEmpty()) {

            //Prepara el Intent
            Intent irMain = new Intent(this, MainActivity.class);

            if(esCambioUsuario){
                //Envía el nombre del usuario
                irMain.putExtra(GlobalConst.PE_CAMBIAR_USUARIO, usuario);
                setResult(RESULT_OK, irMain);
                finish();

            }else {
                //Registro en el archivo de preferencia el nombre del usuario
                Preferencias.setUsuario(this, usuario);

                startActivity(irMain); //Lanza el Intent
            }
        } else {
            Toast toast = Toast.makeText(this, "Debe introducir un Usuario antes de continuar", Toast.LENGTH_SHORT);
            toast.show();
            vEdit.setFocusable(true);
            vEdit.requestFocus();
        }
    }

    public void accionCancelar(View view) {
        if (esCambioUsuario) {
            //Prepara el Intent
            Intent irMain = new Intent(this, MainActivity.class);
            setResult(RESULT_CANCELED, irMain);
            finish();
        } else{
            //si el terminal es versión superior API 15
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.finishAffinity(); //cierra la app del todo. Esta función funciona a partir del API 16
            } else {
                //delegamos en la clase padre
                super.onBackPressed(); //finish();
            }
        }


    }



    public void tomarFoto (View v)
    {
        Log.d(GlobalConst.TAG_LOG, "QUIERO TOMAR UNA FOTO");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 500);
        }
    }

    public void seleccionarFoto (View v)
    {
        Log.d(GlobalConst.TAG_LOG, "QUIERO TOMAR UNA FOTO");
        Intent intentpidefoto = new Intent ();
        intentpidefoto.setAction(Intent.ACTION_PICK);
        intentpidefoto.setType("image/*");//TIPO MIME

        startActivityForResult(intentpidefoto, 30);


    }


    private void devolverFoto (int resultCode, @Nullable Intent data){
        switch (resultCode)
        {
            case RESULT_OK:Log.d(GlobalConst.TAG_LOG, "Tiró la foto bien");
                try {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    //Log.d(GlobalConst.TAG_LOG, "URI = " +data.getData().toString());
                    ImageView im = (ImageView) findViewById(R.id.imageView);
                    im.setImageBitmap(thumbnail);
                }catch (Throwable t)
                {
                    Log.e(GlobalConst.TAG_LOG, "ERROR AL SETEAR LA FOTO", t);
                }
                break;

            case RESULT_CANCELED:Log.d(GlobalConst.TAG_LOG, "Canceló la foto");
                break;

        }
    }

    private void seleccionarFoto (int resultCode, @Nullable Intent data){
        switch (resultCode)
        {
            case RESULT_OK:Log.d(GlobalConst.TAG_LOG, "Seleccionó foto ok");
                Uri uri = data.getData();

                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ImageView imageView = (ImageView)findViewById(R.id.imageView);
                    imageView.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case RESULT_CANCELED:Log.d(GlobalConst.TAG_LOG, "Canceló la foto");
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode){
            case GlobalConst.REQUEST_IMAGE_CAPTURE: devolverFoto(resultCode,data);
            break;

            case GlobalConst.REQUEST_IMAGE_GALLERY: seleccionarFoto(resultCode,data);
            break;
        }
    }

}
