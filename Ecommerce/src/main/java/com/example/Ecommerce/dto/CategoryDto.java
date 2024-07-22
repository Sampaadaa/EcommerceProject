//Data transfer object ,data is transferred between client and server

package com.example.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private long categoryId;
    private String categoryName;
}
