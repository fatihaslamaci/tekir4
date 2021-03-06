package com.ozguryazilim.tekir.voucher.group;

import com.ozguryazilim.tekir.entities.Contact;
import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import com.ozguryazilim.tekir.entities.VoucherGroup;
import com.ozguryazilim.tekir.entities.VoucherGroupStatus;
import com.ozguryazilim.telve.feature.FeatureHandler;
import com.ozguryazilim.telve.sequence.SequenceManager;
import javax.inject.Inject;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

/**
 *
 * @author oyas
 */
@Dependent
@Named
public class VoucherGroupService implements Serializable{
    
    @Inject
    private VoucherGroupRepository repository;

    @Inject
    private SequenceManager sequenceManager;
    
    @Transactional
    public VoucherGroup saveVoucherGroup( VoucherGroup voucherGroup){
        //TODO: Prefix'i configden alsak iyi olur
    	voucherGroup.setGroupNo(getNewSerialNumber());
        
    	voucherGroup = repository.save(voucherGroup);
        
        return voucherGroup;
    }
    
    public VoucherGroup findByVoucherGroupNo( String processNo ){
        return repository.findAnyByGroupNo( processNo );
    }
    
    public String getNewSerialNumber(){       
        return sequenceManager.getNewSerialNumber("VG", 6);
    }
}
