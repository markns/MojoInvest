package com.mns.mojoinvest.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OptionsDto implements IsSerializable {

    private String title;

    public OptionsDto() {
    }

    public OptionsDto(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
