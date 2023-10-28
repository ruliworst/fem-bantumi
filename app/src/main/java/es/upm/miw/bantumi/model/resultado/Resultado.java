package es.upm.miw.bantumi.model.resultado;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Resultado")
public class Resultado {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;

    @ColumnInfo(name = "ganador")
    private String ganador;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "semillas_ganador")
    private Integer semillasGanador;

    @ColumnInfo(name = "semillas_perdedor")
    private Integer semillasPerdedor;

    @ColumnInfo(name = "empate")
    private Boolean empate;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getGanador() {
        return this.ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }


    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getSemillasGanador() {
        return this.semillasGanador;
    }

    public void setSemillasGanador(Integer semillasGanador) {
        this.semillasGanador = semillasGanador;
    }

    public Integer getSemillasPerdedor() {
        return this.semillasPerdedor;
    }

    public void setSemillasPerdedor(Integer semillasPerdedor) {
        this.semillasPerdedor = semillasPerdedor;
    }

    public boolean getEmpate() {
        return this.empate;
    }

    public void setEmpate(Boolean empate) {
        this.empate = empate;
    }

    public static class Builder implements ResultadoBuilder {
        private final Resultado resultado;

        public Builder() {
            this.resultado = new Resultado();
        }

        @Override
        public ResultadoBuilder setSemillasGanador(Integer semillasGanador) {
            this.resultado.setSemillasGanador(semillasGanador);
            return this;
        }

        @Override
        public ResultadoBuilder setSemillasPerdedor(Integer semillasPerdedor) {
            this.resultado.setSemillasPerdedor(semillasPerdedor);
            return this;
        }

        @Override
        public ResultadoBuilder setGanador(String ganador) {
            this.resultado.setGanador(ganador);
            return this;
        }

        @Override
        public ResultadoBuilder setFecha(String fecha) {
            this.resultado.setFecha(fecha);
            return this;
        }

        @Override
        public ResultadoBuilder setEmpate(Boolean empate) {
            this.resultado.setEmpate(empate);
            return this;
        }

        @Override
        public Resultado build() {
            return this.resultado;
        }
    }
}
