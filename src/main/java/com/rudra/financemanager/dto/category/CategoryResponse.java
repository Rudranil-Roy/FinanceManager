package com.rudra.financemanager.dto.category;

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
    private boolean isCustom;
}
