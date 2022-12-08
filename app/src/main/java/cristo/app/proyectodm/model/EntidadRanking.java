package cristo.app.proyectodm.model;

public class EntidadRanking {

    String correo;
    String pasos;

    public EntidadRanking(String correo,String pasos){

        this.correo=correo;
        this.pasos=pasos;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPasos() {
        return pasos;
    }

}
