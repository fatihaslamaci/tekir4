package com.ozguryazilim.tekir.recruit.jobapplication;

import com.ozguryazilim.tekir.entities.JobApplication;
import com.ozguryazilim.tekir.recruit.config.RecruitPages;
import com.ozguryazilim.telve.feature.AbstractFeatureHandler;
import com.ozguryazilim.telve.feature.Feature;
import com.ozguryazilim.telve.feature.Page;
import com.ozguryazilim.telve.feature.PageType;
/**
 *
 * @author yusuf
 */
@Feature(permission="jobapplication",forEntity = JobApplication.class)
@Page(type = PageType.VIEW, page = RecruitPages.jobapplication.JobApplicationView.class)
@Page(type = PageType.MASTER_VIEW, page = RecruitPages.jobapplication.JobApplicationMasterView.class)
@Page(type=PageType.BROWSE,page=RecruitPages.jobapplication.JobApplicationBrowse.class)
@Page(type=PageType.EDIT,page=RecruitPages.jobapplication.JobApplication.class)
public class JobApplicationFeature extends AbstractFeatureHandler{
    
}
