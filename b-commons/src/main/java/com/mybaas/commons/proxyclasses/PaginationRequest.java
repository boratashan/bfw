package com.mybaas.commons.proxyclasses;

import com.mybaas.commons.model.QueryStringParsingException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;

@DataObject(generateConverter = true)
public class PaginationRequest {

    public static final String QRY_PAGENUMBER = "page";
    public static final String QRY_SIZE = "size";
    public static final int DEFAULT_PAGENUMBER = 1;
    public static final int DEFAULT_SIZE = 10;
    private int size = 0;
    private int pageNumber = 0;
    private boolean valid = false;


    public boolean isValid() {
        return valid;
    }

    public int getSize() {
        return (size <= 0 ? DEFAULT_SIZE : size);
    }

    public PaginationRequest setSize(int size) {
        this.size = size;
        return this;
    }

    public int getPageNumber() {
        return (pageNumber <= 0 ? DEFAULT_PAGENUMBER : pageNumber);
    }

    public PaginationRequest setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public PaginationRequest(JsonObject jsonObject) {
        PaginationRequestConverter.fromJson(jsonObject, this);
    }

    public PaginationRequest() {
    }

    public static PaginationRequest create(String queryString) throws QueryStringParsingException {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.valid = false;
        try {
            Arrays.stream(queryString.split("&"))
                    .forEach(value -> {
                        String[] values = value.split("=");
                        if (values.length == 2) {
                            int val = Integer.valueOf(values[1]);
                            switch (values[0]) {
                                case QRY_PAGENUMBER: {
                                    paginationRequest.setPageNumber(val);
                                    break;
                                }
                                case QRY_SIZE: {
                                    paginationRequest.setSize(val);
                                    break;
                                }

                            }
                        }
                    });
            //Set request is completed and valid.
            paginationRequest.valid = true;
        } catch (Exception e) {
            throw new QueryStringParsingException(String.format("Query string parsing error, QueryString `%s`", queryString), e);
        }
        return paginationRequest;
    }


    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        PaginationRequestConverter.toJson(this, jsonObject);
        return jsonObject;
    }


    @Override
    public String toString() {
        return "PaginationRequest{" +
                "size=" + size +
                ", pageNumber=" + pageNumber +
                ", valid=" + valid +
                '}';
    }


}
