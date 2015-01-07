package matrixconversion.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import matrixconversion.util.StringPattern;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class NeXMLIO extends txtMatrixFileIo {
	public void saveNeXML(String filename, String outfilename,
			HashMap mappingRuleMap, boolean saveAll) throws IOException,
			JDOMException {
		BufferedWriter bw;
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<String> firstColumn = new ArrayList<String>();
		ArrayList<String> contents = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();	
		
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice nice
		xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("UTF-8")); //

		int columnNum = readContents4NeXML(filename, mappingRuleMap,
				saveAll, headers, firstColumn, contents, values);

		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(outfilename);
		Document document;
		FileOutputStream fout = null;// FileWriter
		fout = new FileOutputStream(outfilename);// FileWriter
	//	String out = "<nex:nexml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:nex=\"http://www.nexml.org/2009\" xmlns=\"http://www.nexml.org/2009\" version=\"0.9\" xml:base=\"http://example.org/\" xsi:schemaLocation=\"http://www.nexml.org/2009 ../xsd/nexml.xsd\">";
	//	fout.write(out.getBytes());
		
		Namespace xmlns = Namespace.getNamespace("http://www.nexml.org/2009");
        Namespace nexns = Namespace.getNamespace("nex", "http://www.nexml.org/2009");
        Namespace xsins = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");


        Element nexElement = new Element("nexml", nexns);
        nexElement.addNamespaceDeclaration(xmlns);
        nexElement.addNamespaceDeclaration(xsins);
		
		
	//	Element nexElement = new Element("nex");
		Document doc = new Document(nexElement);

		Element otusElement = new Element("otus");
		otusElement.setAttribute(new Attribute("id", "taxa1"));
		otusElement.setAttribute(new Attribute("label", "Primary taxa block"));
		nexElement.addContent(otusElement);
	//	Document doc = new Document(otusElement);
		for (int i = 0; i < firstColumn.size(); i++) {
			Element otuElement = new Element("otu");
			otuElement.setAttribute("id", firstColumn.get(i));
			otuElement.setAttribute("label", firstColumn.get(i));
			otusElement.addContent(otuElement);
		}

		xmlOutput.output(doc, fout);
		

		Element charactersElement = new Element("characters");
		charactersElement.setAttribute(new Attribute("otus", "taxa1"));
		charactersElement.setAttribute(new Attribute("id", "m1"));
		charactersElement.setAttribute(new Attribute("type", "StandardCells",xsins));
		charactersElement.setAttribute(new Attribute("label",
				"Categorical characters"));
		nexElement.addContent(charactersElement);
	//	doc = new Document(charactersElement);

		Element formatElement = new Element("format");
		charactersElement.addContent(formatElement);
		Element statesElement = new Element("states");
		statesElement.setAttribute(new Attribute("id",
				"StandardCategoricalStateSet1"));
		formatElement.addContent(statesElement);

		int j = 0;
		for (int i = 1; i < headers.size(); i++) {
			SortedMap featureMap = (SortedMap) mappingRuleMap.get(headers
					.get(i));
			Element stateElement;
			if (!(featureMap == null || featureMap.isEmpty())) {
				if (featureMap.size() > 1) {
					stateElement = new Element("polymorphic_state_set");
					stateElement.setAttribute(new Attribute("id", headers
							.get(i)));
					stateElement.setAttribute(new Attribute("symbol", String
							.valueOf(j)));
					j++;
					Iterator it = featureMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pairs = (Map.Entry) it.next();
						Element memberElement = new Element("member");
						memberElement.setAttribute(new Attribute("state",
								(String) pairs.getKey()));
						stateElement.addContent(memberElement);
					}
				} else {
					stateElement = new Element("state");
					stateElement.setAttribute(new Attribute("id", headers
							.get(i)));
					stateElement.setAttribute(new Attribute("symbol", String
							.valueOf(j)));
					j++;
				}
				statesElement.addContent(stateElement);
			}
			/*
			 * else{ stateElement = new Element("state");
			 * stateElement.setAttribute(new Attribute("id",
			 * "StandardCategoricalStateSet1")); stateElement.setAttribute(new
			 * Attribute("symbol", String .valueOf(j))); j++; }
			 */
		}

		for (int i = 1; i < headers.size(); i++) {
			Element charElement = new Element("char");
			charElement.setAttribute(new Attribute("states", headers.get(i)));
			charElement.setAttribute(new Attribute("id", headers.get(i)));
			formatElement.addContent(charElement);
		}

		Element matrixElement = new Element("matrix");
		charactersElement.addContent(matrixElement);

		for (int i = 0; i < firstColumn.size(); i++) {
			Element rowElement = new Element("row");
			rowElement
					.setAttribute(new Attribute("id",
							"StandardCategoricalStateCellsRow"
									+ String.valueOf(i + 1)));
			rowElement.setAttribute(new Attribute("otu", firstColumn.get(i)));

			String rowValues = contents.get(i);
			int idx = rowValues.indexOf("$");
			while (idx >= 0 && idx < rowValues.length()) {
				int idx1 = idx;
				int idx2 = rowValues.indexOf("$", idx + 1);
				if (idx2 < 0)
					idx2 = rowValues.length();
				String charStates = rowValues.substring(idx1 + 1, idx2);
				idx = idx2;
				
				int charIdx = Integer.valueOf(charStates.substring(1,charStates.indexOf("@",1)));
				charStates = charStates.substring(charStates.indexOf("@",1)+1);


				if (charStates.length() > 0) {
			//		System.out.println(charStates);
			//		System.out.println(charIdx);
					SortedMap featureMap = (SortedMap) mappingRuleMap
							.get(headers.get(charIdx));
			//		if (featureMap != null) {
					int idx11 = 0;
					int idx12 =charStates.indexOf("|");
					while (idx12 >= 0 && idx12 < charStates.length()) {
						System.out.println(charStates);
						String state = charStates.substring(idx11, idx12);
						idx11 = idx12 + 1;
						idx12 = charStates.indexOf("|", idx11);
						if (idx12 < 0)
							idx12 = charStates.length();

						Element cellElement = new Element("cell");
						cellElement.setAttribute(new Attribute("char", headers
								.get(charIdx)));
						// cellElement.setAttribute(new
						// Attribute("state",
						// state));
						System.out.println(headers.get(charIdx));
						System.out.println(state);
						cellElement.setAttribute(new Attribute("state",
								(String) featureMap.get(state)));
						rowElement.addContent(cellElement);
					}
					if (idx12 < 0) {
						Element cellElement = new Element("cell");
						cellElement.setAttribute(new Attribute("char", headers
								.get(charIdx)));
						// cellElement.setAttribute(new
						// Attribute("state",
						// charStates));
						cellElement.setAttribute(new Attribute("state",
								(String) featureMap.get(charStates)));

						rowElement.addContent(cellElement);
					}
				}
			}

			/*
			 * for (int k = 0;k<headers.size();k++){ SortedMap featureMap =
			 * (SortedMap) mappingRuleMap.get(headers .get(k)); if (!(featureMap
			 * == null || featureMap.isEmpty())) { Iterator it =
			 * featureMap.entrySet().iterator(); while (it.hasNext()) {
			 * Map.Entry pairs = (Map.Entry) it.next(); Element cellElement =
			 * new Element("cell"); cellElement.setAttribute(new
			 * Attribute("char", headers .get(k))); cellElement.setAttribute(new
			 * Attribute("state", (String) pairs.getKey()));
			 * rowElement.addContent(cellElement); } } }
			 */
			matrixElement.addContent(rowElement);
		}

		xmlOutput.output(doc, fout);
		fout.close();

	}
	
	
	protected int readContents4NeXML(String filename, HashMap mappingRuleMap, boolean saveAll, ArrayList<String> headers, ArrayList<String> firstColumn, ArrayList<String> contents, ArrayList<String> values) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(filename));
        //read the header of table
        String sCurrentLine;
        sCurrentLine = br.readLine();
        int idx = 0;
        int idxStart = 0, idxEnd = 0;
        String item;
        while (idx < sCurrentLine.length()) {
            idxStart = idx;
            idxEnd = sCurrentLine.indexOf("\t", idx);
			if (idxEnd < 0)
				idxEnd = sCurrentLine.length();
            item = sCurrentLine.substring(idxStart, idxEnd);
            headers.add(item);
            idx = idxEnd + 1;
        }

        int columnNum = 0;
        //output table contents
        while ((sCurrentLine = br.readLine()) != null) {
            idx = 0;
            idxStart = 0;
            idxEnd = 0;
            int i = 0;
            columnNum = 0;
            String curRow = "";
            while (idx < sCurrentLine.length() && i < headers.size()) {
                idxStart = idx;
                idxEnd = sCurrentLine.indexOf("\t", idx);
				if (idxEnd < 0)
					idxEnd = sCurrentLine.length();
                item = sCurrentLine.substring(idxStart, idxEnd);
                if (StringPattern.isENum(item)){
                    BigDecimal db = new BigDecimal(item);
                    item = db.toPlainString();
                }
                    
                if (i >= 1) {
                    SortedMap featureMap = (SortedMap) mappingRuleMap.get(headers.get(i));
                    if (featureMap == null || featureMap.isEmpty()) {
                        if (saveAll) {
                          //  curRow = curRow + "?";
                        	curRow = curRow+"$"+item;
                        }
                    } else {
                        columnNum++;
                        if (!(item == null || item.equals(""))) {
                            String value = "";
                            ArrayList curValues = new ArrayList();
                            if (item.indexOf("|") > 0) {
                                int end = 0;
                                String itemleft = item;
                                while (itemleft.indexOf("|") > 0) {
                                    end = itemleft.indexOf("|");
                                    String curItem = itemleft.substring(0, end);
                             //       if (value.length() == 0) {
                                        value = String.valueOf(featureMap.get(curItem));
									if ((!value.equals("null"))
											&& (!value.isEmpty())) {
										if (!values.contains(value)) {
											values.add(value);
										}
										if (!curValues.contains(value))
											curValues.add(value);
									}
                            /*        } else {
                                        if (!values.contains(String.valueOf(featureMap.get(curItem)))) {
                                            values.add((String) String.valueOf(featureMap.get(curItem)));
                                        }
                                        value = value + (String) String.valueOf(featureMap.get(curItem));
                                        if (!curValues.contains(value))
                                        	curValues.add(value);
                                    }*/
                                    itemleft = itemleft.substring(end + 1);
                                }
                                value = String.valueOf(featureMap.get(itemleft));
								if ((!value.equals("null"))
										&& (!value.isEmpty())) {
									if (!values.contains(value)) {
										values.add(value);
									}
									if (!curValues.contains(value))
										curValues.add(value);
								}
                             //   value = "{" + value + (String) String.valueOf(featureMap.get(itemleft)) + "}";

                            } else {
                                value = (String) String.valueOf(featureMap.get(item));
								if ((!value.equals("null"))
										&& (!value.isEmpty())) {
									if (!values.contains(value)) {
										values.add(value);
									}
									if (!curValues.contains(value))
										curValues.add(value);
								}
                            }
                            value="";
                            for (int l = 0;l<curValues.size();l++){
                            	value+= curValues.get(l);
                            }
                            if (curValues.size()>1)
                            	value = "{" + value + "}";
                        //    curRow = curRow + value;
                            curRow = curRow+"$@"+i+"@"+item;

                        } else {
                          //  curRow = curRow + "?";
                        	curRow = curRow+"$@"+i+"@"+item;
                        }
                    }
                } else {
                    firstColumn.add(item);
                    curRow = "";
                }
                i++;
                idx = idxEnd + 1;
            }
            contents.add(curRow);
        }
        br.close();

        if (saveAll)
            return headers.size();
        else
            return columnNum;
    }


}
