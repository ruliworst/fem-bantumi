package es.upm.miw.bantumi;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import es.upm.miw.bantumi.model.BantumiViewModel;

public class JuegoBantumi {

    public static final int NUM_POSICIONES = 14;
    // Posiciones 0-5: campo jugador 1
    // Posición 6: depósito jugador 1
    // Posiciones 7-12: campo jugador 2
    // Posición 13: depósito jugador 2

    private final BantumiViewModel bantumiViewModel;

    // Turno juego
    public enum Turno {
        turnoJ1, turnoJ2, Turno_TERMINADO
    }

    // Número inicial de semillas
    private final int numInicialSemillas;

    private final Context context;

    private final Activity activity;

    /**
     * Constructor
     *
     * Inicializa el modelo sólo si éste está vacío
     *
     * @param turno especifica el turno inicial <code>[Turno.turnoJ1 || Turno.turnoJ2]</code>
     * @param numInicialSemillas Número de semillas al inicio del juego
     */
    public JuegoBantumi(
            Context context,
            Activity activity,
            BantumiViewModel bantumiViewModel,
            Turno turno,
            int numInicialSemillas) {
        this.context = context;
        this.activity = activity;
        this.bantumiViewModel = bantumiViewModel;
        this.numInicialSemillas = numInicialSemillas;
        if (campoVacio(Turno.turnoJ1) && campoVacio(Turno.turnoJ2)) { // Inicializa sólo si está vacío!!!
            inicializar(turno);
        }
    }

    /**
     * @param pos posición
     * @return Número de semillas en el hueco <i>pos</i>
     */
    public int getSemillas(int pos) {
        return bantumiViewModel.getNumSemillas(pos).getValue();
    }

    /**
     * Asigna el número de semillas a una posición
     *
     * @param pos posición
     * @param valor número de semillas
     */
    public void setSemillas(int pos, int valor) {
        bantumiViewModel.setNumSemillas(pos, valor);
    }

    /**
     * Inicializa el estado del juego (almacenes vacíos y campos con semillas)
     *
     * @param turno especifica el turno inicial <code>[Turno.turnoJ1 || Turno.turnoJ2]</code>
     */
    public void inicializar(Turno turno) {
        setTurno(turno);
        for (int i = 0; i < NUM_POSICIONES; i++)
            setSemillas(
                    i,
                    (i == 6 || i == 13) // Almacén??
                            ? 0
                            : numInicialSemillas
            );
    }

    /**
     * Recoge las semillas en <i>pos</i> y realiza la siembra
     *
     * @param pos posición escogida [0..13]
     */
    public void jugar(int pos) {
        if (pos < 0 || pos >= NUM_POSICIONES)
            throw new IndexOutOfBoundsException(String.format("Posición (%d) fuera de límites", pos));
        if (getSemillas(pos) == 0
                || (pos < 6 && turnoActual() != Turno.turnoJ1)
                || (pos > 6 && turnoActual() != Turno.turnoJ2)
        )
            return;
        Log.i("MiW", String.format("jugar(%02d)", pos));

        // Recoger semillas en posición pos
        int nSemillasHueco, numSemillas = getSemillas(pos);
        setSemillas(pos, 0);

        // Realizar la siembra
        int nextPos = pos;
        while (numSemillas > 0) {
            nextPos = (nextPos + 1) % NUM_POSICIONES;
            if (turnoActual() == Turno.turnoJ1 && nextPos == 13) // J1 salta depósito jugador 2
                nextPos = 0;
            if (turnoActual() == Turno.turnoJ2 && nextPos == 6) // J2 salta depósito jugador 1
                nextPos = 7;
            nSemillasHueco = getSemillas(nextPos);
            setSemillas(nextPos, nSemillasHueco + 1);
            numSemillas--;
        }

        // Si acaba en hueco vacío en propio campo -> recoger propio + contrario
        if (getSemillas(nextPos) == 1
                && ((turnoActual() == Turno.turnoJ1 && nextPos < 6)
                    || (turnoActual() == Turno.turnoJ2 && nextPos > 6 && nextPos < 13))
        ) {
            int posContrario = 12 - nextPos;
            Log.i("MiW", "\trecoger: turno=" + turnoActual() + ", pos=" + nextPos + ", contrario=" + posContrario);
            int miAlmacen = (turnoActual() == Turno.turnoJ1) ? 6 : 13;
            setSemillas(
                    miAlmacen,
                    1 + getSemillas(miAlmacen) + getSemillas(posContrario)
            );
            setSemillas(nextPos, 0);
            setSemillas(posContrario, 0);
        }

        // Si es fin -> recolectar
        if (campoVacio(Turno.turnoJ1) || campoVacio(Turno.turnoJ2)) {
            recolectar(0);
            recolectar(7);
            setTurno(Turno.Turno_TERMINADO);
        }

        // Determinar turno siguiente (si es depósito propio -> repite turno)
        if (turnoActual() == Turno.turnoJ1 && nextPos != 6)
            setTurno(Turno.turnoJ2);
        else if (turnoActual() == Turno.turnoJ2 && nextPos != 13)
            setTurno(Turno.turnoJ1);
        Log.i("MiW", "\t turno = " + turnoActual());
    }

