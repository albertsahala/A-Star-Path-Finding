package sample;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

public class MarkerLoc extends Marker {

    private LatLong latLong;

    MarkerLoc(MarkerOptions markerOptions, LatLong latLong) {
        super(markerOptions);
        this.latLong = latLong;
    }

    public LatLong getLatLong() {
        return latLong;
    }

    @Override
    public String toString() {
        return latLong.toString();
    }
}
