package cristo.app.proyectodm.model;

public class Entidad {
    String correoEnvia;
    String UIDEnvia;
    String UIDRecibe;
    String correoRecibe;

    public Entidad(String correoEnvia,String UIDEnvia, String UIDRecibe, String correoRecibe){
        this.correoEnvia=correoEnvia;
        this.UIDEnvia=UIDEnvia;
        this.UIDRecibe=UIDRecibe;
        this.correoRecibe=correoRecibe;
    }

    public String getCorreoEnvia() {
        return correoEnvia;
    }

    public String getUIDEnvia() {
        return UIDEnvia;
    }

    public String getUIDRecibe() {
        return UIDRecibe;
    }

    public String getCorreoRecibe() {
        return correoRecibe;
    }
}