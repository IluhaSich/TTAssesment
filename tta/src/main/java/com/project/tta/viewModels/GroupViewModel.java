package com.project.tta.viewModels;

import java.util.List;

public class GroupViewModel {
    public String name;
    public String link;
    public Integer totalGrade;
    public List<CriterionsModelView> criterionModelViews;

    public GroupViewModel(String name, String link, Integer totalGrade, List<CriterionsModelView> criterionModelViews) {
        this.name = name;
        this.link = link;
        this.totalGrade = totalGrade;
        this.criterionModelViews = criterionModelViews;
    }
}
