package in.bizzmark.smartpoints_user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.EarnTransactionBO;

/**
 * Created by User on 18-May-17.
 */

public class EarnTransactionAdapter extends RecyclerView.Adapter<EarnTransactionAdapter.ViewHolder> {
    View view;
    ArrayList<EarnTransactionBO> earnTransactionList;
    Context context ;

    public EarnTransactionAdapter (ArrayList<EarnTransactionBO> earnTransactionBO, Context context){
        this.earnTransactionList = earnTransactionBO;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earn_transaction_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EarnTransactionAdapter.ViewHolder holder, int position) {
        EarnTransactionBO earnTransactionBO = earnTransactionList.get(position);
        holder.tvTransactionId.setText(earnTransactionBO.getTransaction_id());
        holder.tvBillAmount.setText(earnTransactionBO.getBill_amount());
        holder.tvPoints.setText(earnTransactionBO.getPoints());
        holder.tvDateAndTime.setText(earnTransactionBO.getDate_time());
    }

    @Override
    public int getItemCount() {
        return earnTransactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTransactionId, tvBillAmount, tvPoints, tvDateAndTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTransactionId = (TextView) itemView.findViewById(R.id.tv_transaction_id);
            tvBillAmount = (TextView) itemView.findViewById(R.id.tv_bill_amount);
            tvPoints = (TextView) itemView.findViewById(R.id.tv_points);
            tvDateAndTime = (TextView) itemView.findViewById(R.id.tv_date_time);
        }
    }
}
