
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
 *         &lt;element name="GetStdPublicationInfoResult" type="{http://tempuri.org/}DocResult" minOccurs="0"/>
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
    "getStdPublicationInfoResult"
})
@XmlRootElement(name = "GetStdPublicationInfoResponse")
public class GetStdPublicationInfoResponse {

    @XmlElement(name = "GetStdPublicationInfoResult")
    protected DocResult getStdPublicationInfoResult;

    /**
     * Gets the value of the getStdPublicationInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link DocResult }
     *     
     */
    public DocResult getGetStdPublicationInfoResult() {
        return getStdPublicationInfoResult;
    }

    /**
     * Sets the value of the getStdPublicationInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocResult }
     *     
     */
    public void setGetStdPublicationInfoResult(DocResult value) {
        this.getStdPublicationInfoResult = value;
    }

}
