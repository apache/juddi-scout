//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.19 at 09:49:41 PM CDT 
//


package org.apache.ws.scout.model.uddi.v2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for keyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="keyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="businessKey"/>
 *     &lt;enumeration value="tModelKey"/>
 *     &lt;enumeration value="serviceKey"/>
 *     &lt;enumeration value="bindingKey"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "keyType")
@XmlEnum
public enum KeyType {

    @XmlEnumValue("businessKey")
    BUSINESS_KEY("businessKey"),
    @XmlEnumValue("tModelKey")
    T_MODEL_KEY("tModelKey"),
    @XmlEnumValue("serviceKey")
    SERVICE_KEY("serviceKey"),
    @XmlEnumValue("bindingKey")
    BINDING_KEY("bindingKey");
    private final String value;

    KeyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KeyType fromValue(String v) {
        for (KeyType c: KeyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}