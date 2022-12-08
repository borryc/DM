package cristo.app.proyectodm.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PasosFecha {

    private int pasos;
    private Date fecha;

    public PasosFecha(int pasos) {
        this.pasos = pasos;
        this.fecha = new Date(System.currentTimeMillis());
    }

    public int getPasos() {
        return pasos;
    }

    public String getFecha() {
       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); !!!Normal
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(fecha);
    }
    public Date getFechaDate(){
        return fecha;
    }

    public void setPasos(int pasos) {
        this.pasos = pasos;
    }

    @Override
    public String toString() {
        return "PasosFecha{" +
                "pasos=" + pasos  +
                ", fecha=" + fecha +
                '}';
    }
}
