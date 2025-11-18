package org.opendatamesh.platform.pp.notification.rest.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Date;

public abstract class TimestampedResourceV1 implements Serializable {

    @Schema(description = "The creation timestamp. Automatically handled by the API: can not be modified.")
    private Date createdAt;

    @Schema(description = "The last update timestamp. Automatically handled by the API: can not be modified.")
    private Date updatedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}