package com.rudra.financemanager.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private TransactionTypeEnum type;
    @JsonProperty("isCustom")
    private boolean isCustom;
}
