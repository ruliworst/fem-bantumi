package es.upm.miw.bantumi.model.resultado;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ResultadoViewModel extends AndroidViewModel {
    private final ResultadoRepository resultadoRepository;
    private final LiveData<List<Resultado>> mejoresResultados;

    public ResultadoViewModel(Application application) {
        super(application);
        this.resultadoRepository = new ResultadoRepository(application);
        this.mejoresResultados = resultadoRepository.obtenerDiezMejoresResultados();
    }

    public void insertar(Resultado resultado) {
        this.resultadoRepository.insertar(resultado);
    }

    public LiveData<List<Resultado>> obtenerDiezMejoresResultados() {
        return this.mejoresResultados;
    }

    public void borrarTodos() {
        this.resultadoRepository.borrarTodos();
    }
}