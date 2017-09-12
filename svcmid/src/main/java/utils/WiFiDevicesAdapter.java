package utils;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.usu.svcmid.R;
import com.usu.svcmid.wfd.WiFiP2pService;

import java.util.List;

/**
 * Created by lee on 9/12/17.
 */
public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {

    private Context context;
    private List<WiFiP2pService> items;


    public WiFiDevicesAdapter(Context context, int resource, int textViewResourceId,
                              List<WiFiP2pService> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
            // v = vi.inflate(android.R.layout.simple_list_item_2, null);
            v = vi.inflate(R.layout.row_devices, null);

        }

        WiFiP2pService service = items.get(position);
        if (service != null) {
            TextView nameText = (TextView) v.findViewById(android.R.id.text1);

            if (nameText != null) {
                nameText.setText(service.device.deviceName + "_" + service.name);
            }
            TextView statusText = (TextView) v.findViewById(android.R.id.text2);
            statusText.setText(getDeviceStatus(service.device.status));
        }
        return v;
    }

    /**
     * return device status in String rather than number
     * @param statusCode
     * @return
     */
    public static String getDeviceStatus(int statusCode) {
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

}