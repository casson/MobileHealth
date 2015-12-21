package com.zhuchao.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.wahoofitness.connector.conn.connections.params.ConnectionParams;

/**
 * Created by zhuchao on 12/20/15.
 */
public class HardDevice implements Parcelable{
    private String name;
    private String address;
    private ConnectionParams params;

    public ConnectionParams getParams() {
        return params;
    }

    public void setParams(ConnectionParams params) {
        this.params = params;
    }

    public HardDevice(){

    }
    public HardDevice(String name,String address,ConnectionParams params){
        this.name=name;
        this.address=address;
        this.params=params;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeSerializable(params.serialize());
    }
    public static  final Parcelable.Creator<HardDevice>CREATOR=new Creator<HardDevice>() {
        @Override
        public HardDevice createFromParcel(Parcel source) {
            return new HardDevice(source);
        }

        @Override
        public HardDevice[] newArray(int size) {
            return new HardDevice[size];
        }
    };
    private HardDevice(Parcel in){
        name=in.readString();
        address=in.readString();
        params=ConnectionParams.fromString(in.readSerializable().toString());
    }
}
