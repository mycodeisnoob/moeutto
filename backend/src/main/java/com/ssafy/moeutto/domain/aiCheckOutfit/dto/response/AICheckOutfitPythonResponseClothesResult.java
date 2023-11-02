package com.ssafy.moeutto.domain.aiCheckOutfit.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AICheckOutfitPythonResponseClothesResult {

    int id;
    String largeCategoryId;
    String imageUrl;

    @Builder(toBuilder = true)
    public AICheckOutfitPythonResponseClothesResult(int id, String largeCategoryId, String imageUrl){
        this.id = id;
        this.largeCategoryId = largeCategoryId;
        this.imageUrl = imageUrl;
    }
}
