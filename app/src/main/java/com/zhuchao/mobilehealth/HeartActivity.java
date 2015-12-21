package com.zhuchao.mobilehealth;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wahoofitness.connector.HardwareConnectorEnums;
import com.wahoofitness.connector.capabilities.Capability;
import com.wahoofitness.connector.capabilities.Heartrate;
import com.wahoofitness.connector.conn.connections.SensorConnection;
import com.wahoofitness.connector.conn.connections.params.ConnectionParams;
import com.zhuchao.adapter.DeviceAdapter;
import com.zhuchao.bean.HardDevice;
import com.zhuchao.connection.HardwareConnectorServiceConnection;
import com.zhuchao.service.HardwareConnectorService;
import com.zhuchao.view_rewrite.HeartCircle;
import com.zhuchao.view_rewrite.PullToRefreshView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HeartActivity extends Activity implements HardwareConnectorService.Listener,HardwareConnectorServiceConnection.Listener{

    private ListView deviceList;
    private HeartCircle heartCircle;
    private Capability capability;
    private PullToRefreshView pullToRefreshView;
    private ArrayList<HardDevice>devices;
    private DeviceAdapter deviceAdapter;
    private SensorConnection sensorConnection;
    private HardwareConnectorServiceConnection hardwareConnectorServiceConnection;
    private List<ConnectionParams> mDiscoveredConnectionParams = new ArrayList<ConnectionParams>();
    private boolean isRefresh=false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(!isDiscovering())
                        enableDiscovery(true);
                    refreshView();
                    break;
                case 1:

                    pullToRefreshView.onHeaderRefreshComplete();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        initView();
    }
    private void initView(){
        heartCircle=(HeartCircle)findViewById(R.id.heartRate);
        pullToRefreshView=(PullToRefreshView)findViewById(R.id.pull_to_refresh);
        /**
         * list view
         */
        deviceList=(ListView)findViewById(R.id.devices);
        devices=new ArrayList<HardDevice>();
        deviceAdapter=new DeviceAdapter(this,devices);
        deviceList.setAdapter(deviceAdapter);

        /**
         * pull listener
         */
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                isRefresh=true;
                mHandler.sendEmptyMessage(0);
            }
        });
        hardwareConnectorServiceConnection=new HardwareConnectorServiceConnection(this,hardListener);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HardDevice device= (HardDevice) deviceAdapter.getItem(position);
                sensorConnection=connectSensor(device.getParams());
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        HardwareConnectorService service=hardwareConnectorServiceConnection.getHardwareConnectorService();
        if(service!=null)
            service.removeListener(this);
    }

    private void refreshView(){
        mDiscoveredConnectionParams.clear();
        for (SensorConnection connectedDevices : getSensorConnections()) {
            ConnectionParams connectedParams = connectedDevices.getConnectionParams();
            if (!mDiscoveredConnectionParams.contains(connectedParams)) {
                mDiscoveredConnectionParams.add(connectedParams);
            }
        }
        for (ConnectionParams discoveredParams : getDiscoveredConnectionParams()) {
            if (!mDiscoveredConnectionParams.contains(discoveredParams)) {
                mDiscoveredConnectionParams.add(discoveredParams);
            }
        }

        Collections.sort(mDiscoveredConnectionParams, new Comparator<ConnectionParams>() {

            @Override
            public int compare(ConnectionParams lhs, ConnectionParams rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        devices.clear();
        for(int i=0;i<mDiscoveredConnectionParams.size();i++){
            ConnectionParams params=mDiscoveredConnectionParams.get(i);
            Log.d("test",params.getName());
            HardDevice device=new HardDevice();
            device.setName(params.getName());
            SensorConnection sensorConnection=getSensorConnection(params);
            if(sensorConnection!=null){
                switch (sensorConnection.getConnectionState()){
                    case CONNECTED:
                        device.setAddress("Connected");
                        break;
                    case CONNECTING:
                        device.setAddress("Connecting");
                        break;
                    case DISCONNECTED:
                        device.setAddress("Disconnected");
                        break;
                    case DISCONNECTING:
                        device.setAddress("Disconnecting");
                        break;
                    default:
                        device.setAddress("Error");
                        break;
                }
            }
            device.setParams(params);
            devices.add(device);
        }
        deviceAdapter.notifyDataSetChanged();
        if(devices.size()>0)
            enableDiscovery(false);
        if(isRefresh){
            isRefresh=false;
            mHandler.sendEmptyMessage(1);
        }
    }
    /**
     * connect sensor
     * @param params
     * @return
     */
    private SensorConnection connectSensor(ConnectionParams params){
        if (hardwareConnectorServiceConnection != null) {
            return hardwareConnectorServiceConnection.connectSensor(params);
        } else {
            return null;
        }
    }

    /**
     * disconnect sensor
     * @param params
     */
    private void disconnectSensor(ConnectionParams params) {
        if (hardwareConnectorServiceConnection != null) {
            hardwareConnectorServiceConnection.disconnectSensor(params);
        }

    }

    /**
     * open discovery
     * @param enable
     * @return
     */
    private boolean enableDiscovery(boolean enable) {
        if (hardwareConnectorServiceConnection != null) {
            return hardwareConnectorServiceConnection.enableDiscovery(enable);
        } else {
            return false;
        }

    }

    /**
     * get discovery params
     * @return
     */
    protected Collection<ConnectionParams> getDiscoveredConnectionParams() {
        if (hardwareConnectorServiceConnection != null) {
            return hardwareConnectorServiceConnection.getDiscoveredConnectionParams();
        } else {
            return new ArrayList<ConnectionParams>();
        }
    }

    /**
     * connect sensor
     * @param params
     * @return
     */
    protected SensorConnection getSensorConnection(ConnectionParams params) {
        if (hardwareConnectorServiceConnection != null) {
            return hardwareConnectorServiceConnection.getSensorConnection(params);
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    protected Collection<SensorConnection> getSensorConnections() {
        if (hardwareConnectorServiceConnection != null) {
            return hardwareConnectorServiceConnection.getSensorConnections();
        } else {
            return new ArrayList<SensorConnection>();
        }
    }

    boolean isDiscovering() {
        if (hardwareConnectorServiceConnection != null) {
            return hardwareConnectorServiceConnection.isDiscovering();
        } else {
            return false;
        }
    }
    @Override
    public void onDeviceDiscovered(ConnectionParams params) {
        refreshView();
    }

    @Override
    public void onDiscoveredDeviceLost(ConnectionParams params) {
        refreshView();
    }

    @Override
    public void onDiscoveredDeviceRssiChanged(ConnectionParams params) {
        refreshView();
    }

    @Override
    public void onFirmwareUpdateRequired(SensorConnection sensorConnection, String currentVersionNumber, String recommendedVersion) {

    }

    @Override
    public void onNewCapabilityDetected(SensorConnection sensorConnection, Capability.CapabilityType capabilityType) {
        refreshRate();
    }

    @Override
    public void onSensorConnectionStateChanged(SensorConnection sensorConnection, HardwareConnectorEnums.SensorConnectionState state) {
        refreshRate();
    }
    private void refreshRate(){
        if(sensorConnection!=null){
            for(Capability.CapabilityType capabilityType:sensorConnection.getCurrentCapabilities()){
                if(capabilityType== Capability.CapabilityType.Heartrate) {
                    capability = sensorConnection.getCurrentCapability(capabilityType);
                    Heartrate.Data data = ((Heartrate) capability).getHeartrateData();
                    heartCircle.setHeartRate(String.valueOf(data.getHeartrateBpm()));
                }
            }
        }
    }
    private final HardwareConnectorServiceConnection.Listener hardListener=new HardwareConnectorServiceConnection.Listener() {
        @Override
        public void onHardwareConnectorServiceConnected(HardwareConnectorService hardwareConnectorService) {
            hardwareConnectorService.addListener(HeartActivity.this);
            HeartActivity.this.onHardwareConnectorServiceConnected(hardwareConnectorService);
        }

        @Override
        public void onHardwareConnectorServiceDisconnected() {
            HeartActivity.this.onHardwareConnectorServiceDisconnected();
        }
    };

    @Override
    public void onHardwareConnectorServiceConnected(HardwareConnectorService hardwareConnectorService) {
        hardwareConnectorService.addListener(this);
        enableDiscovery(true);
        refreshView();
    }

    @Override
    public void onHardwareConnectorServiceDisconnected() {

    }
}
