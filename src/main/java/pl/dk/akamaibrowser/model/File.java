package pl.dk.akamaibrowser.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class File {

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private int size;

    @XmlAttribute
    private String md5;

    @XmlAttribute
    private int mtime;

    @XmlAttribute
    private int bytes;

    @XmlAttribute
    private int files;

    @XmlAttribute
    private boolean implicit;
}
