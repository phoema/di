
package com.map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetStdInfoResult" type="{http://tempuri.org/}DocResult" minOccurs="0"/>
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
    "getStdInfoResult"
})
@XmlRootElement(name = "GetStdInfoResponse")
public class GetStdInfoResponse {

    @XmlElement(name = "GetStdInfoResult")
    protected DocResult getStdInfoResult;

    /**
     * ��ȡgetStdInfoResult���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link DocResult }
     *     
     */
    public DocResult getGetStdInfoResult() {
        return getStdInfoResult;
    }

    /**
     * ����getStdInfoResult���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link DocResult }
     *     
     */
    public void setGetStdInfoResult(DocResult value) {
        this.getStdInfoResult = value;
    }

}
