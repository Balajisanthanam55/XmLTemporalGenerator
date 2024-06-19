package com.Temporal.SDKBuilder.XML.Entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class UpdateItem {
    private String action;
    private String application;
    private String category;
    private String comments;
    private String name;

    @JacksonXmlProperty(localName = "remote_update_set")
    private String remoteUpdateSet;

    private boolean replaceOnUpgrade;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysId;
    private int sysModCount;
    private String sysRecordedAt;
    private String sysUpdatedBy;
    private String sysUpdatedOn;
    private String table;
    @JacksonXmlProperty(localName = "target_name")
    private String targetName;
    private String type;
    private String updateDomain;
    private String updateGuid;
    private String updateGuidHistory;
    private String updateSet;
    private String view;
}
