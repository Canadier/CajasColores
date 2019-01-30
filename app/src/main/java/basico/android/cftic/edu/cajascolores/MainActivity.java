package basico.android.cftic.edu.cajascolores;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int TOTALCAJAS=12;
    private int contador;
    private int colortocado;
    private long startTime;
    private long endTime;
    private String usuario;
    private boolean juegoIniciado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//flecha por defecto
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.atras); //flecha personalizada

        juegoIniciado = false;
        contador = 0;
        colortocado = getResources().getColor(R.color.colortocado);

        usuario = Preferencias.getUsuario(this);
        //Determina si existe un usuario registrado
        if (usuario.isEmpty()){
            //Prepara el Intent
            Intent irRegistro = new Intent(this, RegistroActivity.class);
            startActivity(irRegistro); //Lanza el Intent
        } else {
            actualizarSubTitulo(usuario);
        }

    }

    /**
     * Actualiza el contenido del subtitulo
     * @param usuario
     */
    private void actualizarSubTitulo(String usuario){
        //this.setTitle("Hola "+usuario);
        getSupportActionBar().setSubtitle("Hola "+usuario);
    }

    /**
     * Poner en marcha el cronometro
     */
    private void initCrono (){
        Chronometer c = findViewById(R.id.crono);
        c.setBase(SystemClock.elapsedRealtime());
        c.start();
    }

    /**
     * Parar el cronometro
     */
    private void pararCrono ()
    {
        Chronometer c = findViewById(R.id.crono);
        c.stop();
    }

    //Obtiene el tiempo transcurrido en segundos
    private Double tiempoTranscurrido (long start, long end){
        Double segundos = (end-start)/1000.0;
        return segundos;
    }

    private void ocultarActionBar ()
    {
        getSupportActionBar().hide();
    }

    private void informarConToast (Double tiempo_total, String nombre)
    {
        Toast toast = Toast.makeText(this, "Nombre = " + nombre + " "+tiempo_total+" segundos", Toast.LENGTH_SHORT);
        toast.show();//informo
    }

    private void informarConSnackBar (Double tiempo_total, String nombre)
    {
        View v = findViewById(R.id.fab);
        Snackbar s =  Snackbar.make(v, "USUARIO " + nombre, Snackbar.LENGTH_LONG);
        s.setAction("TIEMPO " + tiempo_total, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(GlobalConst.TAG_LOG, "Esto se ejecuta al tocar el snack");
            }
        });
        s.show();
    }

    private void reiniciarJuego(){

        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("A T E N C I O N");
        dialogo1.setMessage("¿ Desea una nueva partida ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });
        dialogo1.show();
    }

    public void aceptar() {
        Toast t=Toast.makeText(this,"Reiniciando la partida", Toast.LENGTH_SHORT);
        t.show();

        //reiniciarActivity(this);
        recreate();

    }

    public void cancelar() {
        //si el terminal es versión superior API 15
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity(); //cierra la app del todo. Esta función funciona a partir del API 16
        } else {
            //delegamos en la clase padre
            super.onBackPressed(); //finish();
        }
    }


    private void cerrar(View view){

        pararCrono();

        endTime = System.currentTimeMillis();
        Double tiempo = tiempoTranscurrido(startTime,endTime);

        //informarConToast(tiempo, usuario);
        informarConSnackBar(tiempo, usuario);


        long msegundos = endTime-startTime;

        topRecordTiempo(msegundos);

        Puntuacion puntuacion = new Puntuacion(usuario,msegundos);
        Preferencias.setRecord(this,puntuacion);

        reiniciarJuego();

    }

    //Método de cambiar el color
    public void cambiaColor(View v)
    {
        if (juegoIniciado) {
            Log.d(GlobalConst.TAG_LOG, "TOCÓ CAJA");

            //Determina si la caja ya se le cambió el color
            boolean tocado = (v.getTag() != null) && (v.getTag().equals(colortocado));

            if (!tocado) {
                v.setBackgroundColor(colortocado);
                //forma alternativa de obtener el color
                //int color2 = ContextCompat.getColor(this, R.color.tocado);
                //forma alternativa de obtener el color
                //int color = ResourcesCompat.getColor(getResources(), R.color.tocado, null);
                //v.setBackgroundColor(color);

                v.setTag(colortocado);

                contador++;

            }

            if (contador == 12) {
                Log.d(GlobalConst.TAG_LOG, "SE ACABO");
                cerrar(v);
            }
        }
    }

    /**
     * Metodo que sirve para definir el menú superior
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sup,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Recibimos el evento sobre una opción del menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO permitir cambiar de nombre de usuario
        int id_item = item.getItemId();
        switch (id_item) {
            case R.id.cambiarUsuario:
                Log.d(GlobalConst.TAG_LOG,"Tocó cambiar de usuario");
                Intent intent = new Intent(this, RegistroActivity.class);
                intent.putExtra(GlobalConst.PE_CAMBIAR_USUARIO, true);
                startActivityForResult(intent, GlobalConst.RC_CAMBIAR_USUARIO);
                //startActivity(new Intent(this, RegistroActivity.class));
                break;

            case android.R.id.home:
                Log.d(GlobalConst.TAG_LOG, "Tocada opción salir");
                super.onBackPressed();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.d(GlobalConst.TAG_LOG, "Ha vuelto");

        switch (requestCode){
            case GlobalConst.RC_CAMBIAR_USUARIO:
                Log.d(GlobalConst.TAG_LOG, "Ha vuelto Cambiar Usuario");
                if (resultCode == RESULT_OK)
                {
                    usuario = data.getStringExtra(GlobalConst.PE_CAMBIAR_USUARIO);
                    Preferencias.setUsuario(this,usuario);
                    actualizarSubTitulo(usuario);
                }

                break;

        } //switch

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void accionFab(View view) {
        ocultarActionBar();

        FloatingActionButton actBtn = (FloatingActionButton)view;
        actBtn.hide();

        startTime = System.currentTimeMillis();
        initCrono();
        juegoIniciado = true;

    }


    private void topRecordTiempo (long tiempo){

        List<Puntuacion> list = Preferencias.cargarListaRecord(this);

        list.sort(Comparator.comparing(Puntuacion::getTiempo));

        if (tiempo < list.get(0).getTiempo()){

        }

    }
}
