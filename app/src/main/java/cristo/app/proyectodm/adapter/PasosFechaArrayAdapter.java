package cristo.app.proyectodm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.core.PasosFecha;
import cristo.app.proyectodm.core.SettingsManager;

public class PasosFechaArrayAdapter extends ArrayAdapter<PasosFecha> {

    private SettingsManager settsMan;

    public PasosFechaArrayAdapter(@NonNull Context context, List<PasosFecha> objects, SettingsManager settsMan) {
        super(context, 0, objects);
        this.settsMan = settsMan;
    }


    class ViewHolder {
        TextView textViewFecha;
        TextView textViewPasos;
        TextView textViewDistancia;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.pasos_historial, null);

            viewHolder.textViewFecha = convertView.findViewById(R.id.tv_pahistorial_fecha);
            viewHolder.textViewPasos = convertView.findViewById(R.id.tv_pahistorial_pasos);
            viewHolder.textViewDistancia = convertView.findViewById(R.id.tv_pahistorial_distancia);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        int pasos = getItem(position).getPasos();
        viewHolder.textViewFecha.setText(getItem(position).getFecha());
        viewHolder.textViewPasos.setText(String.valueOf(pasos));
        viewHolder.textViewDistancia.setText(settsMan.stepsToDistance(pasos));

        return convertView;
    }
}
