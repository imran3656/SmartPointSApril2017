package in.bizzmark.smartpoints_user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.RedeemTransactionBO;

/**
 * Created by User on 18-May-17.
 */

public class RedeemTransactionAdapter extends RecyclerView.Adapter<RedeemTransactionAdapter.ViewHolder> {
    View view;
    ArrayList<RedeemTransactionBO> redeemTransactionList;
    Context context ;

    public RedeemTransactionAdapter(ArrayList<RedeemTransactionBO> redeemTransactionBO, Context context){
        this.redeemTransactionList = redeemTransactionBO;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.redeem_transaction_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RedeemTransactionAdapter.ViewHolder holder, int position) {
        RedeemTransactionBO redeemTransactionBO = redeemTransactionList.get(position);
        holder.tvTransactionId.setText(redeemTransactionBO.getTransaction_id());
        holder.tvPaidBill.setText(redeemTransactionBO.getBill_amount());
        holder.tvPoints.setText(redeemTransactionBO.getPoints());
        holder.tvDiscountAmount.setText(redeemTransactionBO.getDiscount_amount());
        holder.tvDateAndTime.setText(redeemTransactionBO.getDate_time());
    }

    @Override
    public int getItemCount() {
        return redeemTransactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTransactionId, tvPaidBill, tvPoints, tvDiscountAmount, tvDateAndTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTransactionId = (TextView) itemView.findViewById(R.id.tv_redeem_transaction_id);
            tvPaidBill = (TextView) itemView.findViewById(R.id.tv_paid_bill);
            tvPoints = (TextView) itemView.findViewById(R.id.tv_redeem_points);
            tvDiscountAmount = (TextView) itemView.findViewById(R.id.tv_redeem_discount_amount);
            tvDateAndTime = (TextView) itemView.findViewById(R.id.tv_date_times);
        }
    }
}
