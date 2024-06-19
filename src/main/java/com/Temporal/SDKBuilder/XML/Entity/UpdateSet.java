package com.Temporal.SDKBuilder.XML.Entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSet {
    @JacksonXmlProperty(localName = "unload_date")
    private String unloadDate;

    @JacksonXmlProperty(localName = "sys_update_xml")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<UpdateItem> items;
}
