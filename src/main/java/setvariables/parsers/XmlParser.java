/**
 * Copyright 2019 XEBIALABS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package setvariables.parsers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import setvariables.DynamicVariable;
import setvariables.DynamicVariables;

public class XmlParser {

    
    public static DynamicVariables getVariablesList(String input)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(input)));
        doc.getDocumentElement().normalize();
        DynamicVariables dynamicVars = getElementAttributes(doc);
        return dynamicVars;
    }

    static DynamicVariables getElementAttributes(Document doc) throws XPathExpressionException{
        XPath xPath =  XPathFactory.newInstance().newXPath();
        String expression = "//*[not(*)]";

        List<DynamicVariable> theList = new ArrayList<DynamicVariable>();
        
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        for(int i = 0 ; i < nodeList.getLength(); i++) 
        {
            DynamicVariable dynVar = new DynamicVariable();
            //Set key to string that represents the parent node names and this node name separated by underscores
            dynVar.setKey(getPath(nodeList.item(i)));
            dynVar.setValue(nodeList.item(i).getTextContent());
            dynVar.setType("xlrelease.StringVariable");
            theList.add(dynVar);
        }
    
        DynamicVariables dynamicVars = new DynamicVariables();
        dynamicVars.setVariables(theList);
        return dynamicVars;
    }

    private static String getPath(Node node) {
        
        Node parent = node.getParentNode();
        if (parent == null) {
            return node.getNodeName();
        }
        String path = getPath(parent) + "_" + node.getNodeName();
        return path.replace("#document_", "");
    }

}