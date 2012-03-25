package com.mns.mojoinvest.server.engine.model.dao;

import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryFundDao {

    List<Fund> funds = new ArrayList<Fund>();

    public InMemoryFundDao() {
        funds.add(new Fund("IYH", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IBB", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IDU", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYK", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IGE", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYE", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYM", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYZ", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYC", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYF", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYJ", "", "", "", false, "", "", "", new LocalDate()));
        funds.add(new Fund("IYT", "", "", "", false, "", "", "", new LocalDate()));
    }


    public Collection<Fund> getAll() {
        return funds;
    }
}
