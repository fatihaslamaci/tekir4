/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozguryazilim.finance.virement;

import javax.enterprise.inject.Default;

import com.ozguryazilim.finance.config.FinancePages;
import com.ozguryazilim.tekir.entities.FinanceAccountVirement;
import com.ozguryazilim.tekir.voucher.Voucher;
import com.ozguryazilim.telve.feature.AbstractFeatureHandler;
import com.ozguryazilim.telve.feature.Feature;
import com.ozguryazilim.telve.feature.Page;
import com.ozguryazilim.telve.feature.PageType;

/**
 *
 * @author oyas
 */
@Feature(permission = "financeAccountVirement", forEntity = FinanceAccountVirement.class )
@Page( type = PageType.BROWSE, page = FinancePages.FinanceAccountVirementBrowse.class )
@Page( type = PageType.VIEW, page = FinancePages.FinanceAccountVirementView.class )
@Page( type = PageType.MASTER_VIEW, page = FinancePages.FinanceAccountVirementMasterView.class )
@Page( type = PageType.EDIT, page = FinancePages.FinanceAccountVirement.class )
@Voucher @Default
public class FinanceAccountVirementFeature extends AbstractFeatureHandler{
    
}
