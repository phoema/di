
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
 *         &lt;element name="GetStdAppPubInfo2Result" type="{http://tempuri.org/}DocResult" minOccurs="0"/>
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
    "getStdAppPubInfo2Result"
})
@XmlRootElement(name = "GetStdAppPubInfo2Response")
public class GetStdAppPubInfo2Response {

    @XmlElement(name = "GetStdAppPubInfo2Result")
    protected DocResult getStdAppPubInfo2Result;

    /**
     * Gets the value of the getStdAppPubInfo2Result property.
     * 
     * @return
     *     possible object is
     *     {@link DocResult }
     *     
     */
    public DocResult getGetStdAppPubInfo2Result() {
        return getStdAppPubInfo2Result;
    }

    /**
     * Sets the value of the getStdAppPubInfo2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocResult }
     *     
     */
    public void setGetStdAppPubInfo2Result(DocResult value) {
        this.getStdAppPubInfo2Result = value;
    }

}
