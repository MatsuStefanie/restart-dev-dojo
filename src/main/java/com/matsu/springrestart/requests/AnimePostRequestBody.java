package com.matsu.springrestart.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimePostRequestBody {

    @NotBlank(message = "The anime name cannot be empty or null")
    @Schema(description = "The anime name", example = "Boko no hero")
    private String name;
}
