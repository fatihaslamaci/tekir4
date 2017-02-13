/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozguryazilim.opportunity.dashlets;

import com.ozguryazilim.mutfak.kahve.Kahve;
import com.ozguryazilim.mutfak.kahve.KahveEntry;
import com.ozguryazilim.mutfak.kahve.annotations.UserAware;
import com.ozguryazilim.tekir.account.AccountTxnRepository;
import com.ozguryazilim.tekir.account.AccountTxnSumModel;
import com.ozguryazilim.tekir.entities.VoucherState;
import com.ozguryazilim.tekir.opportunity.OpportunityRepository;
import com.ozguryazilim.tekir.quote.QuoteRepository;
import com.ozguryazilim.telve.auth.Identity;
import com.ozguryazilim.telve.dashboard.AbstractDashlet;
import com.ozguryazilim.telve.dashboard.Dashlet;
import com.ozguryazilim.telve.dashboard.DashletCapability;
import com.ozguryazilim.telve.utils.DateUtils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author oyas
 */
@Dashlet(capability = {DashletCapability.canHide, DashletCapability.canEdit, DashletCapability.canMinimize, DashletCapability.canRefresh})
public class SalesStatusDashlet extends AbstractDashlet{
    
    @Inject @UserAware
    private Kahve kahve;
    
    @Inject
    private QuoteRepository quoteRepository;
    
    @Inject
    private OpportunityRepository opportunityRepository;
    
    @Inject
    private Identity identity;      
    
    private Boolean mineOnly = Boolean.FALSE; 
    private Boolean openOnly = Boolean.TRUE;
    
    private PieChartModel budgetPieChartModel;
    private PieChartModel piecePieChartModel;

    private BigDecimal opportunityBudgets = BigDecimal.ZERO;
    private BigDecimal quoteBudgets = BigDecimal.ZERO;
    private Integer opportunityPieces = 0;
    private Integer quotePieces = 0;
    
    
    private String style="TABLE"; //CHART|TABLE
            
    
    @Override
    public void load() { 
        KahveEntry ke = kahve.get("salesStatusDashlet.mineOnly");
        if( ke != null ){
            mineOnly = ke.getAsBoolean();
        }
        
        ke = kahve.get("salesStatusDashlet.openOnly");
        if( ke != null ){
            openOnly = ke.getAsBoolean();
        }
        
        ke = kahve.get("salesStatusDashlet.style");
        if( ke != null ){
            style = ke.getAsString();
        }
        
        populate();
        
    }
    
    @Override
    public void save(){
        kahve.put( "salesStatusDashlet.mineOnly", mineOnly);
        kahve.put( "salesStatusDashlet.style", style);
        kahve.put( "salesStatusDashlet.openOnly", openOnly);
        refresh();
    }

    protected void populate(){       
        opportunityBudgets = BigDecimal.ZERO;
        opportunityPieces = 0;
        
        quoteBudgets = BigDecimal.ZERO;
        quotePieces = 0;
        String username = "";
        
        if( getMineOnly() ){
            username = identity.getLoginName();
        }
        
        VoucherState state = null;
        if(getOpenOnly()){
        	state = VoucherState.OPEN;
        }
        
        opportunityBudgets = opportunityRepository.sumLocalBudgetByStateAndOwner(state, username);
        
        opportunityPieces = opportunityRepository.countByStateAndOwner(state, username).intValue(); 
        
        quoteBudgets = quoteRepository.sumLocalBudgetByStateAndOwner(state, username);
        
        quotePieces = quoteRepository.countByStateAndOwner(state, username).intValue();  

        
        buildChartModel();
    }
    
    protected void buildChartModel(){
        budgetPieChartModel = new PieChartModel();
        
        budgetPieChartModel.set("general.label.Opportunity", opportunityBudgets);
        budgetPieChartModel.set("general.label.Quote", quoteBudgets);        
        
        
        budgetPieChartModel.setTitle("Budgets");
        budgetPieChartModel.setLegendPosition("e");
        budgetPieChartModel.setFill(false);
        budgetPieChartModel.setShowDataLabels(true);
        budgetPieChartModel.setShowDatatip(true);
        budgetPieChartModel.setDiameter(150);
        
        piecePieChartModel = new PieChartModel();
        
        piecePieChartModel.set("general.label.Opportunity", opportunityPieces);
        piecePieChartModel.set("general.label.Quote", quotePieces);       
        
        piecePieChartModel.setTitle("Pieces");
        piecePieChartModel.setLegendPosition("e");
        piecePieChartModel.setFill(false);
        piecePieChartModel.setShowDataLabels(true);
        piecePieChartModel.setShowDatatip(true);
        piecePieChartModel.setDiameter(150);
    }
    
    @Override
    public void refresh() {
        populate();
    }

    public Boolean getMineOnly() {
        return mineOnly;
    }

    public void setMineOnly(Boolean mineOnly) {
        this.mineOnly = mineOnly;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

	public Boolean getOpenOnly() {
		return openOnly;
	}

	public void setOpenOnly(Boolean openOnly) {
		this.openOnly = openOnly;
	}

	public QuoteRepository getQuoteRepository() {
		return quoteRepository;
	}

	public OpportunityRepository getOpportunityRepository() {
		return opportunityRepository;
	}

	public BigDecimal getOpportunityBudgets() {
		return opportunityBudgets;
	}

	public BigDecimal getQuoteBudgets() {
		return quoteBudgets;
	}

	public Integer getOpportunityPieces() {
		return opportunityPieces;
	}

	public Integer getQuotePieces() {
		return quotePieces;
	}

	public PieChartModel getBudgetPieChartModel() {
		return budgetPieChartModel;
	}

	public PieChartModel getPiecePieChartModel() {
		return piecePieChartModel;
	}

    
    
    
}
