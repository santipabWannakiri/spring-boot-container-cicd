package com.container.cicd.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LogInfo {
    @JsonProperty
    private String className;
    @JsonProperty
    private String methodName;
    @JsonProperty
    private Object payload;

    public LogInfo(String className, String methodName, Object payload) {
        this.className = className;
        this.methodName = methodName;
        this.payload = payload;
    }
}
