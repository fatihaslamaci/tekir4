package com.ozguryazilim.tekir.recruit.jobadvert;

import com.ozguryazilim.tekir.entities.JobAdvert_;
import com.ozguryazilim.tekir.entities.JobAdvert;
import com.ozguryazilim.tekir.recruit.config.RecruitPages;
import com.ozguryazilim.telve.lookup.Lookup;
import com.ozguryazilim.telve.data.RepositoryBase;
import com.ozguryazilim.telve.lookup.LookupTableControllerBase;
import com.ozguryazilim.telve.lookup.LookupTableModel;
import javax.inject.Inject;

/**
 * JobAdvert için Lookup Dialog Sınıfı.
 * 
 * @author Erdem Uslu
 */
@Lookup(dialogPage = RecruitPages.JobAdvertPages.JobAdvertLookup.class)
public class JobAdvertLookup extends LookupTableControllerBase<JobAdvert, JobAdvertViewModel>  {

    @Inject
    private JobAdvertRepository jobAdvertRepository;

    @Override
    protected void buildModel(LookupTableModel<JobAdvertViewModel> model) {
        model.addColumn("code", "general.label.SerialNumber");
        model.addColumn("topic", "general.label.Topic");
    }

    @Override
    protected RepositoryBase<JobAdvert, JobAdvertViewModel> getRepository() {
        return jobAdvertRepository;
    }

    @Override
    public String getCaptionFieldName() {
        return JobAdvert_.topic.getName();
    }
    
}
