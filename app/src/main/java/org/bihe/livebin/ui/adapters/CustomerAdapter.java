package org.bihe.livebin.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.databinding.ItemCustomerBinding;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private final Context context;
    private List<Customer> customers;
    private final LayoutInflater layoutInflater;

    private MapView mapView;

    public CustomerAdapter(Context context, List<Customer> customers) {
        this.context = context;
        this.customers = customers;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void updateReports(List<Customer> newCustomers) {
        this.customers = newCustomers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerBinding binding = ItemCustomerBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
            return customers.size();
    }

    public void itemInsertedOnTop() {
        notifyItemInserted(0);
        notifyItemRangeChanged(0, customers.size() - 1);
    }

    public void update() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCustomerBinding binding;

        public ViewHolder(@NonNull ItemCustomerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(int position) {
            Customer customer = customers.get(position);

            binding.customerNameTv.setText(customer.getCustomerName());
            binding.phoneNumberTv.setText(customer.getPhoneNumber());
            binding.shopAreaTv.setText(String.valueOf(customer.getShopArea()));

//            chatgpt
            mapView = binding.mapview;

            mapView.setBuiltInZoomControls(true);
            mapView.setMultiTouchControls(true);


            mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);


            double latitude = customer.getLatitude();
            double longitude = customer.getLongitude();

            org.osmdroid.util.GeoPoint userLocation = new GeoPoint(latitude, longitude);


            IMapController mapController = mapView.getController();
            mapController.setZoom(15);
            mapController.setCenter(userLocation);

            Marker marker = new Marker(mapView);
            marker.setPosition(userLocation);
            mapView.getOverlays().add(marker);

        }
    }
}
