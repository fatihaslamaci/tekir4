/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozguryazilim.finance.account;

import com.ozguryazilim.finance.account.txn.FinanceAccountTxnRepository;
import com.ozguryazilim.finance.config.FinancePages;
import com.ozguryazilim.tekir.entities.AccountType;
import com.ozguryazilim.tekir.entities.FinanceAccount;
import com.ozguryazilim.tekir.entities.FinanceAccountTxn;
import com.ozguryazilim.telve.auth.Identity;
import com.ozguryazilim.telve.entities.FeaturePointer;
import com.ozguryazilim.telve.feature.FeatureHandler;
import com.ozguryazilim.telve.forms.FormBase;
import com.ozguryazilim.telve.forms.FormEdit;
import com.ozguryazilim.telve.messages.FacesMessages;
import com.ozguryazilim.telve.utils.DateUtils;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author oyas
 */
@FormEdit(feature = FinanceAccountFeature.class)
public class FinanceAccountHome extends FormBase<FinanceAccount, Long> {

    @Inject
    private FinanceAccountRepository repository;

    @Inject
    private FinanceAccountTxnRepository txnRepository;

    @Inject
    private Identity identity;

    @Inject
    private ViewNavigationHandler viewNavigationHandler;

    private List<String> selectedRoles = new ArrayList<>();

    private List<FinanceAccountTxn> accountTxns;
    

    private List<FinanceAccountBalanceModel> balanceModels;

    private LineChartModel chartModel;
    private BigDecimal balance;
    private BigDecimal takeOverTotal;

    @Override
    public FinanceAccountRepository getRepository() {
        return this.repository;
    }

    public Class<? extends ViewConfig> newCashAccount() {
        FinanceAccount p = new FinanceAccount();
        p.getAccountRoles().add("CASH");
        p.setType(AccountType.CASH);
        p.setOwner(identity.getLoginName());
        setEntity(p);
        selectedRoles.clear();
        return FinancePages.FinanceAccount.class;
    }

    public Class<? extends ViewConfig> newBankAccount() {
        FinanceAccount p = new FinanceAccount();
        p.getAccountRoles().add("BANK");
        p.setType(AccountType.BANK);
        p.setOwner(identity.getLoginName());
        setEntity(p);
        selectedRoles.clear();
        return FinancePages.FinanceAccount.class;
    }

    public Class<? extends ViewConfig> newCreditCardAccount() {
        FinanceAccount p = new FinanceAccount();
        p.getAccountRoles().add("CREDIT_CARD");
        p.setType(AccountType.CREDIT_CARD);
        p.setOwner(identity.getLoginName());
        setEntity(p);
        selectedRoles.clear();
        return FinancePages.FinanceAccount.class;
    }

