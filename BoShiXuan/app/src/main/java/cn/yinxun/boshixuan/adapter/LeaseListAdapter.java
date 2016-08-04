package cn.yinxun.boshixuan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.yinxun.boshixuan.activity.LeaseProductActivity;
import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class LeaseListAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> mLeaseTextCHList;
    private List<String> mLeaseTextENList;
    private List<Integer> mLeaseBgList;
    public LeaseListAdapter(Context context,List<String> leaseTextCHList,List<String> leaseTextENList,List<Integer> leaseBgList){
        this.mContext = context;
        this.mLeaseTextCHList = leaseTextCHList;
        this.mLeaseTextENList = leaseTextENList;
        this.mLeaseBgList = leaseBgList;
    }
    @Override
    public int getCount() {
        return this.mLeaseTextCHList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mLeaseTextCHList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_lease,null);
            holder = new ViewHolder();
            holder.leaseTextCH = (TextView) convertView.findViewById(R.id.leaseTextCH);
            holder.leaseLayout = (RelativeLayout) convertView.findViewById(R.id.leaseLayout);
            holder.leaseTextEN = (TextView) convertView.findViewById(R.id.leaseTextEN);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.leaseTextCH.setText(this.mLeaseTextCHList.get(position));
        holder.leaseTextCH.setTextColor(this.mContext.getResources().getColor(R.color.white));
        holder.leaseLayout.setBackgroundResource(this.mLeaseBgList.get(position));
        holder.leaseTextEN.setText(this.mLeaseTextENList.get(position));
        holder.leaseTextEN.setTextColor(this.mContext.getResources().getColor(R.color.white));
        final int index = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaseListAdapter.this.mContext, LeaseProductActivity.class);
                switch (index) {
                    case 0:
                        intent.putExtra("type",1);
                        break;
                    case 1:
                        intent.putExtra("type",3);
                        break;
                    case 2:
                        intent.putExtra("type",2);
                        break;
                    case 3:
                        intent.putExtra("type",4);
                        break;
                }
                LeaseListAdapter.this.mContext.startActivity(intent);
            }
        });
        return convertView;
    }
    class ViewHolder{
        public TextView leaseTextCH;
        public TextView leaseTextEN;
        public RelativeLayout leaseLayout;
    }
}
