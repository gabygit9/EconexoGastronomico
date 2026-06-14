package com.tfi.econexo.dto.auth.admin;

import com.tfi.econexo.model.enums.RegistrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A UpdateStatusRequestDTO object represents a request to update the status of a user.")
public record UpdateStatusRequestDTO(

        @Schema(description = "The new status of the user.", example = "APPROVED")
        RegistrationStatus status
) {
}