    @Override
    public boolean onBeforeSave() {
        //Eğer person ise name alanını düzeltmek lazım
        if (getEntity().getType() == AccountType.BANK) {
            getEntity().setName(getEntity().getBank() + " " + getEntity().getBranch() + " " + getEntity().getAccountNo());
        }

        //Önce kullanıcı seçimli olmayan rolleri bir toparlayalım
        List<String> ls = getEntity().getAccountRoles().stream()
                .filter(p -> !getAccountRoles().contains(p))
                .collect(Collectors.toList());

        //Şimdi kullanıcın seçtiklerini ekleyelim
        ls.addAll(selectedRoles);

        //Şimdi de yeni durumu yerleştirelim.
        getEntity().getAccountRoles().clear();
        getEntity().getAccountRoles().addAll(ls);

        return super.onBeforeSave(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onAfterLoad() {

        //FIXME: Burayı generic bir hale getirmek lazım                
        if (!identity.isPermitted("financeAccount:select:" + getEntity().getOwner())) {
            FacesMessages.error("Kayda erişim için yetkiniz yok!");
            createNew();
            viewNavigationHandler.navigateTo(FinancePages.FinanceAccountBrowse.class);
            return false;
        }

        selectedRoles = getEntity().getAccountRoles().stream()
                .filter(p -> getAccountRoles().contains(p))
                .collect(Collectors.toList());

        balanceModels = null;
        chartModel = null;
        balance = null;
        takeOverTotal = null;
        
        return super.onAfterLoad();
    }

    public List<String> getAccountRoles() {
        return AccountRoleRegitery.getSelectableAccountRoles();
    }

    public List<String> getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(List<String> selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    @Override
    public Class<? extends FeatureHandler> getFeatureClass() {
        return FinanceAccountFeature.class;
    }

    public FeaturePointer getFeaturePointer() {
        FeaturePointer result = new FeaturePointer();
        result.setBusinessKey(getEntity().getName());
        result.setFeature(getFeatureClass().getSimpleName());
        result.setPrimaryKey(getEntity().getId());
        return result;
    }

    public List<FinanceAccountBalanceModel> getBalanceModels() {
        if( balanceModels == null ){
            refreshTxns();
        }
        return balanceModels;
    }

    public BigDecimal getBalance() {
        if (balance == null) {
            balance = txnRepository.findByAccountBalance(getEntity(), new Date());
            if (balance == null) {
                balance = BigDecimal.ZERO;
            }
        }
        return balance;
    }

    public List<FinanceAccountTxn> getAccountTxns() {
        if (accountTxns == null) {
            refreshTxns();
        }
        return accountTxns;
    }

    public LineChartModel getChartModel() {
        if (chartModel == null) {
            refreshTxns();
        }
        return chartModel;
    }

    protected void refreshTxns() {
        //FIXME: Buradaki period UI'dan alınmalı.
        Date dt = DateUtils.getDateBeforePeriod("10d", new Date());
        accountTxns = txnRepository.findByAccountAndDateGreaterThanEqualsOrderByDateAsc(getEntity(), dt);
        takeOverTotal = txnRepository.findByAccountBalance(getEntity(), dt);
        if (takeOverTotal == null) {
            takeOverTotal = BigDecimal.ZERO;
        }
        buildBalanceModel( dt );
        buildChartModel( dt );
    }

    protected void buildChartModel( Date startDate) {
        chartModel = new LineChartModel();

        LineChartSeries debitSeries = new LineChartSeries();
        debitSeries.setLabel("Debit");
        debitSeries.setShowLine(false);
        //debitSeries.setDisableStack(true);

        LineChartSeries creditSeries = new LineChartSeries();
        creditSeries.setLabel("Credit");
        creditSeries.setShowLine(false);
        //creditSeries.setDisableStack(true);

        LineChartSeries stateSeries = new LineChartSeries();
        stateSeries.setLabel("Balance");
        stateSeries.setFill(true);
        stateSeries.setDisableStack(true);
        stateSeries.setFillAlpha(0.2);
        
        //TODO: Configden ( dil dosyalarından ) alınlı i18n
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        for( FinanceAccountBalanceModel bm : balanceModels ){
            String dts = df.format(bm.getDate());
            stateSeries.set(dts, bm.getBalance());
            if( "TXN".equals(bm.getLineType())){
                if( bm.getAmount().compareTo(BigDecimal.ZERO) < 1 ){
                    BigDecimal bd = (BigDecimal) debitSeries.getData().get(dts);
                    if( bd != null ){
                        debitSeries.set(dts, bm.getAmount().add(bd));
                    } else {
                        debitSeries.set(dts, bm.getAmount());
                    }
                } else {
                    BigDecimal bd = (BigDecimal) creditSeries.getData().get(dts);
                    if( bd != null ){
                        creditSeries.set(dts, bm.getAmount().add(bd));
                    } else {
                        creditSeries.set(dts, bm.getAmount());
                    }
                }
            } 
        }
        
        
        chartModel.addSeries(stateSeries);
        chartModel.addSeries(creditSeries);
        chartModel.addSeries(debitSeries);
        
        //TODO: Bu renk işini temaya almak lazım
        //lineChartModel.setSeriesColors("3c8dbc,f56954,dd4b39,dd4b39");
        chartModel.setExtender("chartExt");
        
        
        
        //chartModel.setTitle("States");
        chartModel.setLegendPosition("nw");
        chartModel.setAnimate(true);
        chartModel.setShadow(false);
        //chartModel.setStacked(true);

        //Axis xAxis = new CategoryAxis();
        //lineChartModel.getAxes().put(AxisType.X, xAxis);
        
        DateAxis axis = new DateAxis("Dates");
        //axis.setTickAngle(-50);
        axis.setMin(df.format(startDate));
        axis.setMax(df.format(new Date()));
        axis.setTickFormat("%#d %b");
        chartModel.getAxes().put(AxisType.X, axis);

        Axis yAxis = chartModel.getAxis(AxisType.Y);
        yAxis.setLabel(getEntity().getCurrency().getDisplayName());
    }

    private void buildBalanceModel( Date startDate ) {

        balanceModels = new ArrayList<>();

        FinanceAccountBalanceModel m = new FinanceAccountBalanceModel();
        m.setLineType("TAKE-OVER");
        //TODO: i18n
        m.setDate(startDate);
        m.setTopic("Take Over");
        m.setAmount(BigDecimal.ZERO);
        m.setBalance(takeOverTotal);
        balanceModels.add(m);

        for (FinanceAccountTxn txn : accountTxns) {
            m = new FinanceAccountBalanceModel();

            m.setFeaturePointer(txn.getFeature());
            m.setTopic(txn.getInfo());
            if (txn.getDebit()) {
                m.setAmount(txn.getAmount().negate());
                takeOverTotal = takeOverTotal.subtract(txn.getLocalAmount());
            } else {
                m.setAmount(txn.getAmount());
                takeOverTotal = takeOverTotal.add(txn.getLocalAmount());
            }
            m.setCcy(txn.getCurrency());
            m.setBalance(takeOverTotal);
            m.setDate(txn.getDate());
            balanceModels.add(m);
        }
        
        m = new FinanceAccountBalanceModel();
        m.setDate(new Date());
        m.setLineType("RESULT");
        //TODO: i18n
        m.setTopic("Result");
        m.setAmount(BigDecimal.ZERO);
        m.setBalance(takeOverTotal);
        balanceModels.add(m);

    }

}
