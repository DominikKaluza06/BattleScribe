package com.example.battlescribe;

import java.util.Map;

public class Recipe {
    public int resultItemId;
    public Map<Integer, Integer> materialsRequired; // Material ID -> Quantity

    public Recipe(int resultItemId, Map<Integer, Integer> materialsRequired) {
        this.resultItemId = resultItemId;
        this.materialsRequired = materialsRequired;
    }
}
