package org.unitedlands.items.util;

import java.io.Serial;
import java.io.Serializable;

public class CropData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String cropId;
    private final int growthStage;

    public CropData(String cropId, int growthStage) {
        this.cropId = cropId;
        this.growthStage = growthStage;
    }

    public String getCropId() {
        return cropId;
    }

    public int getGrowthStage() {
        return growthStage;
    }
}