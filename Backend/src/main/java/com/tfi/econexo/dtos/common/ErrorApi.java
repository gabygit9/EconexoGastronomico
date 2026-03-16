package com.tfi.econexo.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorApi {

    private String timeStamp;

    private Integer status;

    private String error;

    private String message;
}
