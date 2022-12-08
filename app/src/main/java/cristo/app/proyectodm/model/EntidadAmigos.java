package cristo.app.proyectodm.model;

public class EntidadAmigos {
    String correo;
    String pasos;
    String UIDAmigo;
    String foto;

    public EntidadAmigos(String correo,String pasos, String UIDAmigo, String foto){

        this.correo=correo;
        this.pasos=pasos;
        this.UIDAmigo=UIDAmigo;
        this.foto=foto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPasos() {
        return pasos;
    }

    public String getUIDAmigo() {
        return UIDAmigo;
    }

    public String getFoto(){return foto;}
}
