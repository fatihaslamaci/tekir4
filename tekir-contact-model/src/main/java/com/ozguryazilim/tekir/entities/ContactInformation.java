package com.ozguryazilim.tekir.entities;

import com.ozguryazilim.tekir.entites.converters.StringListConverter;
import com.ozguryazilim.telve.entities.AuditBase;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * İletişim adresi.
 * 
 * E-posta, telefon, v.b.
 *
 * Tek bir sınıfa koymak yerine miras alınan alt sınıflar mı yapsak?
 * Hem type safe olur, hem de kontroller ( validation ) daha doğru olabilir.
 * 
 * TODO: Bu sınıf abstract olabilir çünkü hiç doğrudan oluşturulmayacak
 * 
 * @author Hakan Uygun
 */
@Entity
@Table( name = "TCC_CONTACT_INFO" )
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "INFO_TYPE")
public abstract class ContactInformation extends AuditBase{

    
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "genericSeq")
    @Column(name = "ID")
    private Long id;
    
    /**
     * Bu adresin kime ait olduğu
     */
    @ManyToOne
    @JoinColumn(name = "CONTACT_ID", foreignKey = @ForeignKey(name = "FK_CONINF_CONTACT"))
    private Contact contact;
    
    
    /**
     * Business, Home, Personal, Default( primary )
     * 
     */
    @Column(name="ROLES")
    @Convert(converter = StringListConverter.class)
    private List<String> roles = new ArrayList<>();
    
    /**
     * Altsınıfların kendi içinde tür ayrımı için : 
     * 
     * Mobile, Fax, Invoice, Shipment, 
     */
    @Column(name="SUBTYPES")
    @Convert(converter = StringListConverter.class)
    private List<String> subTypes = new ArrayList<>();
    
    /**
     * Temel iletişim verisi.
     * Alt sınıfların bir kısmı sadece bunu kullanırken bir kısmı ek alanlar sağlar.
     */
    @Column(name="ADDRESS")
    private String address;

    @Column(name="INFO")
    private String info;
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<String> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(List<String> subTypes) {
        this.subTypes = subTypes;
    }
    
    
    /**
     * Bu tür erişim bilgisi için hangi tiplerin kabul edileceği
     * @return 
     */
    public abstract List<String> getAcceptedSubTypes();
    
    
    /**
     * UI'de ne nasıl gösterilecek?
     * @return 
     */
    public abstract String getCaption();
    
    
    /**
     * Hangi icon'nun gösterileceği. 
     * 
     * altsınıflar kendi ikonlarını çoklayabilirler. Örneğin normal telefon mobil telefon gibi.
     * @return 
     */
    public abstract String getIcon();
    
    
}
