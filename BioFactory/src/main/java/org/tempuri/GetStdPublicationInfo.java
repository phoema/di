
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
 *         &lt;element name="docdb_pub_country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docdb_pub_num" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docdb_pub_kind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "docdbPubCountry",
    "docdbPubNum",
    "docdbPubKind"
})
@XmlRootElement(name = "GetStdPublicationInfo")
public class GetStdPublicationInfo {

    @XmlElement(name = "docdb_pub_country")
    protected String docdbPubCountry;
    @XmlElement(name = "docdb_pub_num")
    protected String docdbPubNum;
    @XmlElement(name = "docdb_pub_kind")
    protected String docdbPubKind;

    /**
     * Gets the value of the docdbPubCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocdbPubCountry() {
        return docdbPubCountry;
    }

    /**
     * Sets the value of the docdbPubCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocdbPubCountry(String value) {
        this.docdbPubCountry = value;
    }

    /**
     * Gets the value of the docdbPubNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocdbPubNum() {
        return docdbPubNum;
    }

    /**
     * Sets the value of the docdbPubNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocdbPubNum(String value) {
        this.docdbPubNum = value;
    }

    /**
     * Gets the value of the docdbPubKind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocdbPubKind() {
        return docdbPubKind;
    }

    /**
     * Sets the value of the docdbPubKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocdbPubKind(String value) {
        this.docdbPubKind = value;
    }

}
