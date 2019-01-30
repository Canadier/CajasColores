package basico.android.cftic.edu.cajascolores;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Clase que gestiona los ficheros de preferencias de la aplicación
 */
public class Preferencias {
    //Nombre del  fichero donde se almacenan los tiempos
    private static final String NOMB_FICH_RECORD ="record";
    //Nombre del  fichero donde se almecenan datos de control:Usuario,contadores, etc...
    private static final String NOMB_FICH_CONTROL="control"; //Nombre del fichero

    //Clave del nombre del usuario actual en curso
    private static final String CLAVE_USUARIO="usuario";
    //Clave del contador de tiempos almacenados
    private static final String CLAVE_TOTAL_RECORD="total_record";

    /**
     * Almacena en el fichero de preferencias el usuario actual
     * @param context Contexto de la aplicación
     * @param usuario Usuario a registrar
     */
    public static void setUsuario (Context context, String usuario){
         //Obtiene la referencia al fichero de preferencias
        SharedPreferences preferences = context.getSharedPreferences(NOMB_FICH_CONTROL,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString(CLAVE_USUARIO,usuario);
        editor.commit();
    }

    /**
     * Obtiene el nombre del usuario actual registrado en el fichero de preferencias
     * @param context Contexto de la aplicación
     * @return Nombre del usuario actual. Si no existe un usuario devuleve una cadena vacía.
     */
    public static String getUsuario(Context context) {
        String usuario=null;

        //Obtiene la referencia al fichero de preferencias
        SharedPreferences preferences = context.getSharedPreferences(NOMB_FICH_CONTROL,Context.MODE_PRIVATE);
        usuario = preferences.getString(CLAVE_USUARIO,"");

        return usuario;
    }

    /**
     * Almacena en el fichero de preferencias el total de records registrados
     * @param context Contexto de la aplicación
     * @param total Total de records registrados
     */
    public static void setTotalRecord (Context context, long total){
        //Obtiene la referencia al fichero de preferencias
        SharedPreferences preferences = context.getSharedPreferences(NOMB_FICH_CONTROL,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putLong(CLAVE_TOTAL_RECORD,total);
        editor.commit();
    }

    /**
     * Obtiene el total de records registrados en el fichero de preferencias
     * @param context Contexto de la aplicación
     * @return Total de records registrados
     */
    public static long getTotalRecord(Context context) {
        long total=0;

        //Obtiene la referencia al fichero de preferencias
        SharedPreferences preferences = context.getSharedPreferences(NOMB_FICH_CONTROL,Context.MODE_PRIVATE);
        total = preferences.getLong(CLAVE_TOTAL_RECORD,0);

        return total;
    }

    /**
     * Incrementa el valor del total de records actualizandolo en el fichero de preferencias
     * @param context Contexto de la aplicación
     */
    public static void incTotalRecord(Context context){
        long total = 0;
        total = getTotalRecord(context);
        total++;
        setTotalRecord(context,total);
    }

    /**
     * Registra en el fichero de preferencias un nuevo record
     * @param context Contexto de la aplicación
     * @param puntuacion Objeto conteniendo el usuario y el tiempo realizado
     */
    public static void setRecord(Context context, Puntuacion puntuacion){
        //Obtiene la referencia al fichero de preferencias
        SharedPreferences preferences = context.getSharedPreferences(NOMB_FICH_RECORD,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        Gson gson = new Gson();
        Long contador = getTotalRecord(context);
        editor.putString(contador.toString(),gson.toJson(puntuacion));
        editor.commit();
        incTotalRecord(context);
    }

    /**
     * Carga un record especifico del fichero de preferencias
     * @param context Contexto de la aplicación
     * @param indexRecord Posición de la lista de record
     * @return Record de la posición indicada.
     */
    public static Puntuacion getRecord (Context context, Long indexRecord){
        Puntuacion res = null;
        //Obtiene la referencia al fichero de preferencias
        SharedPreferences preferences = context.getSharedPreferences(NOMB_FICH_RECORD,Context.MODE_PRIVATE);

        String str = preferences.getString(indexRecord.toString(),"");
        if (!str.isEmpty()){
            res = new Puntuacion();
            Gson gson = new Gson();
            res = gson.fromJson(str,Puntuacion.class);
        }

        return res;

    }

    /**
     * Carga del fichero de preferencias la lista de records registrados
     * @param context Contexto de la aplicación
     * @return Lista de records registrados
     */
    public static List<Puntuacion> cargarListaRecord (Context context){
        List<Puntuacion> lp=null;
        String puntuacionEnCurso = null;
        Gson gson = null;
        Puntuacion puntuacionAux = null;

        //Obtiene la referencia al fichero de preferencias
        SharedPreferences preferences = context.getSharedPreferences(NOMB_FICH_RECORD,Context.MODE_PRIVATE);

        Map<String,String> mapa_puntuaciones = (Map<String,String>)preferences.getAll();//carga todo el contenido en un mapa
        Set<String> conj_claves = mapa_puntuaciones.keySet();//obtiene las claves para recorrer el mapa

        gson = new Gson();
        lp = new ArrayList<Puntuacion>();

        for (String clave:conj_claves){
            puntuacionEnCurso = mapa_puntuaciones.get(clave);//obtengo el registro puntuacion en formato JSON
            puntuacionAux = gson.fromJson(puntuacionEnCurso,Puntuacion.class);//paso de JSON a Puntuaciones
            lp.add(puntuacionAux);//agrego a lista
        }
        return lp;
    }
}
