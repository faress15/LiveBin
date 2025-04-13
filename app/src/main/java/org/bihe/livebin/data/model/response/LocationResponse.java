package org.bihe.livebin.data.model.response;

import org.bihe.livebin.data.model.LocationObj;

import java.util.List;

public class LocationResponse {
    private List<LocationObj> results;

    public List<LocationObj> getResults() {
        return results;
    }

    public void setResults(List<LocationObj> results) {
        this.results = results;
    }
}
