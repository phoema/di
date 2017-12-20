
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StdInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StdInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="STD_PUB_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STD_PUB_NUM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STD_PUB_KIND" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STD_PUB_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STD_APP_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STD_APP_NUM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STD_APP_KIND" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STD_APP_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StdInfo", propOrder = {
    "stdpubcountry",
    "stdpubnum",
    "stdpubkind",
    "stdpubdate",
    "stdappcountry",
    "stdappnum",
    "stdappkind",
    "stdappdate"
})
public class StdInfo {

    @XmlElement(name = "STD_PUB_COUNTRY")
    protected String stdpubcountry;
    @XmlElement(name = "STD_PUB_NUM")
    protected String stdpubnum;
    @XmlElement(name = "STD_PUB_KIND")
    protected String stdpubkind;
    @XmlElement(name = "STD_PUB_DATE")
    protected String stdpubdate;
    @XmlElement(name = "STD_APP_COUNTRY")
    protected String stdappcountry;
    @XmlElement(name = "STD_APP_NUM")
    protected String stdappnum;
    @XmlElement(name = "STD_APP_KIND")
    protected String stdappkind;
    @XmlElement(name = "STD_APP_DATE")
    protected String stdappdate;

    /**
     * Gets the value of the stdpubcountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDPUBCOUNTRY() {
        return stdpubcountry;
    }

    /**
     * Sets the value of the stdpubcountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDPUBCOUNTRY(String value) {
        this.stdpubcountry = value;
    }

    /**
     * Gets the value of the stdpubnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDPUBNUM() {
        return stdpubnum;
    }

    /**
     * Sets the value of the stdpubnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDPUBNUM(String value) {
        this.stdpubnum = value;
    }

    /**
     * Gets the value of the stdpubkind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDPUBKIND() {
        return stdpubkind;
    }

    /**
     * Sets the value of the stdpubkind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDPUBKIND(String value) {
        this.stdpubkind = value;
    }

    /**
     * Gets the value of the stdpubdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDPUBDATE() {
        return stdpubdate;
    }

    /**
     * Sets the value of the stdpubdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDPUBDATE(String value) {
        this.stdpubdate = value;
    }

    /**
     * Gets the value of the stdappcountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDAPPCOUNTRY() {
        return stdappcountry;
    }

    /**
     * Sets the value of the stdappcountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDAPPCOUNTRY(String value) {
        this.stdappcountry = value;
    }

    /**
     * Gets the value of the stdappnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDAPPNUM() {
        return stdappnum;
    }

    /**
     * Sets the value of the stdappnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDAPPNUM(String value) {
        this.stdappnum = value;
    }

    /**
     * Gets the value of the stdappkind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDAPPKIND() {
        return stdappkind;
    }

    /**
     * Sets the value of the stdappkind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDAPPKIND(String value) {
        this.stdappkind = value;
    }

    /**
     * Gets the value of the stdappdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTDAPPDATE() {
        return stdappdate;
    }

    /**
     * Sets the value of the stdappdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTDAPPDATE(String value) {
        this.stdappdate = value;
    }

}
