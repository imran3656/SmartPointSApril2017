package in.bizzmark.smartpoints_user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.StoresListBO;

/**
 * Created by Saikrupa on 4/1/2018.
 */

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder>{


    private List<StoresListBO> mList;
    private Context mContext;

    public StoresAdapter(List<StoresListBO> storesList, Context _context){
        mList=storesList;
        mContext=_context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.store_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        StoresListBO singleStore=mList.get(position);
        holder.tvStoreName.setText(singleStore.getStoreName());
        holder.tvDescription.setText(singleStore.getDescription());
        holder.tvAddress.setText(singleStore.getBranchAddress());
        String titleStr=singleStore.getStoreName().substring(0,1);
       if(titleStr!=null)
           holder.title.setText(titleStr);
       else
           holder.title.setText("A");


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStoreName;
        TextView tvDescription;
        TextView tvAddress;
        TextView title;



         ViewHolder(View itemView) {
            super(itemView);
            tvStoreName=(TextView)itemView.findViewById(R.id.tvStoreName);
             tvDescription=(TextView)itemView.findViewById(R.id.tvDescription);
            tvAddress=(TextView)itemView.findViewById(R.id.tvAddress);
            title=(TextView)itemView.findViewById(R.id.title);

        }
    }
}
