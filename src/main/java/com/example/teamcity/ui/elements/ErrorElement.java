package com.example.teamcity.ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ErrorElement {

    private static final String ERROR_ID_PREFIX = "#error_%s";

    public static void searchErrorValidationText(String selector, String errorMessage){
        var error = $(ERROR_ID_PREFIX.formatted(selector));
        error.shouldHave(Condition.exactText(errorMessage));
    }
}
