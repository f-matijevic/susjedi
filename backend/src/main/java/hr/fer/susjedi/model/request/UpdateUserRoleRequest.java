package hr.fer.susjedi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {

    @NotBlank(message = "Uloga je obavezna")
    @Pattern(regexp = "ADMIN|PREDSTAVNIK|SUVLASNIK",
            message = "Neispravna uloga")
    private String role;
}