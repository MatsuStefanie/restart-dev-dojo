package com.matsu.springrestart.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimePostRequestBody {

    @NotBlank(message = "The anime name cannot be empty or null")
    private String name;
}
