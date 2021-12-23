package com.mindspore.ide.toolkit.search.entity;

import com.intellij.openapi.actionSystem.AnAction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public class DocumentSearch {
    private final Object value;

    private final String pattern;
    private final int matchingDegree;

    public DocumentSearch(@NotNull Object value, @NotNull String pattern, int matchingDegree) {
        this.value = value;
        this.pattern = pattern;
        this.matchingDegree = matchingDegree;
    }

    public String getValueText(){
        if(value instanceof DocumentValue){
            return ((DocumentValue)value).getTitle();
        }
        if(value instanceof AnAction){
            return ((AnAction)value).getTemplatePresentation().getText();
        }
        return "";
    }

    @Override
    public String toString(){
        return getValueText();
    }

    public int getMatchingDegree(){
        return matchingDegree;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        if(obj instanceof DocumentSearch){
            DocumentSearch value1 = (DocumentSearch) obj;
            return Objects.equals(value,value1.value) && Objects.equals(pattern,value1.pattern);
        }
        return Objects.equals(value,obj) && Objects.equals(pattern,obj);
    }

    @Override
    public int hashCode(){
        return Objects.hash(value,pattern);
    }
}
