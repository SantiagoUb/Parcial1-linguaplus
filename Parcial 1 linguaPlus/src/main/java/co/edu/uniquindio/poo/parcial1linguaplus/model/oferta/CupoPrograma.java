package co.edu.uniquindio.poo.parcial1linguaplus.model.oferta;

public class CupoPrograma implements Cloneable{
    private final String codigoPrograma;
    private final int cuposTotales;
    private int cuposOcupados;

    public CupoPrograma(String codigoPrograma, int cuposTotales) {
        this.codigoPrograma = codigoPrograma;
        this.cuposTotales = cuposTotales;
        this.cuposOcupados = 0;
    }

    public boolean ocuparCupo() {
        if(getCuposDisponibles() <= 0) return false;
        cuposOcupados++;
        return true;
    }


    /* Clon "limpio": el nuevo periodo hereda el total de cupos pero arranca sin ocupar,
     *  asi llenar cupos en un periodo NO afecta los cupos disponibles del periodo siguiente.
     */
    @Override
    public CupoPrograma clone() {
        return new CupoPrograma(codigoPrograma, cuposTotales);
    }

    @Override
    public String toString() {
        return "CupoPrograma{" +
                "codigoPrograma='" + codigoPrograma + '\'' +
                ", cuposTotales=" + cuposTotales +
                ", cuposOcupados=" + cuposOcupados +
                '}';
    }

    public int getCuposDisponibles() {
        return cuposTotales - cuposOcupados;
    }

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public int getCuposTotales() {
        return cuposTotales;
    }
}
