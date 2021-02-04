package pl.dk.akamaibrowser;

import org.apache.tools.ant.filters.StringInputStream;
import org.springframework.stereotype.Component;
import pl.dk.akamaibrowser.model.Stat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Component
class StatUnmarshaller {

    public Stat unmarshall(String string) {
        try {
            JAXBContext context = JAXBContext.newInstance(Stat.class);
            return (Stat) context.createUnmarshaller()
                                 .unmarshal(new StringInputStream(string));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
