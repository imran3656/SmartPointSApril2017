package in.bizzmark.smartpoints_user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.EarnPointsBO;

/**
 * Created by User on 25-Apr-17.
 */

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder>{

    ArrayList<EarnPointsBO> pointsBOList;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void SetOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public PointsAdapter(ArrayList<EarnPointsBO> pointsBOList,Context context, OnItemClickListener listener) {
        super();
        this.pointsBOList = pointsBOList;
        this.context = context;
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return pointsBOList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        EarnPointsBO earnPointsBO = pointsBOList.get(position);
        holder.storename.setText(earnPointsBO.getStorename());
        holder.points.setText(earnPointsBO.getPoints());

        if (mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,position);
                }
            });
        }

       // Toast.makeText(context, "StoreName : "+earnPointsBO.getStorename() + "\n" + "Points : "+earnPointsBO.getPoints(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.points_row,parent,false);

        final ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, viewHolder.getPosition());
            }
        });

        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView storename;
        public TextView points;

        public ViewHolder(View itemView) {
            super(itemView);
            storename = (TextView) itemView.findViewById(R.id.tvStoreName);
            points = (TextView) itemView.findViewById(R.id.tvPoints);

        }
    }
}
