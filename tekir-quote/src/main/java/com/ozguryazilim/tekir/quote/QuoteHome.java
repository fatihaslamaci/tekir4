package com.ozguryazilim.tekir.quote;

import com.ozguryazilim.tekir.core.currency.CurrencyService;
import com.ozguryazilim.tekir.entities.Commodity;
import com.ozguryazilim.tekir.entities.Contact;
import com.ozguryazilim.tekir.entities.Quantity;
import com.ozguryazilim.telve.forms.FormEdit;
import com.ozguryazilim.tekir.entities.Quote;
import com.ozguryazilim.tekir.entities.QuoteItem;
import com.ozguryazilim.tekir.entities.QuoteSummary;
import com.ozguryazilim.tekir.entities.TaxDefinition;
import com.ozguryazilim.tekir.quote.config.QuotePages;
import com.ozguryazilim.tekir.voucher.VoucherFormBase;
import com.ozguryazilim.telve.data.RepositoryBase;
import com.ozguryazilim.telve.entities.FeaturePointer;
import com.ozguryazilim.telve.lookup.LookupSelectTuple;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Home Control Class
 *
 * @author
 */
@FormEdit(browsePage = QuotePages.QuoteBrowse.class, editPage = QuotePages.Quote.class, viewContainerPage = QuotePages.QuoteView.class, masterViewPage = QuotePages.QuoteMasterView.class)
public class QuoteHome extends VoucherFormBase<Quote> {

    @Inject
    private QuoteRepository repository;

    @Inject
    private CurrencyService currencyService;

    private QuoteItem selectedItem;

    @Override
    protected RepositoryBase<Quote, QuoteViewModel> getRepository() {
        return repository;
    }

    @Override
    public void createNew() {
        super.createNew();

        getEntity().setCurrency(currencyService.getDefaultCurrency().getCurrencyCode());
    }

    public void addItem() {
        QuoteItem item = new QuoteItem();
        item.setQuantity(new Quantity(BigDecimal.ZERO, "HDE:Karton"));
        //item.setPrice(new Money(BigDecimal.ZERO, "TRY"));
        //item.setTotal(new Money(BigDecimal.ZERO, "TRY"));
        item.setMaster(getEntity());
        selectedItem = item;
    }

    public void editItem(QuoteItem item) {
        selectedItem = item;
    }

    public void deleteIten(QuoteItem item) {
        getEntity().getItems().remove(item);
    }

    public void saveItem() {
        getEntity().getItems().add(selectedItem);
    }

