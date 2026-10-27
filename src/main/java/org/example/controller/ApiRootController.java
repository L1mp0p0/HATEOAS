package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
@Tag(name = "Root", description = "Точка входа в API")
public class ApiRootController {

    @Operation(summary = "Точка входа",
            description = "Возвращает ссылки на корневые ресурсы API и на документацию.")
    @GetMapping
    public ApiRootModel root() {
        ApiRootModel model = new ApiRootModel();
        model.add(
                linkTo(methodOn(ApiRootController.class).root()).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"),
                Link.of(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/swagger-ui.html").toUriString(), "docs")
        );
        return model;
    }

    @io.swagger.v3.oas.annotations.media.Schema(description = "Точка входа: только ссылки (_links)")
    public static class ApiRootModel extends RepresentationModel<ApiRootModel> {
    }
}
