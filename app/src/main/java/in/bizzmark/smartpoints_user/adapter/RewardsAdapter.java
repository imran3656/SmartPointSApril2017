package in.bizzmark.smartpoints_user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.RewardsBO;

/**
 * Created by User on 31-May-17.
 */

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.ViewHolder> {
    View view;
    ArrayList<RewardsBO> rewardsList;
    Context context ;

    public RewardsAdapter (ArrayList<RewardsBO> rewardsBO, Context context){
        this.rewardsList = rewardsBO;
        this.context = context;
    }

    @Override
    public RewardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_items,parent,false);
        return new RewardsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RewardsAdapter.ViewHolder holder, int position) {
        RewardsBO rewardsBO = rewardsList.get(position);
        holder.tvPoints.setText(rewardsBO.getPoints()+"\n"+"points");
        holder.tvRewards.setText(rewardsBO.getRewards());
        holder.tvRewards.setVisibility(View.GONE);
        holder.tvRewarsExpDate.setText("Expire date : "+rewardsBO.getExp_date());
        holder.tvDescription.setText(rewardsBO.getDescription());
    }

    @Override
    public int getItemCount() {
        return rewardsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvPoints, tvRewards, tvRewarsExpDate,tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            tvPoints = (TextView) itemView.findViewById(R.id.tv_rewards_points);
            tvRewards = (TextView) itemView.findViewById(R.id.tv_rewards);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            tvRewarsExpDate = (TextView) itemView.findViewById(R.id.tv_rewards_exp_date);
        }
    }
}
