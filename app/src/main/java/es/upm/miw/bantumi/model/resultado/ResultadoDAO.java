package es.upm.miw.bantumi.model.resultado;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultadoDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertar(Resultado resultado);

    @Query("select * from Resultado order by semillas_ganador desc limit 10")
    LiveData<List<Resultado>> obtenerDiezMejoresResultados();

    @Query("delete from Resultado")
    void borrarTodos();
}