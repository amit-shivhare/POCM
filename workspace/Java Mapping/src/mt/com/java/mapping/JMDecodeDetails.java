package mt.com.java.mapping;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class JMDecodeDetails extends AbstractTransformation {

    public void transform(TransformationInput arg0, TransformationOutput arg1) throws StreamTransformationException {
        try {
            Document docOld = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(arg0.getInputPayload().getInputStream());
            // XPath to tag containing Base64 PDF is: /ns0:Base64Transmission/Row/Data
            NodeList details = docOld.getElementsByTagName("FileContentBinaryObject");
            String data = details.item(0).getChildNodes().item(0).getNodeValue();
            byte[] decodedBytes = Base64.decodeBase64(data.getBytes());
            //Write transformed PDF to outputsteam.
            arg1.getOutputPayload().getOutputStream().write(decodedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    



	}

	