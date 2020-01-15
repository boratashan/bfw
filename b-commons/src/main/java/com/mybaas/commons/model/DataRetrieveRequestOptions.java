package com.mybaas.commons.model;

import com.mybaas.commons.proxyclasses.PaginationRequest;

public class DataRetrieveRequestOptions {
    PaginationRequest paginationRequest;
    SortingRequest sortingRequest;
    FilteringRequest filteringRequest;

    public DataRetrieveRequestOptions(String query) throws QueryStringParsingException {
        this.paginationRequest = PaginationRequest.create(query);
        this.filteringRequest = new FilteringRequest();
        this.sortingRequest = new SortingRequest();
    }


}
