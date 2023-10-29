package es.upm.miw.bantumi.model.resultado;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Resultado.class}, version = 1, exportSchema = false)
public abstract class ResultadoRoomDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "resultado_database";

    public abstract ResultadoDAO resultadoDAO();

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile ResultadoRoomDatabase INSTANCE;

    static ResultadoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ResultadoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), ResultadoRoomDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}