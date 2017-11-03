package asan.hospital.asanbeacon;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.distance.AndroidModel;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import asan.hospital.asanbeacon.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity implements BeaconConsumer {

    private static final String BEACON_PARSER = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ENABLE_BT = 100;

    BluetoothAdapter mBluetoothAdapter;

    BeaconAdapter beaconAdapter;

    ActivityMainBinding binding;

    BeaconManager mBeaconManager;

    Vector<Item> items;

    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        AndroidModel am = AndroidModel.forThisDevice();
        Log.d("getManufacturer()",am.getManufacturer());
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_PARSER));
            //BeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
        }

        binding.scanBleFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeaconManager.isBound(MainActivity.this)) {
                    binding.scanBleFAB.setImageResource(R.drawable.ic_visibility_white_24dp);
                    Log.i(TAG, "Stop BLE Scanning...");
                    mBeaconManager.unbind(MainActivity.this);
                } else {
                    binding.scanBleFAB.setImageResource(R.drawable.ic_visibility_off_white_24dp);
                    Log.i(TAG, "Start BLE Scanning...");
                    mBeaconManager.bind(MainActivity.this);
                }
            }
        });
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.beaconListView.setLayoutManager(manager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_PARSER));
            //BeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
        }

    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Iterator<Beacon> iterator = beacons.iterator();
                    items = new Vector<>();
                    while (iterator.hasNext()) {
                        Beacon beacon = iterator.next();
                        String address = beacon.getBluetoothAddress();
                            double rssi = beacon.getRssi();
                            int txPower = beacon.getTxPower();
                            double distance = Double.parseDouble(decimalFormat.format(beacon.getDistance()));
                            int major = beacon.getId2().toInt();
                            int minor = beacon.getId3().toInt();
                            items.add(new Item(address, rssi, txPower, distance, major, minor));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            beaconAdapter = new BeaconAdapter(items, MainActivity.this);
                            binding.beaconListView.setAdapter(beaconAdapter);
                            beaconAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }


            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                    Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });
        try {
            mBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
