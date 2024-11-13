package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest{
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {

        //creating a user with super user auth
        superUserCheckRequest.getRequest(USERS).create(testData.getUser());

        //user can now send requests
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        //creating a project
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        //creating a buildType
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        //check that buildType was successfully created
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:"+testData.getBuildType().getId());
        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");

    }

    @Test(description = "User should not be able to create two build types with the same ID", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIDTest() {


        //creating a user with super user auth
        superUserCheckRequest.getRequest(USERS).create(testData.getUser());

        //user can now send requests
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        //creating a project
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());


        //generating buildType2 with the same ID as in buildType1
        var buildTypeWithSameID = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        //creating buildType1
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        //creating buildType2
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameID).then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));

    }

    @Test(description = "Project Admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {

        //generating project
        superUserCheckRequest.getRequest(PROJECTS).create(testData.getProject());

        //generating a user with project admin role
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        //creating a user with project admin role
        superUserCheckRequest.getRequest(USERS).create(testData.getUser());

        //creating build Type by project admin
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        //check that buildType was successfully created
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:"+testData.getBuildType().getId());
        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");

    }

    @Test(description = "Project Admin should be not able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherProjectTest() {

        //generating and creating project1
        var project1 = testData.getProject();
        superUserCheckRequest.getRequest(PROJECTS).create(project1);

        //generating and creating user1 with project admin role
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequest.getRequest(USERS).create(testData.getUser());

        //generating and creating project2
        var newTestData = generate();
        var project2 = newTestData.getProject();
        superUserCheckRequest.getRequest(PROJECTS).create(project2);

        //creating buildType2 by project admin1
        var userUnCheckRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        userUnCheckRequests.getRequest(BUILD_TYPES).create(newTestData.getBuildType())
            .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
            .body(Matchers.containsString("You do not have enough permissions to edit project with id: %s".formatted(newTestData.getProject().getId())));

    }
}