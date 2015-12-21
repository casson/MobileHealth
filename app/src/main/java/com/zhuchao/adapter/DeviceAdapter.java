package com.zhuchao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhuchao.bean.HardDevice;
import com.zhuchao.mobilehealth.R;

import java.util.ArrayList;

/**
 * Created by zhuchao on 12/20/15.
 */
public class DeviceAdapter extends BaseAdapter {
    private ArrayList<HardDevice>devices;
    private Context context;
    public DeviceAdapter(Context context,ArrayList<HardDevice>devices){
        this.devices=devices;
        this.context=context;
    }
    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        if(position>=0&&position<devices.size())
            return devices.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        HardDevice device=devices.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.device_item,parent,false);

            holder=new ViewHolder();
            holder.name=(TextView)convertView.findViewById(R.id.device_name);
            holder.address=(TextView)convertView.findViewById(R.id.device_address);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.name.setText(device.getName());
        holder.address.setText(device.getAddress());
        return convertView;
    }
    static class ViewHolder{
        TextView name;
        TextView address;
    }
}
