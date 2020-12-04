package ru.valery.worldskills.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import ru.valery.worldskills.R;
import ru.valery.worldskills.TypeData.NomenclatureData;
import ru.valery.worldskills.TypeData.OrderData;


//Адаптер для вывода данных о номенклатуре заказа курьера

public class CourierAdapter extends RecyclerView.Adapter<CourierAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NomenclatureData> product;

    public CourierAdapter(Context context, OrderData posts) {
        this.context = context;
        this.product = posts.getNom();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_recylcer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NomenclatureData post = product.get(position);
        holder.textView_name.setText(context.getString(R.string.name) + post.getName());
        holder.textView_count.setText(context.getString(R.string.count) + post.getCount().toString());
    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name;
        TextView textView_count;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.textView_name);
            textView_count = itemView.findViewById(R.id.textView_count);
        }
    }
}