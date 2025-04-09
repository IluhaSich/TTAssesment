package com.project.tta.viewModels;

import java.util.List;

public class AllGradesViewModel{
    public List<GroupViewModel> groupViewModelList;
    public List<GroupViewModel> bestGroupGrades;
    public List<GroupViewModel> worstGroupGrades;

    public AllGradesViewModel(List<GroupViewModel> groupViewModelList, List<GroupViewModel> bestGroupGrades, List<GroupViewModel> worstGroupGrades) {
        this.groupViewModelList = groupViewModelList;
        this.bestGroupGrades = bestGroupGrades;
        this.worstGroupGrades = worstGroupGrades;
    }
}
