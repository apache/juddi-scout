//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.19 at 09:49:41 PM CDT 
//


package org.apache.ws.scout.model.uddi.v2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validate_values complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validate_values">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{urn:uddi-org:api_v2}businessEntity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v2}businessService" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v2}tModel" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="generic" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validate_values", propOrder = {
    "businessEntity",
    "businessService",
    "tModel"
})
public class ValidateValues {

    protected List<BusinessEntity> businessEntity;
    protected List<BusinessService> businessService;
    protected List<TModel> tModel;
    @XmlAttribute(required = true)
    protected String generic;

    /**
     * Gets the value of the businessEntity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the businessEntity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBusinessEntity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BusinessEntity }
     * 
     * 
     */
    public List<BusinessEntity> getBusinessEntity() {
        if (businessEntity == null) {
            businessEntity = new ArrayList<BusinessEntity>();
        }
        return this.businessEntity;
    }

    /**
     * Gets the value of the businessService property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the businessService property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBusinessService().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BusinessService }
     * 
     * 
     */
    public List<BusinessService> getBusinessService() {
        if (businessService == null) {
            businessService = new ArrayList<BusinessService>();
        }
        return this.businessService;
    }

    /**
     * Gets the value of the tModel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tModel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TModel }
     * 
     * 
     */
    public List<TModel> getTModel() {
        if (tModel == null) {
            tModel = new ArrayList<TModel>();
        }
        return this.tModel;
    }

    /**
     * Gets the value of the generic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneric() {
        return generic;
    }

    /**
     * Sets the value of the generic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneric(String value) {
        this.generic = value;
    }

}
