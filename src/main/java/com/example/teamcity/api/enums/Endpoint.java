package com.example.teamcity.api.enums;

import com.example.teamcity.api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class),
    BUILD_TYPES_LIST("/app/rest/buildTypes", BuildTypes.class),
    PROJECTS("/app/rest/projects", Project.class),
    USERS("/app/rest/users", User.class);
    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
