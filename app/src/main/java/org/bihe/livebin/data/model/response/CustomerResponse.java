package org.bihe.livebin.data.model.response;

import org.bihe.livebin.data.model.Customer;

import java.util.List;

public class CustomerResponse {
    private List<Customer> results;

    public List<Customer> getResults() {
        return results;
    }

    public void setResults(List<Customer> results) {
        this.results = results;
    }
}
