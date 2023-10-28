package es.upm.miw.bantumi.model.resultado;

public interface ResultadoBuilder {

    ResultadoBuilder setSemillasGanador(Integer semillasGanador);

    ResultadoBuilder setSemillasPerdedor(Integer semillasPerdedor);

    ResultadoBuilder setGanador(String ganador);

    ResultadoBuilder setFecha(String fecha);

    ResultadoBuilder setEmpate(Boolean empate);


    Resultado build();
}