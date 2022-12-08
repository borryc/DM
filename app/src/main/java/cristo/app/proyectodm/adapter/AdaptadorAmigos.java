package cristo.app.proyectodm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.model.EntidadAmigos;

public class AdaptadorAmigos extends BaseAdapter {
    Context context;
    ArrayList<EntidadAmigos> listAmigos;


    public AdaptadorAmigos(Context context, ArrayList<EntidadAmigos> listAmigos) {
        this.context = context;
        this.listAmigos = listAmigos;
    }

    @Override
    public int getCount() {
        return listAmigos.size();
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
        //Meter todos los elementos que haya metido en el list_item_vic.xml

        TextView nombreAmigo;
        TextView pasosAmigo;

        ImageView iv_foto_perfil;

        EntidadAmigos Item = listAmigos.get(i);

        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.list_item_friends, null);



        nombreAmigo = view.findViewById(R.id.nombreAmigo);

        pasosAmigo = view.findViewById(R.id.pasosAmigo);

        iv_foto_perfil=view.findViewById(R.id.foto_usuario_amigo);

        nombreAmigo.setText(Item.getCorreo());
        pasosAmigo.setText(Item.getPasos());

        Drawable image= ContextCompat.getDrawable(context,R.drawable.logo);

        if(Item.getFoto().equals("")){
            iv_foto_perfil.setImageDrawable(image);
        }
        else {
            iv_foto_perfil.setImageBitmap(StringToBitMap(Item.getFoto()));
        }
        return view;
    }
    //Convierte string a bitmap para la imagen
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


}



