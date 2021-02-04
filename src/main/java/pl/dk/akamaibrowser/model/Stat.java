package pl.dk.akamaibrowser.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "stat")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stat {

    @XmlElement(name = "file")
    private List<File> files;
}
