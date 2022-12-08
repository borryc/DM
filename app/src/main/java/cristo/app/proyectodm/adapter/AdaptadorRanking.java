package cristo.app.proyectodm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.model.EntidadRanking;

public class AdaptadorRanking extends BaseAdapter {
    Context context;
    ArrayList<EntidadRanking> listRanking;

    public AdaptadorRanking(Context context, ArrayList<EntidadRanking> listRanking) {
        this.context = context;
        this.listRanking = listRanking;
    }

    @Override
    public int getCount() {
        return listRanking.size();
    }

    @Override
    public Object getItem(int i) {  //i=position
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView nombreRanking;
        TextView pasosRaking;
        EntidadRanking Item = listRanking.get(i);


        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.list_item_ranking, null);



        nombreRanking = view.findViewById(R.id.nombreRanking);

        pasosRaking = view.findViewById(R.id.pasosRanking);


        nombreRanking.setText(Item.getCorreo());
        pasosRaking.setText(Item.getPasos());


        return view;
    }

}
