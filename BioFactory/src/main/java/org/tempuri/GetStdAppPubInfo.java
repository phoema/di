
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="isLike" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "country",
    "number",
    "kind",
    "numType",
    "isLike"
})
@XmlRootElement(name = "GetStdAppPubInfo")
public class GetStdAppPubInfo {

    protected String country;
    protected String number;
    protected String kind;
    protected int numType;
    protected boolean isLike;

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the kind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the value of the kind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKind(String value) {
        this.kind = value;
    }

    /**
     * Gets the value of the numType property.
     * 
     */
    public int getNumType() {
        return numType;
    }

    /**
     * Sets the value of the numType property.
     * 
     */
    public void setNumType(int value) {
        this.numType = value;
    }

    /**
     * Gets the value of the isLike property.
     * 
     */
    public boolean isIsLike() {
        return isLike;
    }

    /**
     * Sets the value of the isLike property.
     * 
     */
    public void setIsLike(boolean value) {
        this.isLike = value;
    }

}
