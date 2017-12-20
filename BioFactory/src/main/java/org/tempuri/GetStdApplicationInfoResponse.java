
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
 *         &lt;element name="GetStdApplicationInfoResult" type="{http://tempuri.org/}DocResult" minOccurs="0"/>
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
    "getStdApplicationInfoResult"
})
@XmlRootElement(name = "GetStdApplicationInfoResponse")
public class GetStdApplicationInfoResponse {

    @XmlElement(name = "GetStdApplicationInfoResult")
    protected DocResult getStdApplicationInfoResult;

    /**
     * Gets the value of the getStdApplicationInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link DocResult }
     *     
     */
    public DocResult getGetStdApplicationInfoResult() {
        return getStdApplicationInfoResult;
    }

    /**
     * Sets the value of the getStdApplicationInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocResult }
     *     
     */
    public void setGetStdApplicationInfoResult(DocResult value) {
        this.getStdApplicationInfoResult = value;
    }

}
