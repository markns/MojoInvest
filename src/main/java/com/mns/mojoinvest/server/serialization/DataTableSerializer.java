package com.mns.mojoinvest.server.serialization;

import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.render.JsonRenderer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class DataTableSerializer extends JsonSerializer<DataTable> {

    @Override
    public void serialize(DataTable dataTable, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        jsonGenerator.writeString(JsonRenderer.renderDataTable(dataTable, true, true, true).toString());
    }
}
