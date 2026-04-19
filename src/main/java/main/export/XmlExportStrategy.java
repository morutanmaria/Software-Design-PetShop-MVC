package main.export;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

@Component("xml")
public class XmlExportStrategy implements ExportStrategy{

    @Override
    public byte[] export(Object data) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsBytes(data);
    }
}