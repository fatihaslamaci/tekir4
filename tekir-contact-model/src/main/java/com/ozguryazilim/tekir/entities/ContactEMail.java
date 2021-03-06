package com.ozguryazilim.tekir.entities;

import java.util.Collections;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * E-posta adres girişi.
 * 
 * @author Hakan Uygun
 */
@Entity
@DiscriminatorValue(value = "EMAIL")
public class ContactEMail extends ContactInformation{
    
    /**
     * E-posta adresi
     * @return 
     */
    public String getEmailAddress(){
        return getAddress();
    }
    
    /**
     * E-posta format kontrolü yapılacak.
     * @param email 
     */
    public void setEmailAddress( String email ){
        setAddress(email);
    }
    
    @Override
    public String getCaption(){
        return getEmailAddress();
    }

    @Override
    public String getIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getAcceptedSubTypes() {
        return Collections.emptyList();
    }
}