    public QuoteItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(QuoteItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public boolean onBeforeSave() {

        calcSummaries();
        
        //TODO: Burada vergi hesaplama felan da yapmak lazım.
        return super.onBeforeSave();
    }

    public void onCommoditySelect(SelectEvent event) {
        List<Commodity> ls = getCommodities(event);
        addCommodities(ls);
    }

    /**
     * Verilen emtiaları detay olarak ekler
     *
     * @param comps
     */
    protected void addCommodities(List<Commodity> comps) {
        for (Commodity c : comps) {
            if (!isCommodityAdded(c)) {
                QuoteItem it = new QuoteItem();
                it.setMaster(getEntity());
                it.setCommodity(c);
                it.setQuantity(new Quantity(BigDecimal.ZERO, c.getDefaultUnit()));
                it.setPrice(c.getPrice());
                getEntity().getItems().add(it);
            }
        }
    }

    /**
     * Verilen şikayetin daha önce listeye eklenip eklenmediğine bakar.
     *
     * @param c
     * @return
     */
    protected boolean isCommodityAdded(Commodity c) {
        for (QuoteItem it : getEntity().getItems()) {
            if (it.getCommodity().equals(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Seçim eventinden complain listesini döndürür.
     *
     */
    @SuppressWarnings({"unchecked"})
    private List<Commodity> getCommodities(SelectEvent event) {
        LookupSelectTuple tuple = (LookupSelectTuple) event.getObject();

        List<Commodity> ls = new ArrayList<>();
        if (tuple != null) {
            if (tuple.getValue() instanceof List) {
                ls.addAll((List<Commodity>) tuple.getValue());
            } else {
                ls.add((Commodity) tuple.getValue());
            }
        }

        return ls;
    }

    /**
     * Create new item line
     */
    public void addComplain() {
        QuoteItem it = new QuoteItem();
        it.setMaster(getEntity());
        getEntity().getItems().add(it);
    }

    /**
     * Removes item line
     *
     * @param index
     */
    public void removeItem(int index) {
        getEntity().getItems().remove(index);
        calcSummaries();
    }

    public void onCellEdit(CellEditEvent event) {
        //QuoteItem it = (QuoteItem)((DataTable)event.getComponent()).getRowData();
        QuoteItem it = getEntity().getItems().get(event.getRowIndex());

        String id = event.getColumn().getClientId();
        if (id.contains("amount")) {
            it.setTotal(it.getQuantity().getAmount().multiply(it.getPrice()));
        } else if (id.contains("price")) {
            it.setTotal(it.getQuantity().getAmount().multiply(it.getPrice()));
        } else if (id.contains("total")) {
            it.setPrice(it.getTotal().divide(it.getQuantity().getAmount()));
        }
        
        calcSummaries();
    }

    public void onAmountChange(int inx) {
        QuoteItem it = getEntity().getItems().get(inx);
        it.setTotal(it.getQuantity().getAmount().multiply(it.getPrice()));
        calcSummaries();
    }
    
    public void onPriceChange(int inx) {
        QuoteItem it = getEntity().getItems().get(inx);
        it.setTotal(it.getQuantity().getAmount().multiply(it.getPrice()));
        calcSummaries();
    }
    
    public void onTotalChange(int inx) {
        QuoteItem it = getEntity().getItems().get(inx);
        it.setPrice(it.getTotal().divide(it.getQuantity().getAmount()));
        calcSummaries();
    }

    
    public void calcSummaries(){
        getEntity().getSummaries().clear();
        
        getEntity().setTotal(BigDecimal.ZERO);

        BigDecimal totalTax = BigDecimal.ZERO;
        
        for( QuoteItem it : getEntity().getItems()){
            
            TaxDefinition tax = it.getCommodity().getTax1();
            if( tax != null ){
                BigDecimal taxAmt = it.getTotal().multiply(tax.getRate());
                totalTax = totalTax.add(taxAmt);
                
                QuoteSummary sm = getEntity().getSummaries().get("Tax." + tax.getCode());
                if( sm == null ){
                    sm = new QuoteSummary();
                    sm.setRowKey("Tax." + tax.getCode());
                    sm.setInfo(tax.getName());
                    sm.setMaster(getEntity());
                    sm.setAmount(taxAmt);
                    getEntity().getSummaries().put( sm.getRowKey(), sm );
                } else {
                    sm.setAmount( sm.getAmount().add( taxAmt ));
                }
            }
            
            tax = it.getCommodity().getTax2();
            if( tax != null ){
                BigDecimal taxAmt = it.getTotal().multiply(tax.getRate());
                totalTax = totalTax.add(taxAmt);
                
                QuoteSummary sm = getEntity().getSummaries().get("Tax." + tax.getCode());
                if( sm == null ){
                    sm = new QuoteSummary();
                    sm.setRowKey("Tax." + tax.getCode());
                    sm.setInfo(tax.getName());
                    sm.setMaster(getEntity());
                    sm.setAmount(taxAmt);
                    getEntity().getSummaries().put( sm.getRowKey(), sm );
                } else {
                    sm.setAmount( sm.getAmount().add( taxAmt ));
                }
            }
            
            tax = it.getCommodity().getTax3();
            if( tax != null ){
                BigDecimal taxAmt = it.getTotal().multiply(tax.getRate());
                totalTax = totalTax.add(taxAmt);
                
                QuoteSummary sm = getEntity().getSummaries().get("Tax." + tax.getCode());
                if( sm == null ){
                    sm = new QuoteSummary();
                    sm.setRowKey("Tax." + tax.getCode());
                    sm.setInfo(tax.getName());
                    sm.setMaster(getEntity());
                    sm.setAmount(taxAmt);
                    getEntity().getSummaries().put( sm.getRowKey(), sm );
                } else {
                    sm.setAmount( sm.getAmount().add( taxAmt ));
                }
            }
                    
            
        }
        
        //Saklamadan hemen önce toplam tutarı hesaplayıp fiş üzerine yazalım.
        getEntity().getItems().stream()
                .forEach(i -> getEntity().setTotal(getEntity().getTotal().add(i.getTotal())));

        QuoteSummary sm = new QuoteSummary();
        sm.setRowKey("TaxTotal");
        sm.setMaster(getEntity());
        sm.setAmount(totalTax);
        getEntity().getSummaries().put( sm.getRowKey(), sm );
        
        sm = new QuoteSummary();
        sm.setRowKey("BeforeTaxTotal");
        sm.setMaster(getEntity());
        sm.setAmount(getEntity().getTotal());
        getEntity().getSummaries().put( sm.getRowKey(), sm );
        
        //Genel Toplam
        sm = new QuoteSummary();
        sm.setRowKey("GrandTotal");
        sm.setMaster(getEntity());
        sm.setAmount(getEntity().getTotal().add(totalTax));
        getEntity().getSummaries().put( sm.getRowKey(), sm );
        
    }
 
    
    public List<QuoteSummary> getTaxes(){
        List<QuoteSummary> result = new ArrayList<>();
        
        getEntity().getSummaries().entrySet().stream()
                .filter(e ->  e.getKey().startsWith("Tax."))
                .forEach(e -> {
                    result.add(e.getValue());
                });
        
        return result;
    }
    
    
    public Class<? extends ViewConfig> createFromFeature( FeaturePointer featurePointer, Contact contact, String processId ){
        Class page = create();
        
        getEntity().setAccount(contact);
        getEntity().setStarter(featurePointer);
        getEntity().setProcessId(processId);
        
        return page;
    }
    
}