    /**
     * @return Indica si el juego ha finalizado (todas las semillas están en los depósitos)
     */
    public boolean juegoTerminado() {
        return (turnoActual() == Turno.Turno_TERMINADO);
    }

    /**
     * @param turno Turno
     * @return Determina si el campo del jugador especificado por <i>turno</i> está vacío
     */
    private boolean campoVacio(Turno turno) {
        boolean vacio = true;
        int inicioCampo = (turno == Turno.turnoJ1) ? 0 : 7;
        for (int i = inicioCampo; i < inicioCampo + 6; i++)
            vacio = vacio && (getSemillas(i) == 0);

        return vacio;
    }

    /**
     * Recolecta las semillas del campo que empieza en <i>pos</i> en su depósito (<i>pos + 6</i>)
     *
     * @param pos Posición de inicio del campo
     */
    private void recolectar(int pos) {
        int semillasAlmacen = getSemillas(pos + 6);
        for (int i = pos; i < pos + 6; i++) {
            semillasAlmacen += getSemillas(i);
            setSemillas(i, 0);
        }
        setSemillas(pos + 6, semillasAlmacen);
        Log.i("MiW", "\tRecolectar - " + pos);
    }

    /**
     * @return turno actual
     */
    public Turno turnoActual() {
        return bantumiViewModel.getTurno().getValue();
    }

    /**
     * Establece el turno
     *
     * @param turno
     */
    public void setTurno(Turno turno) {
        bantumiViewModel.setTurno(turno);
    }

    /**
     * Devuelve una cadena que representa el estado completo del juego
     *
     * @return juego serializado
     */
    public String serializa() {
        JSONObject partida = new JSONObject();

        try {
            partida.put("casillas", this.getCasillasJSONObject());
            partida.put("turno", this.turnoActual());
        } catch (JSONException exception) {
            Log.e(MainActivity.LOG_TAG, String.valueOf(R.string.serializaError));
        }

        return partida.toString();
    }

    private JSONObject getCasillasJSONObject() {
        JSONObject casillas = new JSONObject();

        try {
            for (int numCasilla = 0; numCasilla <= 13; numCasilla++) {
                String numero = Integer.toString(numCasilla);

                if (numCasilla < 10) {
                    numero = "0" + numero;
                }

                int id = context.getResources().getIdentifier("casilla_" + numero, "id", context.getPackageName());

                if (numero.equals("13") || numero.equals("06")) {
                    TextView tv = activity.findViewById(id);
                    casillas.put("casilla_" + numero, tv.getText());
                } else {
                    Button button = activity.findViewById(id);
                    casillas.put("casilla_" + numero, button.getText());
                }
            }

        } catch (JSONException exception) {
            Log.e(MainActivity.LOG_TAG, String.valueOf(R.string.serializaError));
        }

        return casillas;
    }

    /**
     * Recupera el estado del juego a partir de su representación
     *
     * @param juegoSerializado cadena que representa el estado completo del juego
     */
    public void deserializa(String juegoSerializado) {
        try {
            JSONObject partida = new JSONObject(juegoSerializado);
            this.deserializarCasillas(partida);
            this.deserializarTurno(partida);
        } catch (JSONException exception) {
            Log.e(MainActivity.LOG_TAG, String.valueOf(R.string.deserializaError));
        }
    }

    public void deserializarCasillas(JSONObject partida) throws JSONException {
        JSONObject casillas = partida.getJSONObject("casillas");
        for (int numCasilla = 0; numCasilla <= 13; numCasilla++) {
            String numero = Integer.toString(numCasilla);

            if (numCasilla < 10) {
                numero = "0" + numero;
            }

            String numeroGuardado = casillas.getString("casilla_" + numero);
            int id = context.getResources().getIdentifier("casilla_" + numero, "id", context.getPackageName());

            if (numero.equals("13") || numero.equals("06")) {
                TextView tv = activity.findViewById(id);
                tv.setText(numeroGuardado);
            } else {
                Button button = activity.findViewById(id);
                button.setText(numeroGuardado);
            }
        }
    }

    public void deserializarTurno(JSONObject partida) throws JSONException {
        Turno turno = Turno.valueOf(partida.getString("turno"));
        this.setTurno(turno);
    }
}
