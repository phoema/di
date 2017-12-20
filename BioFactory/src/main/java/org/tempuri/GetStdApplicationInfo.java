
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="docdb_app_country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docdb_app_num" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docdb_app_kind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "docdbAppCountry",
    "docdbAppNum",
    "docdbAppKind"
})
@XmlRootElement(name = "GetStdApplicationInfo")
public class GetStdApplicationInfo {

    @XmlElement(name = "docdb_app_country")
    protected String docdbAppCountry;
    @XmlElement(name = "docdb_app_num")
    protected String docdbAppNum;
    @XmlElement(name = "docdb_app_kind")
    protected String docdbAppKind;

    /**
     * Gets the value of the docdbAppCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocdbAppCountry() {
        return docdbAppCountry;
    }

    /**
     * Sets the value of the docdbAppCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocdbAppCountry(String value) {
        this.docdbAppCountry = value;
    }

    /**
     * Gets the value of the docdbAppNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocdbAppNum() {
        return docdbAppNum;
    }

    /**
     * Sets the value of the docdbAppNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocdbAppNum(String value) {
        this.docdbAppNum = value;
    }

    /**
     * Gets the value of the docdbAppKind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocdbAppKind() {
        return docdbAppKind;
    }

    /**
     * Sets the value of the docdbAppKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocdbAppKind(String value) {
        this.docdbAppKind = value;
    }

}
