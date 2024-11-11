package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.BuildTypes;
import com.example.teamcity.api.models.Project;


import com.example.teamcity.ui.elements.ErrorElement;
import com.example.teamcity.ui.pages.BuildTypePage;
import com.example.teamcity.ui.pages.admin.CreateBuildTypePage;

import org.testng.annotations.Test;




import static com.example.teamcity.api.enums.Endpoint.*;


@Test
public class CreateBuildTypeTest extends BaseUiTest{

    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able create build type for existing project", groups = {"Positive"})
    public void userCreatesBuildType(){

        //precondition: login as user and create project on API level
        loginAs(testData.getUser());
        superUserCheckRequest.<Project>getRequest(PROJECTS).create(testData.getProject());

        //create build type via UI for created project

        CreateBuildTypePage.open(testData.getProject().getId())
                        .createForm(REPO_URL)
                                .setupBuildType(testData.getBuildType().getName());

        //check that build type was created on API level

        var createdBuildType = superUserCheckRequest.<BuildType>getRequest(BUILD_TYPES).read("name:" + testData.getBuildType().getName());
        softy.assertNotNull(createdBuildType);

        //check that it is possible to open created project via UI

        BuildTypePage.open(createdBuildType.getId())
                .title.shouldHave(Condition.exactText(testData.getBuildType().getName()));


    }

    @Test(description = "User should not be able to create build type without name", groups = {"Negative"})
    public void userCreatesBuildWithoutName(){

        //precondition: login as user and create project on API level
        loginAs(testData.getUser());
        superUserCheckRequest.<Project>getRequest(PROJECTS).create(testData.getProject());

        // check amount of build types on server before test
        var amountOfBTBeforeTest = superUserCheckRequest.<BuildTypes>getRequest(BUILD_TYPES_LIST).readAll().getCount();

        //create build type via UI for created project
        CreateBuildTypePage.open(testData.getProject().getId())
                .createForm(REPO_URL)
                .setupBuildType("");

        //check that error message "Build configuration name must not be empty" is shown
        ErrorElement.searchErrorValidationText("buildTypeName","Build configuration name must not be empty");

        //check that amount of build types after test is the same as before
        var amountOfBTAfterTest = superUserCheckRequest.<BuildTypes>getRequest(BUILD_TYPES_LIST).readAll().getCount();
        softy.assertEquals(amountOfBTBeforeTest, amountOfBTAfterTest);
    }
}
