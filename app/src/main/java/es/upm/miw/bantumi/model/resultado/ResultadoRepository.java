package es.upm.miw.bantumi.model.resultado;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ResultadoRepository {
    private final ResultadoDAO resultadoDAO;
    private final LiveData<List<Resultado>> mejoresResultados;

    ResultadoRepository(Application application) {
        ResultadoRoomDatabase db = ResultadoRoomDatabase.getDatabase(application);
        resultadoDAO = db.resultadoDAO();
        mejoresResultados = this.resultadoDAO.obtenerDiezMejoresResultados();
    }

    void insertar(Resultado resultado) {
        ResultadoRoomDatabase.databaseWriteExecutor.execute(() -> this.resultadoDAO.insertar(resultado));
    }

    public LiveData<List<Resultado>> obtenerDiezMejoresResultados() {
        return this.mejoresResultados;
    }

    public void borrarTodos() {
        ResultadoRoomDatabase.databaseWriteExecutor.execute(this.resultadoDAO::borrarTodos);
    }
}