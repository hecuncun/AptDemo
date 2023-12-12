package com.hcc.annotation_compile;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by hecuncun on 2023/12/12
 */

public class ElementForType {
    List<VariableElement> viewElements;
    List<ExecutableElement> methodElement;

    public List<VariableElement> getViewElements() {
        return viewElements;
    }

    public void setViewElements(List<VariableElement> viewElements) {
        this.viewElements = viewElements;
    }

    public List<ExecutableElement> getMethodElement() {
        return methodElement;
    }

    public void setMethodElement(List<ExecutableElement> methodElement) {
        this.methodElement = methodElement;
    }
}
