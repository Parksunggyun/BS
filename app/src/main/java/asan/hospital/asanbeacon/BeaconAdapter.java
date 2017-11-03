package asan.hospital.asanbeacon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Vector;

import asan.hospital.asanbeacon.databinding.ItemBeaconBinding;

/*
 * Created by 15U560 on 2017-10-26.
 */

class BeaconAdapter extends RecyclerView.Adapter {

    private Vector<Item> items;
    private Context context;


    BeaconAdapter(Vector<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemBeaconBinding binding = ItemBeaconBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ItemHolders(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ItemHolders itemViewHolder = (ItemHolders) holder;
            final ItemBeaconBinding binding = itemViewHolder.binding;
            binding.rssiTv.setText(""+items.get(position).getRssi() +"dBm");
            binding.txPowerTv.setText(""+items.get(position).getTxPower() +"dBm");
            binding.distanceTv.setText(""+items.get(position).getDistance() +"m");
            binding.majorTv.setText("major \n"+items.get(position).getMajor()+"");
            binding.minorTv.setText("minor \n"+items.get(position).getMinor()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class ItemHolders extends RecyclerView.ViewHolder {

        ItemBeaconBinding binding;

        ItemHolders(ItemBeaconBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
